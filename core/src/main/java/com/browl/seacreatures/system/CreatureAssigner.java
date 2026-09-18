package com.browl.seacreatures.system;

import com.browl.seacreatures.model.CreatureType;
import com.browl.seacreatures.model.Personality;
import com.browl.seacreatures.model.PersonalityTrait;

import java.util.Random;

/**
 * Turns a personality into a random creature. This is deliberately NOT a lookup table
 * (e.g. "if chaos > 80 then OCTOPUS") - instead each creature gets a compatibility score
 * from the personality, that score becomes a weight, and a weighted random roll picks the
 * winner. High compatibility makes a creature likely, never guaranteed.
 */
public class CreatureAssigner {
    private final Random random;

    public CreatureAssigner() {
        this(new Random());
    }

    public CreatureAssigner(Random random) {
        this.random = random;
    }

    public CreatureType assign(Personality personality) {
        CreatureType[] types = CreatureType.values();
        double[] weights = new double[types.length];
        double totalWeight = 0;

        for (int i = 0; i < types.length; i++) {
            double compatibility = 1; // baseline so every creature always has a chance
            for (var entry : types[i].getAffinity().entrySet()) {
                PersonalityTrait trait = entry.getKey();
                int affinityWeight = entry.getValue();
                compatibility += affinityWeight * (personality.get(trait) / 100.0);
            }
            // Randomness factor: personality influences, but does not fully determine, the result.
            double randomness = 0.5 + random.nextDouble();
            weights[i] = compatibility * randomness;
            totalWeight += weights[i];
        }

        double roll = random.nextDouble() * totalWeight;
        double cumulative = 0;
        for (int i = 0; i < types.length; i++) {
            cumulative += weights[i];
            if (roll <= cumulative) {
                return types[i];
            }
        }
        return types[types.length - 1];
    }
}
