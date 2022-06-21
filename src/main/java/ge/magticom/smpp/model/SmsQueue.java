package ge.magticom.smpp.model;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zviad on 4/30/18.
 * Sms queue model
 */
@Entity
@Table(name = "sms_queue"  )
public class SmsQueue {
    public static final Long STATE_ID_PRE_SUBMITTED = 7L;
    public static final Long STATE_ID_SUBMITTED = 2L;
    public static final Long STATE_ID_SENT = 3L;
    public static final Long STATE_ID_DELIVERRED = 4L;
    public static final Long STATE_ID_BLOCKED = 5L;
    public static final Long STATE_ID_SMS_CENTER_FAIL = 8L;
    public static final Long CHARGE_STATE_UNPRCESSED=0L;
    public static final Long CHARGE_STATE_CHARGED=1L;
    public static final Long CHARGE_STATE_CHARGE_FREE=2L;
    private long id;
    private String phoneNumber;
    private Timestamp sendDate;
    private String messageId;
    private Timestamp deliveryDate;
    private String param;
    private Long stateId;
    private String smsText;
    private String sender;
    private Long isGeo;
    private Long noDelivery;

    @Id
    @Column(name = "id", nullable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "phone_number", nullable = true, length = 15)
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Basic
    @Column(name = "send_date", nullable = true)
    public Timestamp getSendDate() {
        return sendDate;
    }

    public void setSendDate(Timestamp sendDate) {
        this.sendDate = sendDate;
    }

    @Basic
    @Column(name = "message_id", nullable = true, length = 30)
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Basic
    @Column(name = "delivery_date", nullable = true)
    public Timestamp getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Timestamp deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    @Basic
    @Column(name = "param", nullable = true, length = 2000)
    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }


    @Basic
    @Column(name = "sms_text" ,insertable = false,updatable = false)
    public String getSmsText() {
        return smsText;
    }

    public void setSmsText(String smsText) {
        this.smsText = smsText;
    }

    @Basic
    @Column(name = "sender" ,insertable = false,updatable = false)
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }







    @Basic
    @Column(name =  "state_id")
    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    @Basic
    @Column(name = "is_geo",insertable = false,updatable = false)
    public Long getIsGeo() {
        return isGeo;
    }

    public void setIsGeo(Long isGeo) {
        this.isGeo = isGeo;
    }


    @Basic
    @Column(name = "no_delivery" ,insertable = false,updatable = false)
    public Long getNoDelivery() {
        return noDelivery;
    }

    public void setNoDelivery(Long noDelivery) {
        this.noDelivery = noDelivery;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SmsQueue smsQueue = (SmsQueue) o;

        if (id != smsQueue.id) return false;
        if (phoneNumber != null ? !phoneNumber.equals(smsQueue.phoneNumber) : smsQueue.phoneNumber != null)
            return false;
        if (sendDate != null ? !sendDate.equals(smsQueue.sendDate) : smsQueue.sendDate != null) return false;
        if (messageId != null ? !messageId.equals(smsQueue.messageId) : smsQueue.messageId != null) return false;
        if (deliveryDate != null ? !deliveryDate.equals(smsQueue.deliveryDate) : smsQueue.deliveryDate != null)
            return false;
        if (param != null ? !param.equals(smsQueue.param) : smsQueue.param != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (sendDate != null ? sendDate.hashCode() : 0);
        result = 31 * result + (messageId != null ? messageId.hashCode() : 0);
        result = 31 * result + (deliveryDate != null ? deliveryDate.hashCode() : 0);
        result = 31 * result + (param != null ? param.hashCode() : 0);
        return result;
    }



    @Override
    public String toString() {
        return "\nSmsQueue{" +
                "id=" + id +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", sendDate=" + sendDate +
                ", messageId='" + messageId + '\'' +
                ", deliveryDate=" + deliveryDate +
                ", param='" + param + '\'' +
                ", smsText='" + smsText + '\'' +
                ", sender='" + sender + '\'' +
                '}';
    }
}
