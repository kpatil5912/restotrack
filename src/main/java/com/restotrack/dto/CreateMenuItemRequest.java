package com.restotrack.dto;

import com.restotrack.entity.Unit;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record CreateMenuItemRequest(
        @jakarta.validation.constraints.NotBlank String name,
        @NotNull @DecimalMin("0.0") BigDecimal sellingPrice,
        @NotEmpty @Valid List<RecipeLine> recipe
) {
    public record RecipeLine(
            @NotNull Long ingredientId,
            @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal quantity,
            @NotNull Unit unit
    ) {
    }
}
