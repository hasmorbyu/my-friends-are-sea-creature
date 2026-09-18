package com.browl.seacreatures.model;

import java.util.EnumMap;
import java.util.Map;

/**
 * The catalogue of sea creatures a friend can become. Each entry is pure data: a description,
 * flavor text, and an "affinity" map used by {@link com.browl.seacreatures.system.CreatureAssigner}
 * to weight (never determine) which creature a friend's personality tends toward.
 */
public enum CreatureType {
    OCTOPUS("Octopus", "Curious and chaotic. Touches things it should not touch.",
        MovementStyle.DRIFT, "clams",
        new String[] {
            "I have eight arms and somehow still can't find my phone.",
            "I am not hiding. I am camouflaging emotionally."
        },
        affinity(t(PersonalityTrait.CHAOS, 3), t(PersonalityTrait.CURIOUS, 3), t(PersonalityTrait.DRAMATIC, 1))),

    CRAB("Crab", "Sideways-moving and confrontational about nothing in particular.",
        MovementStyle.SCUTTLE, "algae",
        new String[] {
            "I only move sideways. It's a personality, not a limitation.",
            "Honestly, this explains everything."
        },
        affinity(t(PersonalityTrait.COMPETITIVE, 3), t(PersonalityTrait.DRAMATIC, 2), t(PersonalityTrait.CHAOS, 1))),

    SHARK("Shark", "Intense, competitive, secretly wants everyone's approval.",
        MovementStyle.DART, "smaller fish (allegedly)",
        new String[] {
            "I have rows of backup teeth. Emotionally, I have zero backups.",
            "I have requested a promotion to Apex Manager."
        },
        affinity(t(PersonalityTrait.COMPETITIVE, 3), t(PersonalityTrait.ENERGETIC, 2), t(PersonalityTrait.DRAMATIC, 1))),

    PUFFERFISH("Pufferfish", "Calm until mildly inconvenienced, then enormous.",
        MovementStyle.DRIFT, "shrimp",
        new String[] {
            "I am not angry. I am simply 300% larger right now.",
            "I am offended by your choice of food."
        },
        affinity(t(PersonalityTrait.DRAMATIC, 3), t(PersonalityTrait.CALM, 2), t(PersonalityTrait.INTROVERTED, 1))),

    SEA_TURTLE("Sea Turtle", "Slow, wise, unbothered by deadlines.",
        MovementStyle.GLIDE, "seaweed",
        new String[] {
            "I am technically moving.",
            "I have finally reached the other side of the aquarium. It took a while."
        },
        affinity(t(PersonalityTrait.CALM, 3), t(PersonalityTrait.LAZY, 3), t(PersonalityTrait.INTROVERTED, 1))),

    DOLPHIN("Dolphin", "Social, energetic, the group chat's main character.",
        MovementStyle.DART, "fish",
        new String[] {
            "I do a flip every time someone likes my idea.",
            "I have organized a group activity nobody asked for."
        },
        affinity(t(PersonalityTrait.SOCIAL, 3), t(PersonalityTrait.ENERGETIC, 3), t(PersonalityTrait.DRAMATIC, 1))),

    JELLYFISH("Jellyfish", "Drifts through life with no plan and no spine, literally.",
        MovementStyle.PULSE, "plankton",
        new String[] {
            "I have no bones and no opinions.",
            "I am technically 95% water and 100% vibes."
        },
        affinity(t(PersonalityTrait.CALM, 3), t(PersonalityTrait.LAZY, 2), t(PersonalityTrait.INTROVERTED, 2))),

    SQUID("Squid", "Dramatic exits are a personal brand.",
        MovementStyle.DART, "crab legs",
        new String[] {
            "I left the conversation by releasing a cloud of ink.",
            "I contain multitudes and also a lot of ink."
        },
        affinity(t(PersonalityTrait.DRAMATIC, 3), t(PersonalityTrait.CHAOS, 2), t(PersonalityTrait.CURIOUS, 1))),

    CLOWNFISH("Clownfish", "Anxious but extremely loyal to its one plant.",
        MovementStyle.DART, "algae",
        new String[] {
            "I have never left my anemone and I never will.",
            "I panicked, then remembered where I live, then panicked again."
        },
        affinity(t(PersonalityTrait.INTROVERTED, 3), t(PersonalityTrait.SOCIAL, 1), t(PersonalityTrait.CALM, 1))),

    LOBSTER("Lobster", "Argues with everyone, means well eventually.",
        MovementStyle.SCUTTLE, "clams",
        new String[] {
            "I brought claws to a conversation.",
            "I am not defensive, I am pre-emptively correct."
        },
        affinity(t(PersonalityTrait.COMPETITIVE, 2), t(PersonalityTrait.DRAMATIC, 2), t(PersonalityTrait.GREEDY, 1))),

    WHALE("Whale", "Enormous, gentle, occasionally sings for four hours straight.",
        MovementStyle.GLIDE, "krill",
        new String[] {
            "I am large and in charge of absolutely nothing.",
            "I sing. It is a whole thing. You'll hear about it."
        },
        affinity(t(PersonalityTrait.CALM, 3), t(PersonalityTrait.SOCIAL, 1), t(PersonalityTrait.LAZY, 1))),

    SHRIMP("Shrimp", "Small, ambitious, unexpectedly rich.",
        MovementStyle.SCUTTLE, "literally anything",
        new String[] {
            "I have discovered capitalism and I am thriving.",
            "I am small but my portfolio is diversified."
        },
        affinity(t(PersonalityTrait.GREEDY, 3), t(PersonalityTrait.ENERGETIC, 2), t(PersonalityTrait.CURIOUS, 1)));

    private final String displayName;
    private final String personalityDescription;
    private final MovementStyle movementStyle;
    private final String preferredFood;
    private final String[] funnyLines;
    private final Map<PersonalityTrait, Integer> affinity;

    CreatureType(String displayName, String personalityDescription, MovementStyle movementStyle,
                 String preferredFood, String[] funnyLines, Map<PersonalityTrait, Integer> affinity) {
        this.displayName = displayName;
        this.personalityDescription = personalityDescription;
        this.movementStyle = movementStyle;
        this.preferredFood = preferredFood;
        this.funnyLines = funnyLines;
        this.affinity = affinity;
    }

    private static Object[] t(PersonalityTrait trait, int weight) {
        return new Object[] {trait, weight};
    }

    private static Map<PersonalityTrait, Integer> affinity(Object[]... pairs) {
        Map<PersonalityTrait, Integer> map = new EnumMap<>(PersonalityTrait.class);
        for (Object[] pair : pairs) {
            map.put((PersonalityTrait) pair[0], (Integer) pair[1]);
        }
        return map;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPersonalityDescription() {
        return personalityDescription;
    }

    public MovementStyle getMovementStyle() {
        return movementStyle;
    }

    public String getPreferredFood() {
        return preferredFood;
    }

    public String randomFunnyLine(java.util.Random random) {
        return funnyLines[random.nextInt(funnyLines.length)];
    }

    public Map<PersonalityTrait, Integer> getAffinity() {
        return affinity;
    }
}
