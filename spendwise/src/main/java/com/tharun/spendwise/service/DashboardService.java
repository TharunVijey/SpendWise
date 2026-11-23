
package com.tharun.spendwise.service;

import com.tharun.spendwise.dto.DashboardSummary;
import com.tharun.spendwise.entity.Income;
import com.tharun.spendwise.entity.Expense;
import com.tharun.spendwise.entity.Savings;
import com.tharun.spendwise.entity.SavingsSettings;

import com.tharun.spendwise.repository.IncomeRepository;
import com.tharun.spendwise.repository.ExpenseRepository;
import com.tharun.spendwise.repository.SavingsRepository;
import com.tharun.spendwise.repository.SavingsSettingsRepository;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.DateTimeException;
import java.util.List;

@Service
public class DashboardService {

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final SavingsRepository savingsRepository;
    private final SavingsSettingsRepository settingsRepository;

    public DashboardService(
            IncomeRepository incomeRepository,
            ExpenseRepository expenseRepository,
            SavingsRepository savingsRepository,
            SavingsSettingsRepository settingsRepository) {

        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
        this.savingsRepository = savingsRepository;
        this.settingsRepository = settingsRepository;
    }

    public DashboardSummary getDashboardSummary(int year, int month) {

        LocalDate startDate;
        LocalDate endDate;

        try {
            startDate = LocalDate.of(year, month, 1);
            endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        } catch (DateTimeException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid year or month"
            );
        }

        // Fetch transactions for the selected month
        List<Income> incomes =
                incomeRepository.findByIncomeDateBetween(startDate, endDate);

        List<Expense> expenses =
                expenseRepository.findByExpenseDateBetween(startDate, endDate);

        List<Savings> monthlySavings =
                savingsRepository.findBySavingsDateBetween(startDate, endDate);

        // Calculate monthly income
        double totalIncome = incomes.stream()
                .mapToDouble(Income::getAmount)
                .sum();

        // Calculate monthly expenses
        double totalExpenses = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        // Calculate monthly savings deposits
        double savingsDeposits = monthlySavings.stream()
                .filter(s -> "DEPOSIT".equalsIgnoreCase(s.getType()))
                .mapToDouble(Savings::getAmount)
                .sum();

        // Calculate monthly savings withdrawals
        double savingsWithdrawals = monthlySavings.stream()
                .filter(s -> "WITHDRAWAL".equalsIgnoreCase(s.getType()))
                .mapToDouble(Savings::getAmount)
                .sum();

        // Monthly surplus = income - expenses
        double monthlySurplus = totalIncome - totalExpenses;

        // Calculate overall savings balance
        List<Savings> allSavings = savingsRepository.findAll();

        double openingBalance = settingsRepository.findAll()
                .stream()
                .findFirst()
                .map(SavingsSettings::getOpeningBalance)
                .orElse(0.0);

        double totalDeposits = allSavings.stream()
                .filter(s -> "DEPOSIT".equalsIgnoreCase(s.getType()))
                .mapToDouble(Savings::getAmount)
                .sum();

        double totalWithdrawals = allSavings.stream()
                .filter(s -> "WITHDRAWAL".equalsIgnoreCase(s.getType()))
                .mapToDouble(Savings::getAmount)
                .sum();

        double overallSavingsBalance =
                openingBalance + totalDeposits - totalWithdrawals;

        return new DashboardSummary(
                year,
                month,
                totalIncome,
                totalExpenses,
                monthlySurplus,
                savingsDeposits,
                savingsWithdrawals,
                overallSavingsBalance
        );
    }
}
