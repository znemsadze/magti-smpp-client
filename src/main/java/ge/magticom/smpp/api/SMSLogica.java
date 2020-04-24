package ge.magticom.smpp.api;


import ge.magticom.smpp.utils.myutils;
import org.apache.log4j.Logger;
import org.smpp.Data;
import org.smpp.ServerPDUEvent;
import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.smpp.pdu.*;
import org.smpp.util.ByteBuffer;
import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class SMSLogica extends Thread {

    Logger logger=Logger.getLogger(SMSLogica.class);

    public static Properties props;
    public static int Transmit = 0;
    public static int Receive = 1;
    public static int Trancieve = 2;

    public Session iSession;
    public SMSReceiver iReceiver;

    public IMessageListener iListener;
    public String iSMSCAddr;
    public int iSMSCPort;
    public int iSMSCFPort;
    public String iSMSCUser;
    public String iSMSCPassword;
    public String iSystemType;
    public String iSourcePhoneNumber;
    public boolean iTerminating;

    public long iEnquireInterval;
    public long iEnquireTimeout;
    public boolean iWasConnected;
    public long iLastCommReceived;
    public long iSubmitInterval;

    public AtomicInteger nextSid;

    public boolean connected;

    public boolean isFixSender;

    private Queue<ServerPDUEvent> eventQueue;


    public SMSLogica(Integer chanelId, Boolean fixSender ) {
        props = new Properties();
        this.isFixSender=fixSender;
        try {
            props.load(SMSLogica.class.getClassLoader().getResourceAsStream("config.properties"));
            // props.load(new FileInputStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        connected = false;
        nextSid = new AtomicInteger(1);
        iListener = null;
        iSMSCAddr = (String) props.get("SMSCAddress" + chanelId);
        iSMSCPort = Integer.parseInt((String) props.get("SMSCPort"));
        iSMSCFPort = Integer.parseInt((String) props.get("SMSCFport"));
        iSMSCUser = (String) props.get("SMSCUser"+chanelId) ;
        iSMSCPassword = (String) props.get("SMSCPassword"+ chanelId);
        iSystemType = (String) props.get("SystemType");
        iSourcePhoneNumber = (String) props.get("SourcePhoneNumber");
        iEnquireInterval = Long.parseLong((String) props.get("EnquireInterval"));
        iEnquireTimeout = Long.parseLong((String) props.get("EnquireTimeout"));
        iWasConnected = false;
        iSubmitInterval = Long.parseLong((String) props.get("SubmitInterval"));
        iEnquireInterval = 1;
        eventQueue = new LinkedList<>();
        logger.info("serverUser==" + Thread.currentThread().getId() + "==" + iSMSCAddr + ":" + iSMSCUser);
    }

    private static String[] splitMessage(String value, int chunkLen) {
        int cnt = (value.length() % chunkLen == 0) ? (value.length() / chunkLen) : (value.length() / chunkLen + 1);

        String[] a = new String[cnt];
        int k = 0;
        while (!value.isEmpty()) {
            if (chunkLen < value.length()) {
                a[k] = value.substring(0, chunkLen);
                value = value.substring(chunkLen);
            } else {
                a[k] = value;
                value = "";
            }
            k++;
        }
        return a;

    }

    public synchronized boolean isConnected() {
        return connected;
    }

    public synchronized void setConnected(boolean aConnected) {
        connected = aConnected;
    }

    public void run() {

    }

    synchronized public void sendEnquire() throws Exception {
        EnquireLink msg = new EnquireLink();

        msg.setSequenceNumber(getNextSequenceID());

        iSession.enquireLink(msg);
        logger.info("Sent EnquireLink. threadId="+Thread.currentThread().getId()+"ipaddress="+iSession.getConnection().getAddress());
    }

    public void shutdown() {
        iTerminating = true;
        interrupt();

        try {
            this.join(3000);
        } catch (InterruptedException iex) {

        }
    }

    synchronized public void reBind() throws Exception {

        logger.info(myutils.dateToStrTm(Calendar.getInstance().getTime())+"================================(Re)binding..." + Thread.currentThread().getId());
        if (iReceiver != null || iSession != null) {
            logger.info(myutils.dateToStrTm(Calendar.getInstance().getTime())+"Already bound - unbinding...start"+iSession.getConnection().getAddress());
            unBind();

            logger.info(myutils.dateToStrTm(Calendar.getInstance().getTime())+"Already bound - unbinding...end"+iSession.getConnection().getAddress());

            logger.info("Waiting 90 sec...");
            try {
                sleep(90000);
            } catch (InterruptedException intex) {

            }
            logger.info("Waiting comeplete...");
        }

        BindRequest request = null;
        BindResponse response = null;

        request = new BindTransciever();

        logger.info("Establishing TCP connection to " + (isFixSender?iSMSCFPort:iSMSCPort )+ ":" + (isFixSender?iSMSCFPort:iSMSCPort));
        TCPIPConnection connection = new TCPIPConnection(iSMSCAddr, (isFixSender?iSMSCFPort:iSMSCPort));
        connection.setReceiveTimeout( 20*1000);
        logger.info("Connected to SMSC.");

        iSession = new Session(connection);

        logger.info(iSMSCUser + "  " + iSMSCPassword + "  " + iSystemType + "   " + iSourcePhoneNumber);

        // set values
        request.setSystemId(iSMSCUser);
        request.setPassword(iSMSCPassword);
        request.setSystemType(iSystemType);
        request.setInterfaceVersion((byte) 0x34);

        request.setAddressRange((byte) 1, (byte) 1, iSourcePhoneNumber);
        // send the request
        iReceiver = new SMSReceiver(iSession);
        response = iSession.bind(request, iReceiver);

        logger.info("Bind response: " + response.debugString());

        if (response.getCommandStatus() != Data.ESME_ROK) {
            logger.info("Bind failed!");
            iReceiver = null;
        } else {
            iLastCommReceived = System.currentTimeMillis();
            iWasConnected = true;
            setConnected(true);
        }

    }

    public void unBind() throws Exception {
        logger.info("Unbinding... ");
        setConnected(false);

        if (iReceiver == null) {
            logger.info("Already unbounded - unable to unbound");
            // return;
        }

        if (iSession.getReceiver().isReceiver()) {
            logger.info("Stopping receiver...");
        }

        try {
            UnbindResp response = iSession.unbind();
            logger.info("Response unbinded " + response.debugString());
        } catch (Exception ex) {
            logger.info( "Unbind failed", ex);
        }

        try {
            iSession.getConnection().close();
        } catch (Exception ex) {
            logger.info( "Close connection failed", ex);
        }

        iReceiver = null;
        logger.info("Unbinding finished!");

    }

    synchronized public void submitMessage(int aSessionID, int aSubscriberID, String aPhone, String aMessage)
            throws Exception {

        sleep(iSubmitInterval);
        SubmitSM msg = new SubmitSM();
        msg.setSequenceNumber(getNextSequenceID());
        msg.setSourceAddr((byte) 0, (byte) 1, iSourcePhoneNumber);
        msg.setDestAddr((byte) 1, (byte) 1, "995" + aPhone);
        msg.setShortMessage(aMessage);
        msg.setValidityPeriod("000001000000000R");
        msg.setRegisteredDelivery((byte) 1);
        msg.setDestAddrSubunit((byte) 1);

        try {
            iSession.submit(msg);
            if (iListener != null) {
//				logger.infost("Sending message to " + aPhone);
                iListener.registerSequenceID(aSessionID, aSubscriberID, aPhone, msg.getSequenceNumber());
            } else {
//                logger.info("Listener missing for SubmitSM!");
            }
        } catch (Exception ex) {
            reBind();
            throw ex;
        }
    }


    public void submitMessageSimple(int uniId, String aPhone, String aMessage, String from, Integer isGeo, Integer registartDel
            ,Boolean isFlashMessage )
            throws Exception {
        byte ton = 0x05;
        byte npi = 0x00;
        try {
            int smsLen = (isGeo == 0) ? 150 : 50;
            if(isFlashMessage){
                aMessage=from+"\n "+aMessage;
            }

            String smsEnc = (isGeo == 0) ? Data.ENC_ASCII : Data.ENC_UTF16 ;
            int encLen = (isGeo == 0) ? 1 : 8;
            if(isFlashMessage ){
                smsEnc= Data.ENC_UTF16;
                encLen=24;
                smsLen=60;

            }
            String smsparts[] = splitMessage(aMessage, smsLen);
            Integer smsCnt = smsparts.length;
            if (smsCnt < 1) {
                smsCnt = 1;
            }
            sleep(smsCnt * iSubmitInterval*(isFixSender?3:1));
            if (aMessage.length() <= smsLen) {
                SubmitSM msg = new SubmitSM();
                msg.setSequenceNumber(uniId);
                msg.setSourceAddr(ton, npi, from);
                msg.setDestAddr((byte) 1, (byte) 1, "995" + aPhone);
                msg.setShortMessage(aMessage, smsEnc);
                msg.setValidityPeriod("000001000000000R");
                msg.setDataCoding((byte) encLen);
                if (registartDel.intValue() == 1) {
                    msg.setRegisteredDelivery((byte) registartDel.intValue());
                }
                iSession.submit(msg);
            } else {
                for (int i = 0; i < smsparts.length; i++) {
                    SubmitSM msg = new SubmitSM();
                    msg.setEsmClass((byte) Data.SM_UDH_GSM); //Set UDHI Flag Data.SM_UDH_GSM=0x40

                    ByteBuffer ed = new ByteBuffer();

                    ed.appendByte((byte) 5); // UDH Length

                    ed.appendByte((byte) 0x00); // IE Identifier

                    ed.appendByte((byte) 3); // IE Data Length

                    ed.appendByte((byte) uniId); //Reference Number

                    ed.appendByte((byte) smsparts.length); //Number of pieces

                    ed.appendByte((byte) (i + 1)); //Sequence number

                    //This encoding comes in Logica Open SMPP. Refer to its docs for more detail
                    ed.appendString(smsparts[i], smsEnc);

                    msg.setShortMessageData(ed);
                    msg.setSequenceNumber(uniId);
                    msg.setSourceAddr(ton, npi, from);
                    msg.setDestAddr((byte) 1, (byte) 1, "995" + aPhone);
                    msg.setValidityPeriod("000001000000000R");
                    msg.setDataCoding((byte)encLen);
                    if (registartDel.intValue() == 1) {
                        msg.setRegisteredDelivery((byte) registartDel.intValue());
                    }
                    iSession.submit(msg);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            reBind();
            throw ex;
        }
    }

    private int getNextSequenceID() {
        return nextSid.incrementAndGet();
    }

    synchronized public void setListener(IMessageListener aListener) {
        iListener = aListener;
    }

    public Queue<ServerPDUEvent> getEventQueue() {
        return eventQueue;
    }

    public void setEventQueue(Queue<ServerPDUEvent> eventQueue) {
        this.eventQueue = eventQueue;
    }





}
