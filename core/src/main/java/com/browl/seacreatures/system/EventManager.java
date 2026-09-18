package com.browl.seacreatures.system;

import com.browl.seacreatures.model.Friend;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Rolls a pool of data-driven {@link EventDefinition}s against the current game state and applies
 * whichever ones fire. New events are added by appending to the pool in the constructor - the
 * roll/apply loop itself never needs to change.
 */
public class EventManager {
    private final List<EventDefinition> pool = new ArrayList<>();
    private final Random random;

    public EventManager() {
        this(new Random());
    }

    public EventManager(Random random) {
        this.random = random;
        registerDefaultEvents();
    }

    private void registerDefaultEvents() {
        pool.add(new EventDefinition("food_theft", 0.10, true, 60f, (state, a, b) -> {
            a.setHunger(a.getHunger() + 10);
            b.setHunger(b.getHunger() - 10);
            state.getRelationshipManager().adjust(a.getId(), b.getId(), -10);
            return a.getName() + " stole " + b.getName() + "'s food.";
        }));

        pool.add(new EventDefinition("friendly_race", 0.10, true, 60f, (state, a, b) -> {
            a.setHappiness(a.getHappiness() + 10);
            b.setHappiness(b.getHappiness() + 5);
            state.getRelationshipManager().adjust(a.getId(), b.getId(), 5);
            state.addCoins(5);
            return a.getName() + " challenged " + b.getName() + " to a race.";
        }));

        pool.add(new EventDefinition("helped_clean", 0.08, true, 60f, (state, a, b) -> {
            b.setCleanliness(b.getCleanliness() + 15);
            state.getRelationshipManager().adjust(a.getId(), b.getId(), 8);
            return a.getName() + " helped " + b.getName() + " clean the aquarium.";
        }));

        pool.add(new EventDefinition("petty_argument", 0.08, true, 60f, (state, a, b) -> {
            a.setHappiness(a.getHappiness() - 5);
            b.setHappiness(b.getHappiness() - 5);
            state.getRelationshipManager().adjust(a.getId(), b.getId(), -5);
            return a.getName() + " and " + b.getName() + " are arguing over absolutely nothing.";
        }));

        pool.add(new EventDefinition("found_treasure", 0.10, false, 45f, (state, a, b) -> {
            int coins = 10 + random.nextInt(15);
            state.addCoins(coins);
            a.addCoinsEarned(coins);
            a.setHappiness(a.getHappiness() + 10);
            return a.getName() + " found " + coins + " coins hidden in the treasure chest.";
        }));

        pool.add(new EventDefinition("capitalism", 0.06, false, 90f, (state, a, b) -> {
            state.addCoins(8);
            a.addCoinsEarned(8);
            return a.getName() + " has discovered capitalism.";
        }));

        pool.add(new EventDefinition("spoon_collecting", 0.06, false, 90f, (state, a, b) ->
            a.getName() + " has misplaced seven things again."));

        pool.add(new EventDefinition("offended", 0.06, false, 90f, (state, a, b) -> {
            a.setHappiness(a.getHappiness() - 5);
            return a.getName() + " is offended by your choice of food.";
        }));

        pool.add(new EventDefinition("promotion_request", 0.05, false, 90f, (state, a, b) ->
            a.getName() + " has requested a promotion to Apex Manager."));

        pool.add(new EventDefinition("moved_chest", 0.05, false, 90f, (state, a, b) ->
            "Someone moved the treasure chest. Nobody will admit to it."));
    }

    /** Rolls all eligible events once. Returns the list of triggered event messages, in trigger order. */
    public List<String> update(GameState state, float deltaSeconds) {
        List<String> triggeredMessages = new ArrayList<>();
        List<Friend> friends = state.getFriends();

        for (EventDefinition event : pool) {
            event.tickCooldown(deltaSeconds);
            if (event.isOnCooldown()) continue;
            if (event.requiresTwoFriends() && friends.size() < 2) continue;
            if (friends.isEmpty()) continue;

            if (random.nextDouble() < event.getProbabilityPerRoll()) {
                Friend a = friends.get(random.nextInt(friends.size()));
                Friend b = null;
                if (event.requiresTwoFriends()) {
                    do {
                        b = friends.get(random.nextInt(friends.size()));
                    } while (b == a && friends.size() > 1);
                }
                String message = event.getEffect().apply(state, a, b);
                a.logEvent(message);
                if (b != null) b.logEvent(message);
                state.notify(message);
                triggeredMessages.add(message);
                event.resetCooldown();
            }
        }
        return triggeredMessages;
    }
}
