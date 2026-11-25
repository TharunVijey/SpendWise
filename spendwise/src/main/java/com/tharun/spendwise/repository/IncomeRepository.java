package com.tharun.spendwise.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tharun.spendwise.entity.Income;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findAllByUser_Id(Long userId);
    Optional<Income> findByIdAndUser_Id(Long id, Long userId);
    List<Income> findByUser_IdAndIncomeDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}
