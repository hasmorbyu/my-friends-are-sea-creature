package com.browl.seacreatures.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** A real-life friend, reincarnated as a virtual-pet sea creature. */
public class Friend {
    private String id = UUID.randomUUID().toString();
    private String name = "";
    /** Path relative to the save directory's photos/ folder, or null if no photo was set. */
    private String photoPath;
    private CreatureType creatureType = CreatureType.CLOWNFISH;
    private Personality personality = new Personality();

    private int health = 100;
    private int hunger = 100;
    private int happiness = 80;
    private int cleanliness = 100;
    private int energy = 100;

    private float ageSeconds = 0f;
    private int coinsEarned = 0;

    /** Aquarium position/animation state - not saved as part of core identity but persisted for continuity. */
    private float x = 0f;
    private float y = 0f;
    private float wanderAngle = 0f;

    private List<String> eventHistory = new ArrayList<>();

    public Friend() {
    }

    public Friend(String name) {
        this.name = name;
    }

    public void logEvent(String message) {
        eventHistory.add(0, message);
        while (eventHistory.size() > 20) {
            eventHistory.remove(eventHistory.size() - 1);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public CreatureType getCreatureType() {
        return creatureType;
    }

    public void setCreatureType(CreatureType creatureType) {
        this.creatureType = creatureType;
    }

    public Personality getPersonality() {
        return personality;
    }

    public void setPersonality(Personality personality) {
        this.personality = personality;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = clamp(health);
    }

    public int getHunger() {
        return hunger;
    }

    public void setHunger(int hunger) {
        this.hunger = clamp(hunger);
    }

    public int getHappiness() {
        return happiness;
    }

    public void setHappiness(int happiness) {
        this.happiness = clamp(happiness);
    }

    public int getCleanliness() {
        return cleanliness;
    }

    public void setCleanliness(int cleanliness) {
        this.cleanliness = clamp(cleanliness);
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = clamp(energy);
    }

    public static int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }

    public float getAgeSeconds() {
        return ageSeconds;
    }

    public void setAgeSeconds(float ageSeconds) {
        this.ageSeconds = ageSeconds;
    }

    public void addAge(float deltaSeconds) {
        this.ageSeconds += deltaSeconds;
    }

    public int getCoinsEarned() {
        return coinsEarned;
    }

    public void addCoinsEarned(int amount) {
        this.coinsEarned += amount;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWanderAngle() {
        return wanderAngle;
    }

    public void setWanderAngle(float wanderAngle) {
        this.wanderAngle = wanderAngle;
    }

    public List<String> getEventHistory() {
        return eventHistory;
    }

    public void setEventHistory(List<String> eventHistory) {
        this.eventHistory = eventHistory;
    }
}
