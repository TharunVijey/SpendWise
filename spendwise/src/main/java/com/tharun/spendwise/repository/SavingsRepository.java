package com.tharun.spendwise.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tharun.spendwise.entity.Savings;

public interface SavingsRepository extends JpaRepository<Savings, Long> {
    List<Savings> findAllByUser_Id(Long userId);
    Optional<Savings> findByIdAndUser_Id(Long id, Long userId);
    List<Savings> findByUser_IdAndSavingsDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}
