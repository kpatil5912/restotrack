package com.restotrack.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * One line of a menu item's recipe: how much of an ingredient a single dish uses.
 * The quantity is expressed in {@link #unit}, which may differ from the ingredient's
 * stock unit (e.g. recipe in grams, stock in kilograms) but must be in the same family.
 */
@Entity
@Table(name = "recipe_item")
public class RecipeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Unit unit;

    protected RecipeItem() {
    }

    public RecipeItem(Ingredient ingredient, BigDecimal quantity, Unit unit) {
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.unit = unit;
    }

    public Long getId() {
        return id;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public Unit getUnit() {
        return unit;
    }
}
