package com.browl.seacreatures.model;

public enum RelationshipState {
    BESTIES,
    FRIENDS,
    NEUTRAL,
    RIVALS;

    public static RelationshipState fromScore(int score) {
        if (score >= 60) return BESTIES;
        if (score >= 20) return FRIENDS;
        if (score >= -20) return NEUTRAL;
        return RIVALS;
    }
}
