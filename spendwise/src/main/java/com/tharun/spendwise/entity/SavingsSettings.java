
package com.tharun.spendwise.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "savings_settings", uniqueConstraints = @UniqueConstraint(columnNames = "user_id"))
public class SavingsSettings {

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @PositiveOrZero(message = "Opening balance cannot be negative")
    @Column(precision = 19, scale = 2)
    private BigDecimal openingBalance;

    public Long getId() {
        return id;
    }

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(BigDecimal openingBalance) {
        this.openingBalance = openingBalance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
