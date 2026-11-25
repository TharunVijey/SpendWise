
package com.tharun.spendwise.dto;

import java.math.BigDecimal;

public class DashboardSummary {

    private final int year;
    private final int month;
    private final BigDecimal totalIncome;
    private final BigDecimal totalExpenses;
    private final BigDecimal monthlySurplus;
    private final BigDecimal savingsDeposits;
    private final BigDecimal savingsWithdrawals;
    private final BigDecimal overallSavingsBalance;

    public DashboardSummary(int year, int month,
            BigDecimal totalIncome, BigDecimal totalExpenses,
            BigDecimal monthlySurplus, BigDecimal savingsDeposits,
            BigDecimal savingsWithdrawals, BigDecimal overallSavingsBalance) {

        this.year = year;
        this.month = month;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.monthlySurplus = monthlySurplus;
        this.savingsDeposits = savingsDeposits;
        this.savingsWithdrawals = savingsWithdrawals;
        this.overallSavingsBalance = overallSavingsBalance;
    }

    public int getYear() { return year; }
    public int getMonth() { return month; }
    public BigDecimal getTotalIncome() { return totalIncome; }
    public BigDecimal getTotalExpenses() { return totalExpenses; }
    public BigDecimal getMonthlySurplus() { return monthlySurplus; }
    public BigDecimal getSavingsDeposits() { return savingsDeposits; }
    public BigDecimal getSavingsWithdrawals() { return savingsWithdrawals; }
    public BigDecimal getOverallSavingsBalance() { return overallSavingsBalance; }
}
