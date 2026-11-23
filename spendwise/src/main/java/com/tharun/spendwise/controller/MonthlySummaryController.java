
package com.tharun.spendwise.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tharun.spendwise.dto.MonthlySummary;
import com.tharun.spendwise.service.MonthlySummaryService;

@RestController
@RequestMapping("/api/reports")
public class MonthlySummaryController {

    private final MonthlySummaryService monthlySummaryService;

    public MonthlySummaryController(
            MonthlySummaryService monthlySummaryService) {

        this.monthlySummaryService = monthlySummaryService;
    }

    @GetMapping("/monthly")
    public MonthlySummary getMonthlySummary(
            @RequestParam int year,
            @RequestParam int month) {

        return monthlySummaryService.getMonthlySummary(year, month);
    }
}
