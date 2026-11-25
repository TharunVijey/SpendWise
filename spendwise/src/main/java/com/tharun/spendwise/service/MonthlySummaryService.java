
package com.tharun.spendwise.service;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

        LocalDate startDate;
        LocalDate endDate;

        try {
            startDate = LocalDate.of(year, month, 1);
            endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        } catch (DateTimeException exception) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid year or month. Please provide a valid date."
            );
        }

        List<Income> incomes =
                incomeRepository.findByIncomeDateBetween(startDate, endDate);

        List<Expense> expenses =
                expenseRepository.findByExpenseDateBetween(startDate, endDate);

        List<Savings> savings =
                savingsRepository.findBySavingsDateBetween(startDate, endDate);

        BigDecimal totalIncome = incomes.stream()
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal savingsDeposits = savings.stream()
                .filter(s -> "DEPOSIT".equalsIgnoreCase(s.getType()))
                .map(Savings::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal savingsWithdrawals = savings.stream()
                .filter(s -> "WITHDRAWAL".equalsIgnoreCase(s.getType()))
                .map(Savings::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal monthlySurplus = totalIncome.subtract(totalExpenses);

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
