package com.restotrack.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * A raw ingredient held in stock. Stock quantity is always expressed in the
 * ingredient's stockUnit. It is never edited directly except through the
 * stock-movement ledger; the @Version field guards against concurrent depletion.
 */
@Entity
@Table(name = "ingredient")
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Unit stockUnit;

    /** Current quantity on hand, in stockUnit. */
    @Column(nullable = false)
    private BigDecimal currentStock = BigDecimal.ZERO;

    /** Cost per one stockUnit, used for cost-per-dish and wastage-cost reports. */
    @Column(nullable = false)
    private BigDecimal costPerUnit = BigDecimal.ZERO;

    /** Alert threshold, in stockUnit. */
    @Column(nullable = false)
    private BigDecimal lowStockThreshold = BigDecimal.ZERO;

    @Version
    private Long version;

    protected Ingredient() {
    }

    public Ingredient(String name, Unit stockUnit, BigDecimal costPerUnit, BigDecimal lowStockThreshold) {
        this.name = name;
        this.stockUnit = stockUnit;
        this.costPerUnit = costPerUnit;
        this.lowStockThreshold = lowStockThreshold;
        this.currentStock = BigDecimal.ZERO;
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

    public Unit getStockUnit() {
        return stockUnit;
    }

    public void setStockUnit(Unit stockUnit) {
        this.stockUnit = stockUnit;
    }

    public BigDecimal getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(BigDecimal currentStock) {
        this.currentStock = currentStock;
    }

    public BigDecimal getCostPerUnit() {
        return costPerUnit;
    }

    public void setCostPerUnit(BigDecimal costPerUnit) {
        this.costPerUnit = costPerUnit;
    }

    public BigDecimal getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(BigDecimal lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    public Long getVersion() {
        return version;
    }
}
