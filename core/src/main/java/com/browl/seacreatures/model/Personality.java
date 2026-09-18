package com.browl.seacreatures.model;

import java.util.HashMap;
import java.util.Map;

/**
 * A friend's (not scientifically valid) personality scores, one integer per {@link PersonalityTrait}.
 * Scores are unbounded during accumulation and only clamped to 0-100 when finalized by the quiz.
 * Stored keyed by trait name (rather than the enum) so it serializes cleanly to JSON.
 */
public class Personality {
    private Map<String, Integer> scores = new HashMap<>();

    public Personality() {
        for (PersonalityTrait trait : PersonalityTrait.values()) {
            scores.put(trait.name(), 0);
        }
    }

    public void add(PersonalityTrait trait, int amount) {
        scores.put(trait.name(), get(trait) + amount);
    }

    public int get(PersonalityTrait trait) {
        Integer value = scores.get(trait.name());
        return value == null ? 0 : value;
    }

    public void set(PersonalityTrait trait, int value) {
        scores.put(trait.name(), value);
    }

    public void clampAllTo0_100() {
        for (PersonalityTrait trait : PersonalityTrait.values()) {
            int v = Math.max(0, Math.min(100, get(trait)));
            set(trait, v);
        }
    }

    public Map<String, Integer> getScores() {
        return scores;
    }

    public void setScores(Map<String, Integer> scores) {
        this.scores = scores;
    }
}
