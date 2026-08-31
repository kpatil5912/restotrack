package com.restotrack.repository;

import com.restotrack.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    /** Ingredients whose current stock is at or below their low-stock threshold. */
    @Query("select i from Ingredient i where i.currentStock <= i.lowStockThreshold")
    List<Ingredient> findLowStock();
}
