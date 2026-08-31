package com.restotrack.dto;

import com.restotrack.entity.Unit;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RestockRequest(
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal quantity,
        @NotNull Unit unit,
        String note
) {
}
