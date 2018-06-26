package ge.magticom.smpp.model;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zviad on 4/30/18.
 * Model For Sms text
 */
@Entity
@Table(name = "sms_info" )
public class SmsInfo {
    private long id;
    private String smsText;
    private Long userId;
    private Long typeId;
    private Timestamp sendDate;
    private Long stateId;
    private Long isGeo;
    private Long smsCount;
    private Long deliverCount;
    private Integer noDeliver;
    private Timestamp recDate;
    private String toPhone;

    @Id
    @Column(name = "id", nullable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "sms_text", nullable = true, length = 2000)
    public String getSmsText() {
        return smsText;
    }

    public void setSmsText(String smsText) {
        this.smsText = smsText;
    }

    @Basic
    @Column(name = "user_id", nullable = true)
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "type_id", nullable = true)
    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
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
    @Column(name = "state_id", nullable = true)
    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    @Basic
    @Column(name = "is_geo", nullable = true)
    public Long getIsGeo() {
        return isGeo;
    }

    public void setIsGeo(Long isGeo) {
        this.isGeo = isGeo;
    }

    @Basic
    @Column(name = "sms_count", nullable = true)
    public Long getSmsCount() {
        return smsCount;
    }

    public void setSmsCount(Long smsCount) {
        this.smsCount = smsCount;
    }

    @Basic
    @Column(name = "deliver_count", nullable = true)
    public Long getDeliverCount() {
        return deliverCount;
    }

    public void setDeliverCount(Long deliverCount) {
        this.deliverCount = deliverCount;
    }

    @Basic
    @Column(name = "no_deliver", nullable = true)
    public Integer getNoDeliver() {
        return noDeliver;
    }

    public void setNoDeliver(Integer noDeliver) {
        this.noDeliver = noDeliver;
    }

    @Basic
    @Column(name = "rec_date", nullable = true)
    public Timestamp getRecDate() {
        return recDate;
    }

    public void setRecDate(Timestamp recDate) {
        this.recDate = recDate;
    }

    @Basic
    @Column(name = "to_phone", nullable = true, length = 20)
    public String getToPhone() {
        return toPhone;
    }

    public void setToPhone(String toPhone) {
        this.toPhone = toPhone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SmsInfo smsInfo = (SmsInfo) o;

        if (id != smsInfo.id) return false;
        if (smsText != null ? !smsText.equals(smsInfo.smsText) : smsInfo.smsText != null) return false;
        if (userId != null ? !userId.equals(smsInfo.userId) : smsInfo.userId != null) return false;
        if (typeId != null ? !typeId.equals(smsInfo.typeId) : smsInfo.typeId != null) return false;
        if (sendDate != null ? !sendDate.equals(smsInfo.sendDate) : smsInfo.sendDate != null) return false;
        if (stateId != null ? !stateId.equals(smsInfo.stateId) : smsInfo.stateId != null) return false;
        if (isGeo != null ? !isGeo.equals(smsInfo.isGeo) : smsInfo.isGeo != null) return false;
        if (smsCount != null ? !smsCount.equals(smsInfo.smsCount) : smsInfo.smsCount != null) return false;
        if (deliverCount != null ? !deliverCount.equals(smsInfo.deliverCount) : smsInfo.deliverCount != null)
            return false;
        if (noDeliver != null ? !noDeliver.equals(smsInfo.noDeliver) : smsInfo.noDeliver != null) return false;
        if (recDate != null ? !recDate.equals(smsInfo.recDate) : smsInfo.recDate != null) return false;
        if (toPhone != null ? !toPhone.equals(smsInfo.toPhone) : smsInfo.toPhone != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (smsText != null ? smsText.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (typeId != null ? typeId.hashCode() : 0);
        result = 31 * result + (sendDate != null ? sendDate.hashCode() : 0);
        result = 31 * result + (stateId != null ? stateId.hashCode() : 0);
        result = 31 * result + (isGeo != null ? isGeo.hashCode() : 0);
        result = 31 * result + (smsCount != null ? smsCount.hashCode() : 0);
        result = 31 * result + (deliverCount != null ? deliverCount.hashCode() : 0);
        result = 31 * result + (noDeliver != null ? noDeliver.hashCode() : 0);
        result = 31 * result + (recDate != null ? recDate.hashCode() : 0);
        result = 31 * result + (toPhone != null ? toPhone.hashCode() : 0);
        return result;
    }
}
