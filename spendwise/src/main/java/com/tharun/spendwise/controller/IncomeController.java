
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

import com.tharun.spendwise.entity.Income;
import com.tharun.spendwise.service.IncomeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/incomes")
public class IncomeController {

    private final IncomeService incomeService;

    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @PostMapping
    public Income addIncome(@Valid @RequestBody Income income) {
        return incomeService.addIncome(income);
    }

    @GetMapping
    public List<Income> getAllIncomes() {
        return incomeService.getAllIncomes();
    }

   @GetMapping("/{id}")
public Income getIncomeById(@PathVariable Long id) {
    return incomeService.getIncomeById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Income not found with ID: " + id
            ));
}

    @PutMapping("/{id}")
    public Income updateIncome(
            @PathVariable Long id,
            @Valid @RequestBody Income income) {

        return incomeService.updateIncome(id, income);
    }

    @DeleteMapping("/{id}")
    public String deleteIncome(@PathVariable Long id) {

        incomeService.deleteIncome(id);

        return "Income deleted successfully!";
    }
}
