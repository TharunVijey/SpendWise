
package com.tharun.spendwise.service;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tharun.spendwise.dto.DashboardSummary;
import com.tharun.spendwise.entity.Expense;
import com.tharun.spendwise.entity.Income;
import com.tharun.spendwise.entity.Savings;
import com.tharun.spendwise.entity.SavingsSettings;
import com.tharun.spendwise.repository.ExpenseRepository;
import com.tharun.spendwise.repository.IncomeRepository;
import com.tharun.spendwise.repository.SavingsRepository;
import com.tharun.spendwise.repository.SavingsSettingsRepository;

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
                    HttpStatus.BAD_REQUEST, "Invalid year or month");
        }

        List<Income> incomes =
                incomeRepository.findByIncomeDateBetween(startDate, endDate);

        List<Expense> expenses =
                expenseRepository.findByExpenseDateBetween(startDate, endDate);

        List<Savings> monthlySavings =
                savingsRepository.findBySavingsDateBetween(startDate, endDate);

        BigDecimal totalIncome = incomes.stream()
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal savingsDeposits = monthlySavings.stream()
                .filter(s -> "DEPOSIT".equalsIgnoreCase(s.getType()))
                .map(Savings::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal savingsWithdrawals = monthlySavings.stream()
                .filter(s -> "WITHDRAWAL".equalsIgnoreCase(s.getType()))
                .map(Savings::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal monthlySurplus = totalIncome.subtract(totalExpenses);

        List<Savings> allSavings = savingsRepository.findAll();

        BigDecimal openingBalance = settingsRepository.findAll().stream()
                .findFirst()
                .map(SavingsSettings::getOpeningBalance)
                .orElse(BigDecimal.ZERO);

        BigDecimal totalDeposits = allSavings.stream()
                .filter(s -> "DEPOSIT".equalsIgnoreCase(s.getType()))
                .map(Savings::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalWithdrawals = allSavings.stream()
                .filter(s -> "WITHDRAWAL".equalsIgnoreCase(s.getType()))
                .map(Savings::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal overallSavingsBalance = openingBalance
                .add(totalDeposits)
                .subtract(totalWithdrawals);

        return new DashboardSummary(year, month, totalIncome,
                totalExpenses, monthlySurplus, savingsDeposits,
                savingsWithdrawals, overallSavingsBalance);
    }
}
