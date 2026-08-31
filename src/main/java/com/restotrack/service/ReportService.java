package com.restotrack.service;

import com.restotrack.entity.MenuItem;
import com.restotrack.entity.RecipeItem;
import com.restotrack.dto.DishCostResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Cost-per-dish reporting: sums the cost of a dish's recipe ingredients
 * (recipe quantity converted to the ingredient's stock unit, times cost per unit)
 * and reports the margin against the selling price.
 */
@Service
public class ReportService {

    private final MenuService menuService;

    public ReportService(MenuService menuService) {
        this.menuService = menuService;
    }

    @Transactional(readOnly = true)
    public DishCostResponse dishCost(Long menuItemId) {
        MenuItem item = menuService.get(menuItemId);
        BigDecimal cost = BigDecimal.ZERO;
        for (RecipeItem line : item.getRecipe()) {
            BigDecimal inStockUnit = UnitConverter.convert(
                    line.getQuantity(), line.getUnit(), line.getIngredient().getStockUnit());
            cost = cost.add(inStockUnit.multiply(line.getIngredient().getCostPerUnit()));
        }
        BigDecimal margin = item.getSellingPrice().subtract(cost);
        return new DishCostResponse(item.getId(), item.getName(), item.getSellingPrice(), cost, margin);
    }
}
