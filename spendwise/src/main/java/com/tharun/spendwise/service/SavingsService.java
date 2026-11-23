
package com.tharun.spendwise.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tharun.spendwise.entity.Savings;
import com.tharun.spendwise.repository.SavingsRepository;

@Service
public class SavingsService {

    private final SavingsRepository savingsRepository;

    public SavingsService(SavingsRepository savingsRepository) {
        this.savingsRepository = savingsRepository;
    }

    public Savings addSavings(Savings savings) {
        validateType(savings.getType());
        savings.setType(savings.getType().toUpperCase());

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
                        "Savings transaction not found with ID: " + id
                ));

        existingSavings.setAmount(updatedSavings.getAmount());
        existingSavings.setType(updatedSavings.getType().toUpperCase());
        existingSavings.setSavingsDate(updatedSavings.getSavingsDate());
        existingSavings.setNotes(updatedSavings.getNotes());

        return savingsRepository.save(existingSavings);
    }

    public void deleteSavings(Long id) {

        Savings savings = savingsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Savings transaction not found with ID: " + id
                ));

        savingsRepository.delete(savings);
    }

    private void validateType(String type) {

        if (type == null ||
                (!type.equalsIgnoreCase("DEPOSIT") &&
                 !type.equalsIgnoreCase("WITHDRAWAL"))) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Transaction type must be DEPOSIT or WITHDRAWAL"
            );
        }
    }
}
