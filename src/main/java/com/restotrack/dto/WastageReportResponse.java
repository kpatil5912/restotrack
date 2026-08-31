package com.restotrack.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record WastageReportResponse(
        LocalDate date,
        BigDecimal totalWastageCost,
        List<Line> items
) {
    public record Line(
            String ingredientName,
            BigDecimal quantity,
            String unit,
            BigDecimal cost,
            String reason
    ) {
    }
}
