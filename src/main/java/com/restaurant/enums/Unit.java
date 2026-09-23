package com.restaurant.enums;

public enum Unit {
    // Mass units (Base: GRAM)
    GRAM("g", Dimension.MASS, 1.0),
    KILOGRAM("kg", Dimension.MASS, 1000.0),

    // Volume units (Base: MILLILITER)
    MILLILITER("ml", Dimension.VOLUME, 1.0),
    LITER("l", Dimension.VOLUME, 1000.0),
    TEASPOON("tsp", Dimension.VOLUME, 5.0),
    TABLESPOON("tbsp", Dimension.VOLUME, 15.0),
    CUP("cup", Dimension.VOLUME, 240.0),

    // Discrete count units (Base: PIECE)
    PIECE("pc", Dimension.COUNT, 1.0),
    PINCH("pinch", Dimension.COUNT, 0.5);

    public enum Dimension {
        MASS,
        VOLUME,
        COUNT
    }

    private final String symbol;
    private final Dimension dimension;
    private final double conversionFactorToBase;

    Unit(String symbol, Dimension dimension, double conversionFactorToBase) {
        this.symbol = symbol;
        this.dimension = dimension;
        this.conversionFactorToBase = conversionFactorToBase;
    }

    public String getSymbol() {
        return symbol;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public double getConversionFactorToBase() {
        return conversionFactorToBase;
    }

    public boolean isCompatibleWith(Unit other) {
        return this.dimension == other.dimension;
    }
}

