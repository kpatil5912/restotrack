package com.restotrack.dto;

import java.math.BigDecimal;

public record DishCostResponse(
        Long menuItemId,
        String name,
        BigDecimal sellingPrice,
        BigDecimal ingredientCost,
        BigDecimal margin
) {
}
