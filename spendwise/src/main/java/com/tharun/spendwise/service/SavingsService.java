
package com.tharun.spendwise.service;

import com.tharun.spendwise.entity.Savings;
import com.tharun.spendwise.entity.SavingsSettings;
import com.tharun.spendwise.repository.SavingsRepository;
import com.tharun.spendwise.repository.SavingsSettingsRepository;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class SavingsService {

    private final SavingsRepository savingsRepository;
    private final SavingsSettingsRepository settingsRepository;

    public SavingsService(
            SavingsRepository savingsRepository,
            SavingsSettingsRepository settingsRepository) {
        this.savingsRepository = savingsRepository;
        this.settingsRepository = settingsRepository;
    }

    public Savings addSavings(Savings savings) {

        validateType(savings.getType());
        savings.setType(savings.getType().toUpperCase());

        if ("WITHDRAWAL".equals(savings.getType())) {
            validateWithdrawal(savings.getAmount(), null);
        }

        return savingsRepository.save(savings);
    }

    public List<Savings> getAllSavings() {
        return savingsRepository.findAll();
    }

    public Optional<Savings> getSavingsById(Long id) {
        return savingsRepository.findById(id);
    }

    public Savings updateSavings(Long id, Savings updatedSavings) {

        validateType(updatedSavings.getType());

        Savings existingSavings = savingsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Savings transaction not found with ID: " + id));

        String newType = updatedSavings.getType().toUpperCase();

        if ("WITHDRAWAL".equals(newType)) {
            validateWithdrawal(updatedSavings.getAmount(), id);
        }

        existingSavings.setAmount(updatedSavings.getAmount());
        existingSavings.setType(newType);
        existingSavings.setSavingsDate(updatedSavings.getSavingsDate());
        existingSavings.setNotes(updatedSavings.getNotes());

        return savingsRepository.save(existingSavings);
    }

    public void deleteSavings(Long id) {

        Savings savings = savingsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Savings transaction not found with ID: " + id));

        savingsRepository.delete(savings);
    }

    private void validateWithdrawal(BigDecimal withdrawalAmount, Long excludeId) {

        if (withdrawalAmount == null ||
                withdrawalAmount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Withdrawal amount must be greater than zero");
        }

        BigDecimal openingBalance = settingsRepository.findAll().stream()
                .findFirst()
                .map(SavingsSettings::getOpeningBalance)
                .orElse(BigDecimal.ZERO);

        List<Savings> transactions = savingsRepository.findAll();

        // Exclude the transaction currently being updated.
        // This exclusion applies to BOTH deposits and withdrawals.

        BigDecimal totalDeposits = transactions.stream()
                .filter(s -> "DEPOSIT".equalsIgnoreCase(s.getType()))
                .filter(s -> excludeId == null || !excludeId.equals(s.getId()))
                .map(Savings::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalWithdrawals = transactions.stream()
                .filter(s -> "WITHDRAWAL".equalsIgnoreCase(s.getType()))
                .filter(s -> excludeId == null || !excludeId.equals(s.getId()))
                .map(Savings::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal availableBalance = openingBalance
                .add(totalDeposits)
                .subtract(totalWithdrawals);

        if (withdrawalAmount.compareTo(availableBalance) > 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Insufficient savings balance. Available balance: "
                            + availableBalance);
        }
    }

    private void validateType(String type) {

        if (type == null ||
                (!type.equalsIgnoreCase("DEPOSIT")
                        && !type.equalsIgnoreCase("WITHDRAWAL"))) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Transaction type must be DEPOSIT or WITHDRAWAL");
        }
    }
}
