package com.browl.seacreatures.system;

import com.browl.seacreatures.model.Personality;
import com.browl.seacreatures.model.PersonalityTrait;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.browl.seacreatures.model.PersonalityTrait.*;

/**
 * A deliberately unscientific personality quiz used purely for entertainment and creature
 * assignment flavor. Nine questions, each answer nudging a handful of trait scores.
 */
public class PersonalityQuiz {
    private final List<QuizQuestion> questions = new ArrayList<>();

    public PersonalityQuiz() {
        questions.add(new QuizQuestion(
            "Your friend receives a message saying \"We need to talk.\" What do they do?",
            Arrays.asList(
                new QuizAnswer("Panic immediately", DRAMATIC, 3, CHAOS, 1),
                new QuizAnswer("Reply \"What happened?\"", SOCIAL, 2, CALM, 1),
                new QuizAnswer("Ignore it", LAZY, 2, INTROVERTED, 2),
                new QuizAnswer("Start preparing for war", COMPETITIVE, 3, CHAOS, 2),
                new QuizAnswer("Send a meme", CHAOS, 2, SOCIAL, 1))));

        questions.add(new QuizQuestion(
            "It's their turn to pick a restaurant. They:",
            Arrays.asList(
                new QuizAnswer("Pick the same place as always", LAZY, 2, CALM, 2),
                new QuizAnswer("Spend an hour comparing reviews", CURIOUS, 2, DRAMATIC, 1),
                new QuizAnswer("Pick the most expensive option", GREEDY, 3),
                new QuizAnswer("Ask the group chat and ignore every answer", SOCIAL, 2, CHAOS, 2))));

        questions.add(new QuizQuestion(
            "Someone challenges them to a race. They:",
            Arrays.asList(
                new QuizAnswer("Accept immediately, no context needed", COMPETITIVE, 3, ENERGETIC, 2),
                new QuizAnswer("Ask what they win", GREEDY, 2, COMPETITIVE, 1),
                new QuizAnswer("Politely decline and nap", LAZY, 3, CALM, 1),
                new QuizAnswer("Turn it into a whole dramatic event", DRAMATIC, 3, CHAOS, 1))));

        questions.add(new QuizQuestion(
            "Their phone battery hits 1%. They:",
            Arrays.asList(
                new QuizAnswer("Treat it as a national emergency", DRAMATIC, 3),
                new QuizAnswer("Shrug and keep going", CALM, 3),
                new QuizAnswer("Already have three power banks", COMPETITIVE, 1, GREEDY, 1),
                new QuizAnswer("Use it as an excuse to disappear", INTROVERTED, 3))));

        questions.add(new QuizQuestion(
            "At a party, they are most likely to be found:",
            Arrays.asList(
                new QuizAnswer("In the middle of everything", SOCIAL, 3, ENERGETIC, 2),
                new QuizAnswer("Investigating the host's bookshelf", CURIOUS, 3, INTROVERTED, 1),
                new QuizAnswer("Asleep on the couch", LAZY, 3),
                new QuizAnswer("Starting some kind of chaos", CHAOS, 3))));

        questions.add(new QuizQuestion(
            "They find a mysterious unlabeled button. They:",
            Arrays.asList(
                new QuizAnswer("Press it immediately", CURIOUS, 3, CHAOS, 2),
                new QuizAnswer("Research it for three hours first", CALM, 2, CURIOUS, 1),
                new QuizAnswer("Never even notice it", LAZY, 2, INTROVERTED, 1),
                new QuizAnswer("Dare someone else to press it", SOCIAL, 2, CHAOS, 1))));

        questions.add(new QuizQuestion(
            "Someone borrows their favorite item and returns it late. They:",
            Arrays.asList(
                new QuizAnswer("Never let it go, ever", DRAMATIC, 2, COMPETITIVE, 1),
                new QuizAnswer("Didn't notice it was gone", LAZY, 2, CALM, 1),
                new QuizAnswer("Immediately invoice them", GREEDY, 3),
                new QuizAnswer("Start a petty rivalry over it", COMPETITIVE, 2, CHAOS, 1))));

        questions.add(new QuizQuestion(
            "Their ideal weekend involves:",
            Arrays.asList(
                new QuizAnswer("A packed schedule with everyone invited", SOCIAL, 3, ENERGETIC, 2),
                new QuizAnswer("Total silence and zero plans", INTROVERTED, 3, CALM, 1),
                new QuizAnswer("Something mildly illegal but fun", CHAOS, 3),
                new QuizAnswer("Working on a side hustle", GREEDY, 2, COMPETITIVE, 1))));

        questions.add(new QuizQuestion(
            "When something goes wrong, they:",
            Arrays.asList(
                new QuizAnswer("Make it everyone's problem", DRAMATIC, 3, SOCIAL, 1),
                new QuizAnswer("Solve it quietly and never mention it", CALM, 3, INTROVERTED, 1),
                new QuizAnswer("Find it hilarious", CHAOS, 2, ENERGETIC, 1),
                new QuizAnswer("Blame the nearest inanimate object", DRAMATIC, 1, CHAOS, 2))));
    }

    public List<QuizQuestion> getQuestions() {
        return questions;
    }

    public Personality computePersonality(List<QuizAnswer> chosenAnswers) {
        Personality personality = new Personality();
        for (QuizAnswer answer : chosenAnswers) {
            for (var entry : answer.getEffects().entrySet()) {
                personality.add(entry.getKey(), entry.getValue());
            }
        }
        // Scale up to a friendlier 0-100 display range, then clamp.
        for (PersonalityTrait trait : PersonalityTrait.values()) {
            personality.set(trait, personality.get(trait) * 6 + 10);
        }
        personality.clampAllTo0_100();
        return personality;
    }
}
