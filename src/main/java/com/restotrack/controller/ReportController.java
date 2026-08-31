package com.restotrack.controller;

import com.restotrack.service.ReportService;
import com.restotrack.dto.DishCostResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    /** Cost-per-dish and margin for a menu item. */
    @GetMapping("/dish-cost/{menuItemId}")
    public DishCostResponse dishCost(@PathVariable Long menuItemId) {
        return service.dishCost(menuItemId);
    }
}
