
package com.tharun.spendwise.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tharun.spendwise.entity.Savings;

public interface SavingsRepository extends JpaRepository<Savings, Long> {

    List<Savings> findBySavingsDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );

}
