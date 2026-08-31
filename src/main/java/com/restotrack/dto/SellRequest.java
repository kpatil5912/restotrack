package com.restotrack.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SellRequest(
        @NotNull Long menuItemId,
        @NotNull @Min(1) Integer quantity
) {
}
