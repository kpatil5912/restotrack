package com.restotrack.dto;

import com.restotrack.entity.Unit;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateIngredientRequest(
        @NotBlank String name,
        @NotNull Unit stockUnit,
        @NotNull @DecimalMin("0.0") BigDecimal costPerUnit,
        @NotNull @DecimalMin("0.0") BigDecimal lowStockThreshold
) {
}
