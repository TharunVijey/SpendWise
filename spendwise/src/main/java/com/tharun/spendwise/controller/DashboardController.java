
package com.tharun.spendwise.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tharun.spendwise.dto.DashboardSummary;
import com.tharun.spendwise.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public DashboardSummary getDashboardSummary(
            @RequestParam int year,
            @RequestParam int month) {

        return dashboardService.getDashboardSummary(year, month);
    }
}
