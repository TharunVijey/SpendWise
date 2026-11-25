package com.tharun.spendwise.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tharun.spendwise.entity.Savings;
import com.tharun.spendwise.entity.SavingsSettings;
import com.tharun.spendwise.entity.User;
import com.tharun.spendwise.repository.SavingsRepository;
import com.tharun.spendwise.repository.SavingsSettingsRepository;

@Service
public class SavingsService {
    private final SavingsRepository savingsRepository;
    private final SavingsSettingsRepository settingsRepository;
    private final CurrentUserService currentUserService;

    public SavingsService(SavingsRepository savingsRepository, SavingsSettingsRepository settingsRepository,
            CurrentUserService currentUserService) {
        this.savingsRepository = savingsRepository;
        this.settingsRepository = settingsRepository;
        this.currentUserService = currentUserService;
    }

    public Savings addSavings(Savings savings) {
        User user = currentUserService.getCurrentUser();
        validateType(savings.getType());
        savings.setType(savings.getType().toUpperCase());
        if ("WITHDRAWAL".equals(savings.getType())) {
            validateWithdrawal(savings.getAmount(), null, user.getId());
        }
        savings.setUser(user);
        return savingsRepository.save(savings);
    }

    public List<Savings> getAllSavings() {
        return savingsRepository.findAllByUser_Id(currentUserService.getCurrentUser().getId());
    }

    public Optional<Savings> getSavingsById(Long id) {
        return savingsRepository.findByIdAndUser_Id(id, currentUserService.getCurrentUser().getId());
    }

    public Savings updateSavings(Long id, Savings updatedSavings) {
        User user = currentUserService.getCurrentUser();
        validateType(updatedSavings.getType());
        Savings existing = savingsRepository.findByIdAndUser_Id(id, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Savings transaction not found with ID: " + id));
        String newType = updatedSavings.getType().toUpperCase();
        if ("WITHDRAWAL".equals(newType)) {
            validateWithdrawal(updatedSavings.getAmount(), id, user.getId());
        }
        existing.setAmount(updatedSavings.getAmount());
        existing.setType(newType);
        existing.setSavingsDate(updatedSavings.getSavingsDate());
        existing.setNotes(updatedSavings.getNotes());
        return savingsRepository.save(existing);
    }

    public void deleteSavings(Long id) {
        Savings savings = savingsRepository.findByIdAndUser_Id(id, currentUserService.getCurrentUser().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Savings transaction not found with ID: " + id));
        savingsRepository.delete(savings);
    }

    private void validateWithdrawal(BigDecimal amount, Long excludeId, Long userId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Withdrawal amount must be greater than zero");
        }
        BigDecimal openingBalance = settingsRepository.findByUser_Id(userId)
                .map(SavingsSettings::getOpeningBalance).orElse(BigDecimal.ZERO);
        List<Savings> transactions = savingsRepository.findAllByUser_Id(userId);
        BigDecimal deposits = transactions.stream()
                .filter(s -> "DEPOSIT".equalsIgnoreCase(s.getType()))
                .filter(s -> excludeId == null || !excludeId.equals(s.getId()))
                .map(Savings::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal withdrawals = transactions.stream()
                .filter(s -> "WITHDRAWAL".equalsIgnoreCase(s.getType()))
                .filter(s -> excludeId == null || !excludeId.equals(s.getId()))
                .map(Savings::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal available = openingBalance.add(deposits).subtract(withdrawals);
        if (amount.compareTo(available) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient savings balance. Available balance: " + available);
        }
    }

    private void validateType(String type) {
        if (type == null || (!type.equalsIgnoreCase("DEPOSIT") && !type.equalsIgnoreCase("WITHDRAWAL"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction type must be DEPOSIT or WITHDRAWAL");
        }
    }
}
