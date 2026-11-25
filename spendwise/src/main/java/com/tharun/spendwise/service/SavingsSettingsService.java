package com.tharun.spendwise.service;

import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.tharun.spendwise.entity.SavingsSettings;
import com.tharun.spendwise.entity.User;
import com.tharun.spendwise.repository.SavingsSettingsRepository;

@Service
public class SavingsSettingsService {
    private final SavingsSettingsRepository settingsRepository;
    private final CurrentUserService currentUserService;

    public SavingsSettingsService(SavingsSettingsRepository settingsRepository, CurrentUserService currentUserService) {
        this.settingsRepository = settingsRepository;
        this.currentUserService = currentUserService;
    }

    public SavingsSettings setOpeningBalance(BigDecimal openingBalance) {
        if (openingBalance == null || openingBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Opening balance cannot be negative");
        }
        User user = currentUserService.getCurrentUser();
        SavingsSettings settings = settingsRepository.findByUser_Id(user.getId()).orElseGet(() -> {
            SavingsSettings created = new SavingsSettings();
            created.setUser(user);
            return created;
        });
        settings.setOpeningBalance(openingBalance);
        return settingsRepository.save(settings);
    }

    public SavingsSettings getOpeningBalance() {
        User user = currentUserService.getCurrentUser();
        return settingsRepository.findByUser_Id(user.getId()).orElseGet(() -> {
            SavingsSettings settings = new SavingsSettings();
            settings.setUser(user);
            settings.setOpeningBalance(BigDecimal.ZERO);
            return settingsRepository.save(settings);
        });
    }
}
