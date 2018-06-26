package ge.magticom.smpp.model;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zviad on 4/30/18.
 * Senders Model
 */
@Entity
@Table(name = "sms_senders", schema = "public" )
public class SmsSenders {
    private long id;
    private String sender;
    private Long userId;
    private Long stateId;
    private Long createUserId;
    private Timestamp createDate;
    private Long removeUserId;
    private Timestamp removeDate;

    @Id
    @Column(name = "id", nullable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "sender", nullable = true, length = 50)
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
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
    @Column(name = "state_id", nullable = true)
    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    @Basic
    @Column(name = "create_user_id", nullable = true)
    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    @Basic
    @Column(name = "create_date", nullable = true)
    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    @Basic
    @Column(name = "remove_user_id", nullable = true)
    public Long getRemoveUserId() {
        return removeUserId;
    }

    public void setRemoveUserId(Long removeUserId) {
        this.removeUserId = removeUserId;
    }

    @Basic
    @Column(name = "remove_date", nullable = true)
    public Timestamp getRemoveDate() {
        return removeDate;
    }

    public void setRemoveDate(Timestamp removeDate) {
        this.removeDate = removeDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SmsSenders that = (SmsSenders) o;

        if (id != that.id) return false;
        if (sender != null ? !sender.equals(that.sender) : that.sender != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (stateId != null ? !stateId.equals(that.stateId) : that.stateId != null) return false;
        if (createUserId != null ? !createUserId.equals(that.createUserId) : that.createUserId != null) return false;
        if (createDate != null ? !createDate.equals(that.createDate) : that.createDate != null) return false;
        if (removeUserId != null ? !removeUserId.equals(that.removeUserId) : that.removeUserId != null) return false;
        if (removeDate != null ? !removeDate.equals(that.removeDate) : that.removeDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (sender != null ? sender.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (stateId != null ? stateId.hashCode() : 0);
        result = 31 * result + (createUserId != null ? createUserId.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (removeUserId != null ? removeUserId.hashCode() : 0);
        result = 31 * result + (removeDate != null ? removeDate.hashCode() : 0);
        return result;
    }
}
