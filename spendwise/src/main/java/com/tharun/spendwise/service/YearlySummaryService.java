package com.tharun.spendwise.service;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.tharun.spendwise.dto.YearlySummary;
import com.tharun.spendwise.entity.Expense;
import com.tharun.spendwise.entity.Income;
import com.tharun.spendwise.entity.Savings;
import com.tharun.spendwise.repository.ExpenseRepository;
import com.tharun.spendwise.repository.IncomeRepository;
import com.tharun.spendwise.repository.SavingsRepository;

@Service
public class YearlySummaryService {
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final SavingsRepository savingsRepository;
    private final CurrentUserService currentUserService;

    public YearlySummaryService(IncomeRepository incomeRepository, ExpenseRepository expenseRepository,
            SavingsRepository savingsRepository, CurrentUserService currentUserService) {
        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
        this.savingsRepository = savingsRepository;
        this.currentUserService = currentUserService;
    }

    public YearlySummary getYearlySummary(int year) {
        LocalDate start;
        LocalDate end;
        try {
            start = LocalDate.of(year, 1, 1);
            end = LocalDate.of(year, 12, 31);
        } catch (DateTimeException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid year");
        }
        Long userId = currentUserService.getCurrentUser().getId();
        List<Income> incomes = incomeRepository.findByUser_IdAndIncomeDateBetween(userId, start, end);
        List<Expense> expenses = expenseRepository.findByUser_IdAndExpenseDateBetween(userId, start, end);
        List<Savings> savings = savingsRepository.findByUser_IdAndSavingsDateBetween(userId, start, end);
        BigDecimal income = incomes.stream().map(Income::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal expensesTotal = expenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal deposits = savings.stream().filter(s -> "DEPOSIT".equalsIgnoreCase(s.getType()))
                .map(Savings::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal withdrawals = savings.stream().filter(s -> "WITHDRAWAL".equalsIgnoreCase(s.getType()))
                .map(Savings::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new YearlySummary(year, income, expensesTotal, income.subtract(expensesTotal), deposits, withdrawals);
    }
}
