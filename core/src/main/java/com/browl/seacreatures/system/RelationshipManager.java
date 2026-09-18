package com.browl.seacreatures.system;

import com.browl.seacreatures.model.RelationshipState;

import java.util.HashMap;
import java.util.Map;

/** Tracks a pairwise relationship score (-100..100) between every pair of friends, keyed by sorted friend ids. */
public class RelationshipManager {
    private Map<String, Integer> scores = new HashMap<>();

    private static String keyFor(String idA, String idB) {
        return idA.compareTo(idB) <= 0 ? idA + "|" + idB : idB + "|" + idA;
    }

    public int getScore(String idA, String idB) {
        Integer v = scores.get(keyFor(idA, idB));
        return v == null ? 0 : v;
    }

    public RelationshipState getState(String idA, String idB) {
        return RelationshipState.fromScore(getScore(idA, idB));
    }

    public void adjust(String idA, String idB, int delta) {
        if (idA.equals(idB)) {
            return;
        }
        String key = keyFor(idA, idB);
        int current = scores.getOrDefault(key, 0);
        int updated = Math.max(-100, Math.min(100, current + delta));
        scores.put(key, updated);
    }

    public Map<String, Integer> getScores() {
        return scores;
    }

    public void setScores(Map<String, Integer> scores) {
        this.scores = scores;
    }
}
