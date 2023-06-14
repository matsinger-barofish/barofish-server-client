package com.matsinger.barofishserver.verification;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class Verification {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "target", nullable = false, length = 300)
    private String target;
    @Basic
    @Column(name = "verification_unmber", nullable = false, length = 6)
    private String verificationUnmber;
    @Basic
    @Column(name = "expired_at", nullable = true)
    private Timestamp expiredAt;
    @Basic
    @Column(name = "create_at", nullable = false)
    private Timestamp createAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getVerificationUnmber() {
        return verificationUnmber;
    }

    public void setVerificationUnmber(String verificationUnmber) {
        this.verificationUnmber = verificationUnmber;
    }

    public Timestamp getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Timestamp expiredAt) {
        this.expiredAt = expiredAt;
    }

    public Timestamp getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Timestamp createAt) {
        this.createAt = createAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Verification that = (Verification) o;
        return id == that.id &&
                Objects.equals(target, that.target) &&
                Objects.equals(verificationUnmber, that.verificationUnmber) &&
                Objects.equals(expiredAt, that.expiredAt) &&
                Objects.equals(createAt, that.createAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, target, verificationUnmber, expiredAt, createAt);
    }
}
