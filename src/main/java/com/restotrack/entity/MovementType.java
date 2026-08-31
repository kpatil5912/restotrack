package com.restotrack.entity;

/**
 * Every change to ingredient stock is recorded as an immutable movement.
 * Current stock is derived from the sum of movements (signed by type).
 */
public enum MovementType {
    PURCHASE(+1),   // restock: stock increases
    CONSUME(-1),    // used by a sale (recipe depletion): stock decreases
    WASTE(-1),      // logged wastage: stock decreases
    ADJUST(0);      // manual correction: sign carried by the stored quantity

    private final int sign;

    MovementType(int sign) {
        this.sign = sign;
    }

    public int getSign() {
        return sign;
    }
}
