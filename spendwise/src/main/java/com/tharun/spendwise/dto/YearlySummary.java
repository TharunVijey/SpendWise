
package com.tharun.spendwise.dto;

import java.math.BigDecimal;

public class YearlySummary {

    private final int year;
    private final BigDecimal totalIncome;
    private final BigDecimal totalExpenses;
    private final BigDecimal yearlySurplus;
    private final BigDecimal savingsDeposits;
    private final BigDecimal savingsWithdrawals;

    public YearlySummary(int year, BigDecimal totalIncome,
            BigDecimal totalExpenses, BigDecimal yearlySurplus,
            BigDecimal savingsDeposits, BigDecimal savingsWithdrawals) {

        this.year = year;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.yearlySurplus = yearlySurplus;
        this.savingsDeposits = savingsDeposits;
        this.savingsWithdrawals = savingsWithdrawals;
    }

    public int getYear() { return year; }
    public BigDecimal getTotalIncome() { return totalIncome; }
    public BigDecimal getTotalExpenses() { return totalExpenses; }
    public BigDecimal getYearlySurplus() { return yearlySurplus; }
    public BigDecimal getSavingsDeposits() { return savingsDeposits; }
    public BigDecimal getSavingsWithdrawals() { return savingsWithdrawals; }
}
