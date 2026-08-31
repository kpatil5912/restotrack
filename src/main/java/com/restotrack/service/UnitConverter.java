package com.restotrack.service;

import com.restotrack.common.BusinessRuleException;
import com.restotrack.entity.Unit;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Converts a quantity from one unit to another within the same family
 * (weight, volume, count). Recipes are often authored in grams/millilitres
 * while stock is held in kilograms/litres, so every depletion must convert.
 */
public final class UnitConverter {

    private UnitConverter() {
    }

    /**
     * Convert {@code quantity} expressed in {@code from} into the equivalent
     * amount expressed in {@code to}.
     *
     * @throws BusinessRuleException if the units belong to different families
     */
    public static BigDecimal convert(BigDecimal quantity, Unit from, Unit to) {
        if (from.getFamily() != to.getFamily()) {
            throw new BusinessRuleException(
                    "Cannot convert " + from + " to " + to + ": incompatible unit families ("
                            + from.getFamily() + " vs " + to.getFamily() + ")");
        }
        if (from == to) {
            return quantity;
        }
        // value in base unit, then into the target unit
        BigDecimal inBase = quantity.multiply(BigDecimal.valueOf(from.getToBaseFactor()));
        return inBase.divide(BigDecimal.valueOf(to.getToBaseFactor()), 6, RoundingMode.HALF_UP);
    }
}
