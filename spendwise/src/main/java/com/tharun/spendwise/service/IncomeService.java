
package com.tharun.spendwise.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tharun.spendwise.entity.Income;
import com.tharun.spendwise.repository.IncomeRepository;

@Service
public class IncomeService {

    private final IncomeRepository incomeRepository;

    public IncomeService(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    public Income addIncome(Income income) {
        return incomeRepository.save(income);
    }

    public List<Income> getAllIncomes() {
        return incomeRepository.findAll();
    }

    public Optional<Income> getIncomeById(Long id) {
        return incomeRepository.findById(id);
    }
    public Income updateIncome(Long id, Income updatedIncome) {

    Income existingIncome = incomeRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Income not found with ID: " + id
            ));

    existingIncome.setTitle(updatedIncome.getTitle());
    existingIncome.setAmount(updatedIncome.getAmount());
    existingIncome.setSource(updatedIncome.getSource());
    existingIncome.setIncomeDate(updatedIncome.getIncomeDate());
    existingIncome.setNotes(updatedIncome.getNotes());

    return incomeRepository.save(existingIncome);
}

public void deleteIncome(Long id) {

    Income income = incomeRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Income not found with ID: " + id
            ));

    incomeRepository.delete(income);
}
}
