package com.browl.seacreatures.system;

import com.browl.seacreatures.model.Friend;

import java.util.ArrayList;
import java.util.List;

/** The entire persisted state of a save file: friends, relationships, coins, aquarium level, settings. */
public class GameState {
    private List<Friend> friends = new ArrayList<>();
    private RelationshipManager relationshipManager = new RelationshipManager();
    private int coins = 50;
    private AquariumUpgrade aquariumUpgrade = AquariumUpgrade.BASIC_TANK;
    private GameSettings settings = new GameSettings();
    private List<String> recentNotifications = new ArrayList<>();

    public List<Friend> getFriends() {
        return friends;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }

    public Friend findFriend(String id) {
        for (Friend f : friends) {
            if (f.getId().equals(id)) return f;
        }
        return null;
    }

    public RelationshipManager getRelationshipManager() {
        return relationshipManager;
    }

    public void setRelationshipManager(RelationshipManager relationshipManager) {
        this.relationshipManager = relationshipManager;
    }

    public int getCoins() {
        return coins;
    }

    public void addCoins(int amount) {
        this.coins = Math.max(0, this.coins + amount);
    }

    public boolean spendCoins(int amount) {
        if (coins < amount) return false;
        coins -= amount;
        return true;
    }

    public AquariumUpgrade getAquariumUpgrade() {
        return aquariumUpgrade;
    }

    public void setAquariumUpgrade(AquariumUpgrade aquariumUpgrade) {
        this.aquariumUpgrade = aquariumUpgrade;
    }

    public GameSettings getSettings() {
        return settings;
    }

    public void setSettings(GameSettings settings) {
        this.settings = settings;
    }

    public void notify(String message) {
        recentNotifications.add(0, message);
        while (recentNotifications.size() > 30) {
            recentNotifications.remove(recentNotifications.size() - 1);
        }
    }

    public List<String> getRecentNotifications() {
        return recentNotifications;
    }

    public void setRecentNotifications(List<String> recentNotifications) {
        this.recentNotifications = recentNotifications;
    }
}
