
package com.tharun.spendwise.dto;

public class YearlySummary {

    private final int year;
    private final double totalIncome;
    private final double totalExpenses;
    private final double yearlySurplus;
    private final double savingsDeposits;
    private final double savingsWithdrawals;

    public YearlySummary(
            int year,
            double totalIncome,
            double totalExpenses,
            double yearlySurplus,
            double savingsDeposits,
            double savingsWithdrawals) {

        this.year = year;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.yearlySurplus = yearlySurplus;
        this.savingsDeposits = savingsDeposits;
        this.savingsWithdrawals = savingsWithdrawals;
    }

    public int getYear() {
        return year;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public double getTotalExpenses() {
        return totalExpenses;
    }

    public double getYearlySurplus() {
        return yearlySurplus;
    }

    public double getSavingsDeposits() {
        return savingsDeposits;
    }

    public double getSavingsWithdrawals() {
        return savingsWithdrawals;
    }
}
