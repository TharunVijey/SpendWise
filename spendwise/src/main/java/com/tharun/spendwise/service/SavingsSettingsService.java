
package com.tharun.spendwise.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tharun.spendwise.entity.SavingsSettings;
import com.tharun.spendwise.repository.SavingsSettingsRepository;

@Service
public class SavingsSettingsService {

    private final SavingsSettingsRepository settingsRepository;

    public SavingsSettingsService(SavingsSettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public SavingsSettings setOpeningBalance(double openingBalance) {

        if (openingBalance < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Opening balance cannot be negative"
            );
        }

        SavingsSettings settings = settingsRepository.findAll()
                .stream()
                .findFirst()
                .orElse(new SavingsSettings());

        settings.setOpeningBalance(openingBalance);

        return settingsRepository.save(settings);
    }

    public SavingsSettings getOpeningBalance() {

        return settingsRepository.findAll()
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    SavingsSettings settings = new SavingsSettings();
                    settings.setOpeningBalance(0);
                    return settingsRepository.save(settings);
                });
    }
}
