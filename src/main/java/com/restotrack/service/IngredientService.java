package com.restotrack.service;

import com.restotrack.common.NotFoundException;
import com.restotrack.entity.Ingredient;
import com.restotrack.entity.MovementType;
import com.restotrack.entity.StockMovement;
import com.restotrack.entity.Unit;
import com.restotrack.repository.IngredientRepository;
import com.restotrack.repository.StockMovementRepository;
import com.restotrack.dto.CreateIngredientRequest;
import com.restotrack.dto.RestockRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Use case 1: manage ingredients and restock them. All stock increases go
 * through the movement ledger so current stock always equals the ledger sum.
 */
@Service
public class IngredientService {

    private final IngredientRepository ingredients;
    private final StockMovementRepository movements;

    public IngredientService(IngredientRepository ingredients, StockMovementRepository movements) {
        this.ingredients = ingredients;
        this.movements = movements;
    }

    @Transactional
    public Ingredient create(CreateIngredientRequest req) {
        Ingredient ingredient = new Ingredient(
                req.name(), req.stockUnit(), req.costPerUnit(), req.lowStockThreshold());
        return ingredients.save(ingredient);
    }

    @Transactional(readOnly = true)
    public List<Ingredient> findAll() {
        return ingredients.findAll();
    }

    @Transactional(readOnly = true)
    public Ingredient get(Long id) {
        return ingredients.findById(id)
                .orElseThrow(() -> new NotFoundException("Ingredient not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Ingredient> findLowStock() {
        return ingredients.findLowStock();
    }

    /** Restock an ingredient. The purchased quantity may be in a different unit
     *  of the same family; it is converted to the stock unit before adding. */
    @Transactional
    public Ingredient restock(Long id, RestockRequest req) {
        Ingredient ingredient = get(id);
        BigDecimal inStockUnit = UnitConverter.convert(req.quantity(), req.unit(), ingredient.getStockUnit());
        ingredient.setCurrentStock(ingredient.getCurrentStock().add(inStockUnit));
        movements.save(new StockMovement(ingredient, MovementType.PURCHASE, inStockUnit,
                req.note() == null ? "restock" : req.note()));
        return ingredient;
    }
}
