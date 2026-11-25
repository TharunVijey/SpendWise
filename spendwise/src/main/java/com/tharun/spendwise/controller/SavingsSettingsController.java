
package com.tharun.spendwise.controller;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tharun.spendwise.entity.SavingsSettings;
import com.tharun.spendwise.service.SavingsSettingsService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/api/savings/settings")
public class SavingsSettingsController {

    private final SavingsSettingsService settingsService;

    public SavingsSettingsController(SavingsSettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @PutMapping("/opening-balance")
    public SavingsSettings setOpeningBalance(
            @Valid @RequestBody OpeningBalanceRequest request) {
        return settingsService.setOpeningBalance(request.getOpeningBalance());
    }

    @GetMapping("/opening-balance")
    public SavingsSettings getOpeningBalance() {
        return settingsService.getOpeningBalance();
    }

    public static class OpeningBalanceRequest {

        @NotNull(message = "Opening balance is required")
        @PositiveOrZero(message = "Opening balance cannot be negative")
        private BigDecimal openingBalance;

        public BigDecimal getOpeningBalance() {
            return openingBalance;
        }

        public void setOpeningBalance(BigDecimal openingBalance) {
            this.openingBalance = openingBalance;
        }
    }
}
