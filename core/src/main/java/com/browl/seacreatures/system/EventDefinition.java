package com.browl.seacreatures.system;

/** Data describing one possible random event. The event engine is generic; events are just data + an effect. */
public class EventDefinition {
    private final String id;
    private final double probabilityPerRoll;
    private final boolean requiresTwoFriends;
    private final float cooldownSeconds;
    private final EventEffect effect;

    private float cooldownRemaining = 0f;

    public EventDefinition(String id, double probabilityPerRoll, boolean requiresTwoFriends,
                            float cooldownSeconds, EventEffect effect) {
        this.id = id;
        this.probabilityPerRoll = probabilityPerRoll;
        this.requiresTwoFriends = requiresTwoFriends;
        this.cooldownSeconds = cooldownSeconds;
        this.effect = effect;
    }

    public String getId() {
        return id;
    }

    public double getProbabilityPerRoll() {
        return probabilityPerRoll;
    }

    public boolean requiresTwoFriends() {
        return requiresTwoFriends;
    }

    public boolean isOnCooldown() {
        return cooldownRemaining > 0f;
    }

    public void tickCooldown(float deltaSeconds) {
        cooldownRemaining = Math.max(0f, cooldownRemaining - deltaSeconds);
    }

    public void resetCooldown() {
        cooldownRemaining = cooldownSeconds;
    }

    public EventEffect getEffect() {
        return effect;
    }
}
