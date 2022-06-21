package ge.magticom.smpp;

import ge.magticom.smpp.api.SMSLogica;
import ge.magticom.smpp.model.SmsQueue;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

/**
 * Created by zviad on 4/30/18.
 * Sender Runnable Class
 */
public class SenderRunnable implements Runnable {
    Logger logger=Logger.getLogger(SenderRunnable.class);
    private SMSLogica smsLogica;
    private EntityManagerFactory emf = null;
    private EntityManager em = null;
    private static final Long syncFlag = 1L;
    private Boolean stopLoop = false;

    public SenderRunnable() {
    }


    public SenderRunnable(EntityManager em, SMSLogica smsLogica) {
        if (em == null) {
            this.emf = Persistence.createEntityManagerFactory("sms-app");
            this.em = emf.createEntityManager();
        } else {
            this.em = em;
        }
        this.smsLogica = smsLogica;
    }

    @Override
    public void run() {
        emf = Persistence.createEntityManagerFactory("sms-app");
        em = emf.createEntityManager();
        try {
            while (!stopLoop) {
                try {
                    submitMessage();
                } catch (Exception e) {
                    e.printStackTrace();

                    logger.info("Sender error" + e.getMessage());
                    try {
                        Thread.sleep(3000);
                        if (em != null) {
                            em.close();
                        }
                        if (emf != null) {
                            emf.close();
                        }
                        emf = Persistence.createEntityManagerFactory("sms-app");
                        em = emf.createEntityManager();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        } finally {
            if (em != null) {
                em.close();
                emf.close();
            }
        }
    }

    void stop() {
        stopLoop = true;
    }


    private static final String Q_GET_ACTIVE_MESSAGES_FOR_SEND = "select\n" +
            "        id, phone_number, send_date, message_id,\n" +
            "        delivery_date, param, sms_text, sender,\n" +
            "        state_id, is_geo, no_delivery\n" +
            "      from sms_queue q  where     q.state_id=1 LIMIT 100";

    /**
     * method gets Synchronized Portion of phones where and parks as processing
     */
    @SuppressWarnings("unchecked")
    public List<SmsQueue> getMessagesForSend() {
        synchronized (syncFlag) {
            Query query = em.createNativeQuery(Q_GET_ACTIVE_MESSAGES_FOR_SEND, SmsQueue.class);
            List<SmsQueue> smsQueues = query.getResultList();
            updateStatus(smsQueues,  SmsQueue.STATE_ID_PRE_SUBMITTED);
            return smsQueues;
        }
    }


    private static final String Q_UPDATE_SMS_QUEUE_STATUS = "update sms_queue set state_id = ? where id in( %s )";

    /**
     * group update sms_queue status
     */
    private void updateStatus(List<SmsQueue> smsQueueIds, Long statusId) {
        String updateSql = String.format(Q_UPDATE_SMS_QUEUE_STATUS, getMessageIdsFromList(smsQueueIds));
        logger.info(updateSql);
        em.getTransaction().begin();
        em.createNativeQuery(updateSql)
                .setParameter(1, statusId)
                .executeUpdate();
        em.getTransaction().commit();
    }


    /**
     * get String param From Longs List
     */
    private String getMessageIdsFromList(List<SmsQueue> smsQueues) {
        StringBuilder sb = new StringBuilder("-1111");
        smsQueues.forEach(item -> {
            sb.append(",");
            sb.append(item.getId());
        });
        return sb.toString();
    }

    /**
     * Submit top 100 messages
     */
    private void submitMessage() {
        List<SmsQueue> smsQueues = getMessagesForSend();
        smsQueues.forEach(item -> {
            try {
                smsLogica.submitMessageSimple((int) item.getId(), item.getPhoneNumber(), item.getSmsText(), item.getSender(),
                        item.getIsGeo().intValue(), (item.getNoDelivery() > 0) ? 0 : 1, false);
                em.getTransaction().begin();
                item.setSendDate(new Timestamp(Calendar.getInstance().getTime().getTime()));
                item.setStateId(SmsQueue.STATE_ID_SUBMITTED);
                em.merge(item);
                em.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        if(smsQueues.size()==0){
            try {
                Thread.sleep(5000);
                logger.info("No messages For Send ===== waiting fo 5 second");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        em.clear();
    }


}
