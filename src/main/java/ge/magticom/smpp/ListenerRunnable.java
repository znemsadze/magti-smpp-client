package ge.magticom.smpp;

import ge.magticom.smpp.api.SMSLogica;
import ge.magticom.smpp.model.SmsQueue;
import org.apache.log4j.Logger;
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
                                if (req.getMessageState() == 2) {
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

}
