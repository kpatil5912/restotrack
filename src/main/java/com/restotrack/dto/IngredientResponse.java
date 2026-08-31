package com.restotrack.dto;

import com.restotrack.entity.Ingredient;
import com.restotrack.entity.Unit;

import java.math.BigDecimal;

public record IngredientResponse(
        Long id,
        String name,
        Unit stockUnit,
        BigDecimal currentStock,
        BigDecimal costPerUnit,
        BigDecimal lowStockThreshold,
        boolean lowStock
) {
    public static IngredientResponse from(Ingredient i) {
        return new IngredientResponse(
                i.getId(),
                i.getName(),
                i.getStockUnit(),
                i.getCurrentStock(),
                i.getCostPerUnit(),
                i.getLowStockThreshold(),
                i.getCurrentStock().compareTo(i.getLowStockThreshold()) <= 0
        );
    }
}
