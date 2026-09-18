package com.browl.seacreatures.system;

import com.browl.seacreatures.model.Friend;

/** Applies the four care actions (feed/play/clean/rest) and passive stat decay over time. */
public final class StatManager {
    private StatManager() {
    }

    public static void feed(Friend friend) {
        friend.setHunger(friend.getHunger() + 20);
        friend.setHappiness(friend.getHappiness() + 5);
        friend.logEvent(friend.getName() + " ate. Crisis averted, for now.");
    }

    public static void play(Friend friend) {
        friend.setHappiness(friend.getHappiness() + 20);
        friend.setEnergy(friend.getEnergy() - 15);
        friend.logEvent(friend.getName() + " played and had a great time.");
    }

    public static void clean(Friend friend) {
        friend.setCleanliness(friend.getCleanliness() + 30);
        friend.setHappiness(friend.getHappiness() + 5);
        friend.logEvent(friend.getName() + " is sparkling clean.");
    }

    public static void rest(Friend friend) {
        friend.setEnergy(friend.getEnergy() + 30);
        friend.logEvent(friend.getName() + " took a nap.");
    }

    /** Slow passive decay so stats matter over a play session without punishing short absences. */
    public static void decay(Friend friend, float deltaSeconds) {
        float minutes = deltaSeconds / 60f;
        friend.setHunger((int) (friend.getHunger() - minutes * 2f));
        friend.setCleanliness((int) (friend.getCleanliness() - minutes * 1.5f));
        friend.setEnergy((int) (friend.getEnergy() - minutes * 1f));

        int neglectPenalty = 0;
        if (friend.getHunger() < 20) neglectPenalty++;
        if (friend.getCleanliness() < 20) neglectPenalty++;
        if (friend.getEnergy() < 20) neglectPenalty++;
        if (neglectPenalty > 0) {
            friend.setHappiness((int) (friend.getHappiness() - minutes * neglectPenalty));
        } else {
            friend.setHealth(Math.min(100, friend.getHealth() + 1));
        }

        // Neglect is annoying, never fatal or permanent - friends can always be nursed back up.
        if (friend.getHunger() < 30 || friend.getCleanliness() < 30 || friend.getEnergy() < 20) {
            friend.setHealth((int) (friend.getHealth() - minutes * 0.5f));
        }
    }

    public static String neglectMessage(Friend friend) {
        if (friend.getHunger() < 20) {
            return friend.getName() + " has begun judging you for the lack of food.";
        }
        if (friend.getCleanliness() < 20) {
            return "The aquarium has filed a complaint about " + friend.getName() + "'s cleanliness.";
        }
        if (friend.getEnergy() < 20) {
            return friend.getName() + " is exhausted and side-eyeing the other fish.";
        }
        return null;
    }
}
