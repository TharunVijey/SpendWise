package com.tharun.spendwise.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tharun.spendwise.entity.Expense;
import com.tharun.spendwise.entity.User;
import com.tharun.spendwise.repository.ExpenseRepository;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final CurrentUserService currentUserService;

    public ExpenseService(ExpenseRepository expenseRepository, CurrentUserService currentUserService) {
        this.expenseRepository = expenseRepository;
        this.currentUserService = currentUserService;
    }

    public Expense addExpense(Expense expense) {
        expense.setUser(currentUserService.getCurrentUser());
        return expenseRepository.save(expense);
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAllByUser_Id(currentUserService.getCurrentUser().getId());
    }

    public Optional<Expense> getExpenseById(Long id) {
        return expenseRepository.findByIdAndUser_Id(id, currentUserService.getCurrentUser().getId());
    }

    public Expense updateExpense(Long id, Expense updatedExpense) {
        User user = currentUserService.getCurrentUser();
        Expense existing = expenseRepository.findByIdAndUser_Id(id, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found with ID: " + id));
        existing.setTitle(updatedExpense.getTitle());
        existing.setAmount(updatedExpense.getAmount());
        existing.setCategory(updatedExpense.getCategory());
        existing.setExpenseDate(updatedExpense.getExpenseDate());
        existing.setNotes(updatedExpense.getNotes());
        return expenseRepository.save(existing);
    }

    public void deleteExpense(Long id) {
        Expense expense = expenseRepository.findByIdAndUser_Id(id, currentUserService.getCurrentUser().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found with ID: " + id));
        expenseRepository.delete(expense);
    }
}
