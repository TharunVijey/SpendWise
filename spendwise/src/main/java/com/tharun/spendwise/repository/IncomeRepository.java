
package com.tharun.spendwise.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tharun.spendwise.entity.Income;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    List<Income> findByIncomeDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );

}
