package com.tharun.spendwise.service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import com.tharun.spendwise.entity.Savings;
import com.tharun.spendwise.entity.SavingsSettings;
import com.tharun.spendwise.repository.SavingsRepository;
import com.tharun.spendwise.repository.SavingsSettingsRepository;

@Service
public class SavingsBalanceService {
    private final SavingsRepository savingsRepository;
    private final SavingsSettingsRepository settingsRepository;
    private final CurrentUserService currentUserService;

    public SavingsBalanceService(SavingsRepository savingsRepository, SavingsSettingsRepository settingsRepository,
            CurrentUserService currentUserService) {
        this.savingsRepository = savingsRepository;
        this.settingsRepository = settingsRepository;
        this.currentUserService = currentUserService;
    }

    public SavingsBalance getSavingsBalance() {
        Long userId = currentUserService.getCurrentUser().getId();
        List<Savings> transactions = savingsRepository.findAllByUser_Id(userId);
        BigDecimal opening = settingsRepository.findByUser_Id(userId)
                .map(SavingsSettings::getOpeningBalance).orElse(BigDecimal.ZERO);
        BigDecimal deposits = transactions.stream().filter(s -> "DEPOSIT".equalsIgnoreCase(s.getType()))
                .map(Savings::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal withdrawals = transactions.stream().filter(s -> "WITHDRAWAL".equalsIgnoreCase(s.getType()))
                .map(Savings::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new SavingsBalance(opening, deposits, withdrawals, opening.add(deposits).subtract(withdrawals));
    }

    public static class SavingsBalance {
        private final BigDecimal openingBalance;
        private final BigDecimal totalDeposits;
        private final BigDecimal totalWithdrawals;
        private final BigDecimal currentBalance;
        public SavingsBalance(BigDecimal openingBalance, BigDecimal totalDeposits, BigDecimal totalWithdrawals, BigDecimal currentBalance) {
            this.openingBalance = openingBalance;
            this.totalDeposits = totalDeposits;
            this.totalWithdrawals = totalWithdrawals;
            this.currentBalance = currentBalance;
        }
        public BigDecimal getOpeningBalance() { return openingBalance; }
        public BigDecimal getTotalDeposits() { return totalDeposits; }
        public BigDecimal getTotalWithdrawals() { return totalWithdrawals; }
        public BigDecimal getCurrentBalance() { return currentBalance; }
    }
}
