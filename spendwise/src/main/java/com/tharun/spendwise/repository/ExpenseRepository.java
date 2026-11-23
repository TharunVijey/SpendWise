
package com.tharun.spendwise.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tharun.spendwise.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByExpenseDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );

}
