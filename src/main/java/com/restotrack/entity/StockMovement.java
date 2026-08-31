package com.restotrack.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Immutable ledger row. Every stock change (purchase, consume, waste, adjust) is
 * appended here and never modified. Quantity is always stored in the ingredient's
 * stock unit as a positive number; the movement type carries the sign.
 */
@Entity
@Table(name = "stock_movement")
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType type;

    /** Quantity in the ingredient's stock unit (positive). */
    @Column(nullable = false)
    private BigDecimal quantity;

    private String reason;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected StockMovement() {
    }

    public StockMovement(Ingredient ingredient, MovementType type, BigDecimal quantity, String reason) {
        this.ingredient = ingredient;
        this.type = type;
        this.quantity = quantity;
        this.reason = reason;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public MovementType getType() {
        return type;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public String getReason() {
        return reason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
