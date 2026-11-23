
package com.tharun.spendwise.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.tharun.spendwise.entity.Savings;
import com.tharun.spendwise.service.SavingsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/savings")
public class SavingsController {

    private final SavingsService savingsService;

    public SavingsController(SavingsService savingsService) {
        this.savingsService = savingsService;
    }

    @PostMapping
    public Savings addSavings(@Valid @RequestBody Savings savings) {
        return savingsService.addSavings(savings);
    }

    @GetMapping
    public List<Savings> getAllSavings() {
        return savingsService.getAllSavings();
    }

    @GetMapping("/{id}")
    public Savings getSavingsById(@PathVariable Long id) {
        return savingsService.getSavingsById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Savings transaction not found with ID: " + id
                ));
    }

    @PutMapping("/{id}")
    public Savings updateSavings(
            @PathVariable Long id,
            @Valid @RequestBody Savings savings) {
        return savingsService.updateSavings(id, savings);
    }

    @DeleteMapping("/{id}")
    public String deleteSavings(@PathVariable Long id) {
        savingsService.deleteSavings(id);
        return "Savings transaction deleted successfully!";
    }
}
