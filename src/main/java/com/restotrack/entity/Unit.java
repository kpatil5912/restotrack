package com.restotrack.entity;

/**
 * Supported units of measure. Each unit belongs to a family (WEIGHT, VOLUME, COUNT)
 * and carries a factor to a base unit within that family so recipe quantities can be
 * converted to stock quantities.
 *
 * Base units: GRAM (weight), MILLILITRE (volume), PIECE (count).
 */
public enum Unit {
    GRAM(UnitFamily.WEIGHT, 1.0),
    KILOGRAM(UnitFamily.WEIGHT, 1000.0),
    MILLILITRE(UnitFamily.VOLUME, 1.0),
    LITRE(UnitFamily.VOLUME, 1000.0),
    PIECE(UnitFamily.COUNT, 1.0);

    private final UnitFamily family;
    private final double toBaseFactor;

    Unit(UnitFamily family, double toBaseFactor) {
        this.family = family;
        this.toBaseFactor = toBaseFactor;
    }

    public UnitFamily getFamily() {
        return family;
    }

    public double getToBaseFactor() {
        return toBaseFactor;
    }

    public enum UnitFamily {
        WEIGHT, VOLUME, COUNT
    }
}
