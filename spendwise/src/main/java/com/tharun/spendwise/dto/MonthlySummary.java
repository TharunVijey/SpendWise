
package com.tharun.spendwise.dto;

public class MonthlySummary {

    private final int year;
    private final int month;

    private final double totalIncome;
    private final double totalExpenses;
    private final double monthlySurplus;

    private final double savingsDeposits;
    private final double savingsWithdrawals;

    public MonthlySummary(
            int year,
            int month,
            double totalIncome,
            double totalExpenses,
            double monthlySurplus,
            double savingsDeposits,
            double savingsWithdrawals) {

        this.year = year;
        this.month = month;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.monthlySurplus = monthlySurplus;
        this.savingsDeposits = savingsDeposits;
        this.savingsWithdrawals = savingsWithdrawals;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public double getTotalExpenses() {
        return totalExpenses;
    }

    public double getMonthlySurplus() {
        return monthlySurplus;
    }

    public double getSavingsDeposits() {
        return savingsDeposits;
    }

    public double getSavingsWithdrawals() {
        return savingsWithdrawals;
    }
}
