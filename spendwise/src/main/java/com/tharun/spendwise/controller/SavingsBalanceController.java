
package com.tharun.spendwise.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tharun.spendwise.service.SavingsBalanceService;
import com.tharun.spendwise.service.SavingsBalanceService.SavingsBalance;

@RestController
@RequestMapping("/api/savings")
public class SavingsBalanceController {

    private final SavingsBalanceService savingsBalanceService;

    public SavingsBalanceController(
            SavingsBalanceService savingsBalanceService) {
        this.savingsBalanceService = savingsBalanceService;
    }

    @GetMapping("/balance")
    public SavingsBalance getSavingsBalance() {
        return savingsBalanceService.getSavingsBalance();
    }
}
