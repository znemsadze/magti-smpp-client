package ge.magticom.smpp.model;

import javax.persistence.*;

/**
 * Created by zviad on 4/30/18.
 * Sms State Model
 */
@Entity
@Table(name = "sms_state"  )
public class SmsState {
    private long id;
    private String name;
    private String description;

    @Id
    @Column(name = "id", nullable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name", nullable = true, length = 200)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "description", nullable = true, length = 200)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SmsState smsState = (SmsState) o;

        if (id != smsState.id) return false;
        if (name != null ? !name.equals(smsState.name) : smsState.name != null) return false;
        if (description != null ? !description.equals(smsState.description) : smsState.description != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
