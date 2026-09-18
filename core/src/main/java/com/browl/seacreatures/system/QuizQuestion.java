package com.browl.seacreatures.system;

import java.util.List;

public class QuizQuestion {
    private final String text;
    private final List<QuizAnswer> answers;

    public QuizQuestion(String text, List<QuizAnswer> answers) {
        this.text = text;
        this.answers = answers;
    }

    public String getText() {
        return text;
    }

    public List<QuizAnswer> getAnswers() {
        return answers;
    }
}
