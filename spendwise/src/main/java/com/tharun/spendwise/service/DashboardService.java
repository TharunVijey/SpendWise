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
    private final CurrentUserService currentUserService;

    public DashboardService(IncomeRepository incomeRepository, ExpenseRepository expenseRepository,
            SavingsRepository savingsRepository, SavingsSettingsRepository settingsRepository,
            CurrentUserService currentUserService) {
        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
        this.savingsRepository = savingsRepository;
        this.settingsRepository = settingsRepository;
        this.currentUserService = currentUserService;
    }

    public DashboardSummary getDashboardSummary(int year, int month) {
        LocalDate start;
        LocalDate end;
        try {
            start = LocalDate.of(year, month, 1);
            end = start.withDayOfMonth(start.lengthOfMonth());
        } catch (DateTimeException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid year or month");
        }
        Long userId = currentUserService.getCurrentUser().getId();
        List<Income> incomes = incomeRepository.findByUser_IdAndIncomeDateBetween(userId, start, end);
        List<Expense> expenses = expenseRepository.findByUser_IdAndExpenseDateBetween(userId, start, end);
        List<Savings> monthSavings = savingsRepository.findByUser_IdAndSavingsDateBetween(userId, start, end);
        BigDecimal income = incomes.stream().map(Income::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal expensesTotal = expenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal depositsMonth = monthSavings.stream().filter(s -> "DEPOSIT".equalsIgnoreCase(s.getType()))
                .map(Savings::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal withdrawalsMonth = monthSavings.stream().filter(s -> "WITHDRAWAL".equalsIgnoreCase(s.getType()))
                .map(Savings::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<Savings> allSavings = savingsRepository.findAllByUser_Id(userId);
        BigDecimal opening = settingsRepository.findByUser_Id(userId)
                .map(SavingsSettings::getOpeningBalance).orElse(BigDecimal.ZERO);
        BigDecimal depositsAll = allSavings.stream().filter(s -> "DEPOSIT".equalsIgnoreCase(s.getType()))
                .map(Savings::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal withdrawalsAll = allSavings.stream().filter(s -> "WITHDRAWAL".equalsIgnoreCase(s.getType()))
                .map(Savings::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal savingsBalance = opening.add(depositsAll).subtract(withdrawalsAll);
        return new DashboardSummary(year, month, income, expensesTotal, income.subtract(expensesTotal),
                depositsMonth, withdrawalsMonth, savingsBalance);
    }
}
