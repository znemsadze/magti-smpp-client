package ge.magticom.smpp;

import ge.magticom.smpp.api.SMSLogica;
import ge.magticom.smpp.model.SmsQueue;
import org.apache.log4j.Logger;
import org.smpp.Data;
import org.smpp.ServerPDUEvent;
import org.smpp.pdu.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Created by zviad on 5/1/18.
 * Listener Runnable Class for listening smpp events Smpp
 *
 */
public class ListenerRunnable implements Runnable {

    Logger logger=Logger.getLogger(ListenerRunnable.class);

    private SMSLogica smsLogica;
    private String smsCenterId = "";
    private Boolean stopLoop = false;
    private EntityManager em;
    private EntityManagerFactory emf;

    public ListenerRunnable(SMSLogica smsLogica, EntityManager em) {
        this.smsLogica = smsLogica;
        if (em == null) {
            this.emf = Persistence.createEntityManagerFactory("sms-app");
            this.em = emf.createEntityManager();
        } else {
            this.em = em;
        }
    }

    @Override
    public void run() {
        try {
            smsCenterId = "C" + smsLogica.iSMSCAddr.substring(smsLogica.iSMSCAddr.length() - 3, smsLogica.iSMSCAddr.length());
            emf = Persistence.createEntityManagerFactory("sms-app");
            em = emf.createEntityManager();
            while (!stopLoop) {
                try {
                    handleIncomingMessages();
                } catch (Exception e) {
                    try {
                        if (em != null) {
                            em.close();
                        }
                        if (emf != null) {
                            emf.close();
                        }
                        emf = Persistence.createEntityManagerFactory("sms-app");
                        em = emf.createEntityManager();
                        smsLogica.setConnected(false);
                        smsLogica.reBind();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                    logger.info(e.getMessage());
                }
            }
        } finally {
            if (em != null) {
                em.close();
                emf.close();
            }
        }
    }

    private synchronized void handleIncomingMessages() throws Exception {
        if (smsLogica == null || smsLogica.iReceiver == null ||
                smsLogica.iSession == null || !smsLogica.iSession.isBound()
                || !smsLogica.iSession.isOpened()) {
            assert smsLogica != null;
            if (smsLogica.iWasConnected)
                smsLogica.setConnected(false);
            smsLogica.reBind();
        } else {
            smsLogica.sendEnquire();
        }
        int c = smsLogica.iReceiver.getWaitingCount();
        if (c > 0) {
            for (int i = 0; i < c; i++) {
                ServerPDUEvent e = smsLogica.iReceiver.getRequestEvent(100);
                if (e != null) {
                    PDU pdu = e.getPDU();
                    if (pdu != null) {
                        if (pdu.isRequest()) {
                            // Handle request
                            Response response = ((Request) pdu).getResponse();
                            // respond with default response
                            smsLogica.iSession.respond(response);
                            // Decode request
                            if (pdu instanceof DeliverSM) {
                                DeliverSM req = ((DeliverSM) pdu);
                                String smsText = req.getShortMessage(Data.ENC_UTF16);
                                logger.debug(smsText);
                                logger.debug(req.getSourceAddr().getAddress());
                                logger.debug(req.getDestAddr().getAddress());
                                if (smsText!=null &&   smsText.equals(trans(smsText, 55))) {
                                    smsText = req.getShortMessage(Data.ENC_ASCII);
                                    logger.info(smsText);
                                }
                                if (smsText != null && smsText.length() > 0 && req.getSourceAddr().getAddress() != null &&
                                        req.getSourceAddr() != null && req.getSourceAddr().getAddress().length() > 0 &&
                                        req.getDestAddr() != null && req.getDestAddr().getAddress() != null
                                        && req.getDestAddr().getAddress().length()>0
                                ) {
                                    logger.info(req.debugString());
                                    saveMoSmsQueue(null , req.getSourceAddr().getAddress(),
                                            req.getDestAddr().getAddress(), smsText);
                                }else if (req.getMessageState() == 2) {
                                    saveDelivery(req.getReceiptedMessageId(), SmsQueue.STATE_ID_DELIVERRED);
                                } else if (req.getMessageState() > 2) {
                                    saveDelivery(req.getReceiptedMessageId(), SmsQueue.STATE_ID_SMS_CENTER_FAIL);
                                }
                            }
                        } else if (pdu.isResponse() && pdu instanceof SubmitSMResp) {
                            SubmitSMResp res = (SubmitSMResp) pdu;
                            saveSubmitSm(res.getMessageId(), (long) res.getSequenceNumber());
                        }
                    }
                }
            }
            smsLogica.iLastCommReceived = System.currentTimeMillis();
        } else {
            logger.info(Thread.currentThread().getId() + "Listener waiting for events 2 second");
            Thread.sleep(2000);
        }
    }

    private static final String Q_SET_SMS_CENTE_MESSAGEID = "update sms_queue set message_id=?   ,state_id=? ," +
            "send_date=current_date  where id=?  and  message_id is null";

    /**
     * Receive submit SM
     */
    public void saveSubmitSm(String messageId, Long queueId) {
        em.getTransaction().begin();
        em.createNativeQuery(Q_SET_SMS_CENTE_MESSAGEID).
                setParameter(1, smsCenterId + messageId).
                setParameter(2, SmsQueue.STATE_ID_SENT).
                setParameter(3, queueId).
                executeUpdate();
        em.getTransaction().commit();
    }


    public void saveMoSmsQueue(String messageId, String fromPhone, String destUserIdentifier, String text) {
        logger.info("======================================");
        logger.info(messageId+" "+fromPhone+" "+ destUserIdentifier+" "+text+" ===============");
        logger.info("=======================================");
    }

    private static final String Q_MARK_SMS_AS_DELIVERED = "update sms_queue set state_id=? , " +
            "delivery_date=current_date WHERE message_id=? and delivery_date is null ";

    /**
     * Receive Delivery
     */
    public void saveDelivery(String messageId, Long status) {
        em.getTransaction().begin();
        em.createNativeQuery(Q_MARK_SMS_AS_DELIVERED).
                setParameter(1, status).
                setParameter(2, smsCenterId + messageId).
                executeUpdate();
        em.getTransaction().commit();
    }

    void stop() {
        stopLoop = true;
    }


    public static boolean strisemty(String s) {
        try {
            return ((s == null) || (s.trim().equals("")));
        } catch (Exception e) {
            return true;
        }

    }

    public static String trans(String s, Integer v) {
        if (strisemty(s))
            return "";
        StringBuilder sb = new StringBuilder(s);
        StringBuilder sh1 = new StringBuilder();
        StringBuilder sh2 = new StringBuilder();
        switch (v) {
            case 12:
                sh1.insert(0, "ÀÁÂÃÄÅÆÈÉÊËÌÍÏÐÑÒÓÔÖ×ØÙÚÛÜÝÞßàáãä");
                sh2.insert(0, "abgdevzTiklmnopJrstufqRySCcZwWxjh");
                break;
            case 21:
                sh2.insert(0, "ÀÁÂÃÄÅÆÈÉÊËÌÍÏÐÑÒÓÔÖ×ØÙÚÛÜÝÞßàáãä");
                sh1.insert(0, "abgdevzTiklmnopJrstufqRySCcZwWxjh");
                break;
            case 13:
                sh1.insert(0, "ÀÁÂÃÄÅÆÈÉÊËÌÍÏÐÑÒÓÔÖ×ØÙÚÛÜÝÞßàáãä");
                sh2.insert(0, "აბგდევზთიკლმნოპჟრსტუფქღყშჩცძწჭხჯჰ");
                break;
            case 31:
                sh2.insert(0, "ÀÁÂÃÄÅÆÈÉÊËÌÍÏÐÑÒÓÔÖ×ØÙÚÛÜÝÞßàáãä");
                sh1.insert(0, "აბგდევზთიკლმნოპჟრსტუფქღყშჩცძწჭხჯჰ");
                break;
            case 23:
                sh1.insert(0, "abgdevzTiklmnopJrstufqRySCcZwWxjh");
                sh2.insert(0, "აბგდევზთიკლმნოპჟრსტუფქღყშჩცძწჭხჯჰ");
                break;
            case 32:
                sh2.insert(0, "abgdevzTiklmnopJrstufqRySCcZwWxjh");
                sh1.insert(0, "აბგდევზთიკლმნოპჟრსტუფქღყშჩცძწჭხჯჰ");
                break;
            case 44:
                sh1.insert(0, "აობვგღდჯძეჟზიკქყლმნპფრსთტუხცწჩჭშჰ");
                sh2.insert(0, "ააგდეეზზზკლმნოოორსტუუქღყყჩცძძჭჭჯჰ");
                break;
            case 55:
                sh1.insert(0, "აობვგღდჯძეჟზიკქყლმნპფრსთტუხცწჩჭშჰйцуекенгшщзхъфывпаролджэячсмитьбюЙЦКУЕГШЩЗХЪЖЭЛРПАЫВФЯЧСМИТЬБЮ");
                sh2.insert(0, "abgdevzTiklmnopJrstufqRySCcZwWxjh______________________________________________________________");
                break;
            default:
                break;
        }

        for (int i = 0; i < s.length(); i++) {
            if (sh1.indexOf(s.substring(i, i + 1)) > -1) {
                sb.replace(
                        i,
                        i + 1,
                        sh2.substring(sh1.indexOf(sb.substring(i, i + 1)),
                                sh1.indexOf(sb.substring(i, i + 1)) + 1));
            }
        }
        return sb.toString();
    }
}
