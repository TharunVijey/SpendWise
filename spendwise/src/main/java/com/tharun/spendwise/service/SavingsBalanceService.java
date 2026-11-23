
package com.tharun.spendwise.service;

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

    public SavingsBalanceService(
            SavingsRepository savingsRepository,
            SavingsSettingsRepository settingsRepository) {

        this.savingsRepository = savingsRepository;
        this.settingsRepository = settingsRepository;
    }

    public SavingsBalance getSavingsBalance() {

        List<Savings> transactions = savingsRepository.findAll();

        double openingBalance = settingsRepository.findAll()
                .stream()
                .findFirst()
                .map(SavingsSettings::getOpeningBalance)
                .orElse(0.0);

        double totalDeposits = transactions.stream()
                .filter(s -> "DEPOSIT".equalsIgnoreCase(s.getType()))
                .mapToDouble(Savings::getAmount)
                .sum();

        double totalWithdrawals = transactions.stream()
                .filter(s -> "WITHDRAWAL".equalsIgnoreCase(s.getType()))
                .mapToDouble(Savings::getAmount)
                .sum();

        double currentBalance = openingBalance
                + totalDeposits
                - totalWithdrawals;

        return new SavingsBalance(
                openingBalance,
                totalDeposits,
                totalWithdrawals,
                currentBalance
        );
    }

    public static class SavingsBalance {

        private final double openingBalance;
        private final double totalDeposits;
        private final double totalWithdrawals;
        private final double currentBalance;

        public SavingsBalance(
                double openingBalance,
                double totalDeposits,
                double totalWithdrawals,
                double currentBalance) {

            this.openingBalance = openingBalance;
            this.totalDeposits = totalDeposits;
            this.totalWithdrawals = totalWithdrawals;
            this.currentBalance = currentBalance;
        }

        public double getOpeningBalance() {
            return openingBalance;
        }

        public double getTotalDeposits() {
            return totalDeposits;
        }

        public double getTotalWithdrawals() {
            return totalWithdrawals;
        }

        public double getCurrentBalance() {
            return currentBalance;
        }
    }
}
