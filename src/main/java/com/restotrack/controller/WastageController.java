package com.restotrack.controller;

import com.restotrack.service.WastageService;
import com.restotrack.dto.WastageReportResponse;
import com.restotrack.dto.WastageRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/wastage")
public class WastageController {

    private final WastageService service;

    public WastageController(WastageService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> log(@Valid @RequestBody WastageRequest req) {
        service.logWastage(req);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/report")
    public WastageReportResponse report(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.dailyReport(date == null ? LocalDate.now() : date);
    }
}
