package com.browl.seacreatures.system;

import com.browl.seacreatures.model.PersonalityTrait;

import java.util.EnumMap;
import java.util.Map;

public class QuizAnswer {
    private final String text;
    private final Map<PersonalityTrait, Integer> effects;

    public QuizAnswer(String text, Object... traitWeightPairs) {
        this.text = text;
        this.effects = new EnumMap<>(PersonalityTrait.class);
        for (int i = 0; i < traitWeightPairs.length; i += 2) {
            effects.put((PersonalityTrait) traitWeightPairs[i], (Integer) traitWeightPairs[i + 1]);
        }
    }

    public String getText() {
        return text;
    }

    public Map<PersonalityTrait, Integer> getEffects() {
        return effects;
    }
}
