package com.tharun.spendwise.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tharun.spendwise.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findAllByUser_Id(Long userId);
    Optional<Expense> findByIdAndUser_Id(Long id, Long userId);
    List<Expense> findByUser_IdAndExpenseDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}
