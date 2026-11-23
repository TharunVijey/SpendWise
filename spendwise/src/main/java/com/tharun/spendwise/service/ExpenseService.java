
package com.tharun.spendwise.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tharun.spendwise.entity.Expense;
import com.tharun.spendwise.repository.ExpenseRepository;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public Expense addExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }
    public Optional<Expense> getExpenseById(Long id) {
    return expenseRepository.findById(id);
}
    public Expense updateExpense(Long id, Expense updatedExpense) {

    Expense existingExpense = expenseRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        "Expense not found with ID: " + id
            ));

    existingExpense.setTitle(updatedExpense.getTitle());
    existingExpense.setAmount(updatedExpense.getAmount());
    existingExpense.setCategory(updatedExpense.getCategory());
    existingExpense.setExpenseDate(updatedExpense.getExpenseDate());
    existingExpense.setNotes(updatedExpense.getNotes());

    return expenseRepository.save(existingExpense);
}
public void deleteExpense(Long id) {

    Expense expense = expenseRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Expense not found with ID: " + id
            ));

    expenseRepository.delete(expense);
}
}
