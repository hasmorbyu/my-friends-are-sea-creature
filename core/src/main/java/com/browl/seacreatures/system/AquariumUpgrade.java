package com.browl.seacreatures.system;

public enum AquariumUpgrade {
    BASIC_TANK("Basic Tank", 0),
    LARGER_TANK("Larger Tank", 100),
    CORAL_REEF("Coral Reef", 250),
    TREASURE_CHEST("Treasure Chest", 400),
    SHIPWRECK("Shipwreck", 600),
    CASTLE("Castle", 900),
    DEEP_SEA_AREA("Deep Sea Area", 1300);

    private final String displayName;
    private final int cost;

    AquariumUpgrade(String displayName, int cost) {
        this.displayName = displayName;
        this.cost = cost;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getCost() {
        return cost;
    }

    public AquariumUpgrade next() {
        int nextOrdinal = ordinal() + 1;
        AquariumUpgrade[] values = values();
        return nextOrdinal < values.length ? values[nextOrdinal] : null;
    }
}
