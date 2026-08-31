package com.restotrack.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * A sellable dish. Its recipe is the bill-of-materials of ingredients that get
 * depleted from stock each time the dish is sold.
 */
@Entity
@Table(name = "menu_item")
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private BigDecimal sellingPrice = BigDecimal.ZERO;

    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeItem> recipe = new ArrayList<>();

    protected MenuItem() {
    }

    public MenuItem(String name, BigDecimal sellingPrice) {
        this.name = name;
        this.sellingPrice = sellingPrice;
    }

    public void addRecipeItem(RecipeItem item) {
        item.setMenuItem(this);
        this.recipe.add(item);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public List<RecipeItem> getRecipe() {
        return recipe;
    }
}
