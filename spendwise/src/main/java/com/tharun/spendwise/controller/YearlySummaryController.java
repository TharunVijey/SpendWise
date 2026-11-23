
package com.tharun.spendwise.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tharun.spendwise.dto.YearlySummary;
import com.tharun.spendwise.service.YearlySummaryService;

@RestController
@RequestMapping("/api/reports")
public class YearlySummaryController {

    private final YearlySummaryService yearlySummaryService;

    public YearlySummaryController(
            YearlySummaryService yearlySummaryService) {

        this.yearlySummaryService = yearlySummaryService;
    }

    @GetMapping("/yearly")
    public YearlySummary getYearlySummary(
            @RequestParam int year) {

        return yearlySummaryService.getYearlySummary(year);
    }
}
