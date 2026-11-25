package com.tharun.spendwise.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tharun.spendwise.entity.Income;
import com.tharun.spendwise.entity.User;
import com.tharun.spendwise.repository.IncomeRepository;

@Service
public class IncomeService {
    private final IncomeRepository incomeRepository;
    private final CurrentUserService currentUserService;

    public IncomeService(IncomeRepository incomeRepository, CurrentUserService currentUserService) {
        this.incomeRepository = incomeRepository;
        this.currentUserService = currentUserService;
    }

    public Income addIncome(Income income) {
        income.setUser(currentUserService.getCurrentUser());
        return incomeRepository.save(income);
    }

    public List<Income> getAllIncomes() {
        return incomeRepository.findAllByUser_Id(currentUserService.getCurrentUser().getId());
    }

    public Optional<Income> getIncomeById(Long id) {
        return incomeRepository.findByIdAndUser_Id(id, currentUserService.getCurrentUser().getId());
    }

    public Income updateIncome(Long id, Income updatedIncome) {
        User user = currentUserService.getCurrentUser();
        Income existing = incomeRepository.findByIdAndUser_Id(id, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Income not found with ID: " + id));
        existing.setTitle(updatedIncome.getTitle());
        existing.setAmount(updatedIncome.getAmount());
        existing.setSource(updatedIncome.getSource());
        existing.setIncomeDate(updatedIncome.getIncomeDate());
        existing.setNotes(updatedIncome.getNotes());
        return incomeRepository.save(existing);
    }

    public void deleteIncome(Long id) {
        Income income = incomeRepository.findByIdAndUser_Id(id, currentUserService.getCurrentUser().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Income not found with ID: " + id));
        incomeRepository.delete(income);
    }
}
