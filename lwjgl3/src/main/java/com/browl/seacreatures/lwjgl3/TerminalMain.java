package com.browl.seacreatures.lwjgl3;

import com.browl.seacreatures.model.CreatureType;
import com.browl.seacreatures.model.Friend;
import com.browl.seacreatures.model.Personality;
import com.browl.seacreatures.model.PersonalityTrait;
import com.browl.seacreatures.model.RelationshipState;
import com.browl.seacreatures.system.CreatureAssigner;
import com.browl.seacreatures.system.EventManager;
import com.browl.seacreatures.system.GameState;
import com.browl.seacreatures.system.PersonalityQuiz;
import com.browl.seacreatures.system.QuizAnswer;
import com.browl.seacreatures.system.QuizQuestion;
import com.browl.seacreatures.system.SaveManager;
import com.browl.seacreatures.system.StatManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * A lightweight text-only interface exercising the same shared core game logic as the graphical
 * app: adding friends, feeding/playing, relationships, random events and saving. No LibGDX
 * Application/graphics context is required, so this runs headless on any JVM.
 */
public class TerminalMain {
    private final Scanner scanner = new Scanner(System.in);
    private final Random random = new Random();
    private final PersonalityQuiz quiz = new PersonalityQuiz();
    private final CreatureAssigner creatureAssigner = new CreatureAssigner();
    private final EventManager eventManager = new EventManager();
    private final SaveManager saveManager;
    private final GameState state;

    public static void main(String[] args) {
        new TerminalMain().run();
    }

    public TerminalMain() {
        File dataDir = new File(System.getProperty("user.home"), ".seacreatures");
        saveManager = new SaveManager(dataDir);
        state = saveManager.load();
    }

    public void run() {
        System.out.println("=== MY FRIENDS ARE SEA CREATURES (terminal mode) ===");
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": addFriend(); break;
                case "2": viewFriends(); break;
                case "3": feedFriend(); break;
                case "4": playWithFriend(); break;
                case "5": viewRelationships(); break;
                case "6": triggerRandomEvent(); break;
                case "7": viewAquarium(); break;
                case "8": saveManager.save(state); System.out.println("Saved."); break;
                case "9": saveManager.save(state); running = false; break;
                default: System.out.println("Unknown option.");
            }
        }
        System.out.println("Bye! Your friends will be waiting in the aquarium.");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1. Add friend");
        System.out.println("2. View friends");
        System.out.println("3. Feed friend");
        System.out.println("4. Play with friend");
        System.out.println("5. View relationships");
        System.out.println("6. Trigger random event");
        System.out.println("7. View aquarium");
        System.out.println("8. Save");
        System.out.println("9. Exit");
        System.out.print("Choose: ");
    }

    private void addFriend() {
        System.out.print("Friend's name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) name = "Mystery Friend";

        System.out.println("(Terminal mode skips photo selection - use the graphical app for that.)");

        List<QuizAnswer> chosen = new ArrayList<>();
        for (QuizQuestion question : quiz.getQuestions()) {
            System.out.println(question.getText());
            List<QuizAnswer> answers = question.getAnswers();
            for (int i = 0; i < answers.size(); i++) {
                System.out.println("  " + (char) ('A' + i) + ". " + answers.get(i).getText());
            }
            System.out.print("Answer: ");
            String answerLine = scanner.nextLine().trim().toUpperCase();
            int index = answerLine.isEmpty() ? 0 : (answerLine.charAt(0) - 'A');
            if (index < 0 || index >= answers.size()) index = 0;
            chosen.add(answers.get(index));
        }

        Personality personality = quiz.computePersonality(chosen);
        CreatureType creature = creatureAssigner.assign(personality);

        System.out.println();
        System.out.println("ANALYZING PERSONALITY...");
        System.out.println(name.toUpperCase() + " IS...");
        System.out.println(creature.getDisplayName().toUpperCase());
        System.out.println("\"" + creature.randomFunnyLine(random) + "\"");

        Friend friend = new Friend(name);
        friend.setCreatureType(creature);
        friend.setPersonality(personality);
        state.getFriends().add(friend);
        state.notify(name + " has joined the aquarium as a " + creature.getDisplayName() + "!");
        System.out.println(name + " has been added to the aquarium.");
    }

    private void viewFriends() {
        if (state.getFriends().isEmpty()) {
            System.out.println("No friends yet. Add one!");
            return;
        }
        for (Friend f : state.getFriends()) {
            System.out.println("- " + f.getName() + " the " + f.getCreatureType().getDisplayName()
                + " | Health " + f.getHealth() + " Hunger " + f.getHunger() + " Happiness " + f.getHappiness()
                + " Cleanliness " + f.getCleanliness() + " Energy " + f.getEnergy());
            for (PersonalityTrait trait : PersonalityTrait.values()) {
                int v = f.getPersonality().get(trait);
                if (v >= 40) System.out.print("    " + trait + " " + v);
            }
            System.out.println();
        }
    }

    private Friend selectFriend() {
        if (state.getFriends().isEmpty()) {
            System.out.println("No friends yet.");
            return null;
        }
        for (int i = 0; i < state.getFriends().size(); i++) {
            System.out.println(i + ": " + state.getFriends().get(i).getName());
        }
        System.out.print("Which friend? ");
        try {
            int index = Integer.parseInt(scanner.nextLine().trim());
            return state.getFriends().get(index);
        } catch (Exception e) {
            System.out.println("Invalid selection.");
            return null;
        }
    }

    private void feedFriend() {
        Friend friend = selectFriend();
        if (friend == null) return;
        StatManager.feed(friend);
        System.out.println(friend.getName() + " has been fed. Hunger is now " + friend.getHunger() + ".");
    }

    private void playWithFriend() {
        Friend friend = selectFriend();
        if (friend == null) return;
        StatManager.play(friend);
        System.out.println("Played with " + friend.getName() + ". Happiness is now " + friend.getHappiness() + ".");
    }

    private void viewRelationships() {
        List<Friend> friends = state.getFriends();
        if (friends.size() < 2) {
            System.out.println("Add at least two friends to see relationships.");
            return;
        }
        for (int i = 0; i < friends.size(); i++) {
            for (int j = i + 1; j < friends.size(); j++) {
                Friend a = friends.get(i);
                Friend b = friends.get(j);
                int score = state.getRelationshipManager().getScore(a.getId(), b.getId());
                RelationshipState relState = RelationshipState.fromScore(score);
                System.out.println(a.getName() + " <-> " + b.getName() + " = " + score + " (" + relState + ")");
            }
        }
    }

    private void triggerRandomEvent() {
        List<String> messages = eventManager.update(state, 60f);
        if (messages.isEmpty()) {
            System.out.println("Nothing happened this time. Try again.");
        } else {
            messages.forEach(System.out::println);
        }
    }

    private void viewAquarium() {
        System.out.println("Aquarium tier: " + state.getAquariumUpgrade().getDisplayName());
        System.out.println("Coins: " + state.getCoins());
        System.out.println("Friends in the tank: " + state.getFriends().size());
    }
}
