
package com.tharun.spendwise.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tharun.spendwise.dto.MonthlySummary;
import com.tharun.spendwise.entity.Expense;
import com.tharun.spendwise.entity.Income;
import com.tharun.spendwise.entity.Savings;
import com.tharun.spendwise.repository.ExpenseRepository;
import com.tharun.spendwise.repository.IncomeRepository;
import com.tharun.spendwise.repository.SavingsRepository;

@Service
public class MonthlySummaryService {

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final SavingsRepository savingsRepository;

    public MonthlySummaryService(
            IncomeRepository incomeRepository,
            ExpenseRepository expenseRepository,
            SavingsRepository savingsRepository) {

        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
        this.savingsRepository = savingsRepository;
    }

    public MonthlySummary getMonthlySummary(int year, int month) {

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(
                startDate.lengthOfMonth()
        );

        List<Income> incomes =
                incomeRepository.findByIncomeDateBetween(startDate, endDate);

        List<Expense> expenses =
                expenseRepository.findByExpenseDateBetween(startDate, endDate);

        List<Savings> savings =
                savingsRepository.findBySavingsDateBetween(startDate, endDate);

        double totalIncome = incomes.stream()
                .mapToDouble(Income::getAmount)
                .sum();

        double totalExpenses = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        double savingsDeposits = savings.stream()
                .filter(s -> "DEPOSIT".equalsIgnoreCase(s.getType()))
                .mapToDouble(Savings::getAmount)
                .sum();

        double savingsWithdrawals = savings.stream()
                .filter(s -> "WITHDRAWAL".equalsIgnoreCase(s.getType()))
                .mapToDouble(Savings::getAmount)
                .sum();

        double monthlySurplus = totalIncome - totalExpenses;

        return new MonthlySummary(
                year,
                month,
                totalIncome,
                totalExpenses,
                monthlySurplus,
                savingsDeposits,
                savingsWithdrawals
        );
    }
}
