package com.tharun.spendwise.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tharun.spendwise.entity.SavingsSettings;

public interface SavingsSettingsRepository extends JpaRepository<SavingsSettings, Long> {
    Optional<SavingsSettings> findByUser_Id(Long userId);
}
