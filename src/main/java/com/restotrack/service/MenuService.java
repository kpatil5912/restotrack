package com.restotrack.service;

import com.restotrack.common.BusinessRuleException;
import com.restotrack.common.NotFoundException;
import com.restotrack.entity.*;
import com.restotrack.repository.IngredientRepository;
import com.restotrack.repository.MenuItemRepository;
import com.restotrack.repository.StockMovementRepository;
import com.restotrack.dto.CreateMenuItemRequest;
import com.restotrack.dto.SellRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Use case 2: define dishes as recipes and sell them. Selling a dish depletes
 * every recipe ingredient from stock (converted to each ingredient's stock unit),
 * recorded as CONSUME movements. The whole sale is atomic: if any ingredient is
 * short, nothing is deducted.
 */
@Service
public class MenuService {

    private final MenuItemRepository menuItems;
    private final IngredientRepository ingredients;
    private final StockMovementRepository movements;

    public MenuService(MenuItemRepository menuItems, IngredientRepository ingredients,
                       StockMovementRepository movements) {
        this.menuItems = menuItems;
        this.ingredients = ingredients;
        this.movements = movements;
    }

    @Transactional
    public MenuItem create(CreateMenuItemRequest req) {
        MenuItem item = new MenuItem(req.name(), req.sellingPrice());
        for (CreateMenuItemRequest.RecipeLine line : req.recipe()) {
            Ingredient ingredient = ingredients.findById(line.ingredientId())
                    .orElseThrow(() -> new NotFoundException("Ingredient not found: " + line.ingredientId()));
            // Fail fast if the recipe unit can't be converted to the ingredient's stock unit.
            UnitConverter.convert(line.quantity(), line.unit(), ingredient.getStockUnit());
            item.addRecipeItem(new RecipeItem(ingredient, line.quantity(), line.unit()));
        }
        return menuItems.save(item);
    }

    @Transactional(readOnly = true)
    public List<MenuItem> findAll() {
        return menuItems.findAllWithRecipe();
    }

    @Transactional(readOnly = true)
    public MenuItem get(Long id) {
        return menuItems.findByIdWithRecipe(id)
                .orElseThrow(() -> new NotFoundException("Menu item not found: " + id));
    }

    /**
     * Sell {@code quantity} dishes and deplete stock accordingly.
     * Validates availability for all ingredients first, then applies the deductions,
     * so a shortfall on one ingredient leaves stock untouched.
     */
    @Transactional
    public void sell(SellRequest req) {
        MenuItem item = get(req.menuItemId());
        int dishes = req.quantity();

        // Compute required amount per ingredient in its stock unit.
        List<Deduction> deductions = new ArrayList<>();
        for (RecipeItem line : item.getRecipe()) {
            Ingredient ingredient = line.getIngredient();
            BigDecimal perDish = UnitConverter.convert(line.getQuantity(), line.getUnit(), ingredient.getStockUnit());
            BigDecimal required = perDish.multiply(BigDecimal.valueOf(dishes));
            if (ingredient.getCurrentStock().compareTo(required) < 0) {
                throw new BusinessRuleException(
                        "Insufficient stock for " + ingredient.getName()
                                + ": need " + required + " " + ingredient.getStockUnit()
                                + ", have " + ingredient.getCurrentStock());
            }
            deductions.add(new Deduction(ingredient, required));
        }

        // Apply after all checks pass.
        for (Deduction d : deductions) {
            d.ingredient.setCurrentStock(d.ingredient.getCurrentStock().subtract(d.amount));
            movements.save(new StockMovement(d.ingredient, MovementType.CONSUME, d.amount,
                    "sale of " + dishes + " x " + item.getName()));
        }
    }

    private record Deduction(Ingredient ingredient, BigDecimal amount) {
    }
}
