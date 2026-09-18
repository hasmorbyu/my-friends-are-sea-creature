package com.browl.seacreatures.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.browl.seacreatures.model.CreatureType;
import com.browl.seacreatures.model.Friend;
import com.browl.seacreatures.model.Personality;
import com.browl.seacreatures.system.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Walks the player through: name -> photo -> quiz -> creature reveal -> added to aquarium.
 * Implemented as a single reusable overlay Window with swappable content, rather than several
 * separate Screens, to keep the add-friend flow's state in one place.
 */
public class AddFriendDialog {
    public interface OnFriendAdded {
        void accept(Friend friend);
    }

    private final Stage stage;
    private final UiFactory uiFactory;
    private final SaveManager saveManager;
    private final PhotoProvider photoProvider;
    private final PersonalityQuiz quiz = new PersonalityQuiz();
    private final CreatureAssigner creatureAssigner = new CreatureAssigner();
    private final Random random = new Random();

    private final Window window;
    private final Table content = new Table();

    private String friendName = "";
    private String photoRelativePath = null;
    private final List<QuizAnswer> chosenAnswers = new ArrayList<>();
    private int questionIndex = 0;

    public AddFriendDialog(Stage stage, UiFactory uiFactory, SaveManager saveManager, PhotoProvider photoProvider) {
        this.stage = stage;
        this.uiFactory = uiFactory;
        this.saveManager = saveManager;
        this.photoProvider = photoProvider;

        window = new Window("Add Friend", uiFactory.getSkin());
        window.setModal(true);
        window.setMovable(false);
        window.add(content).width(520).pad(20);
        window.pack();
    }

    public void show(OnFriendAdded onFriendAdded) {
        friendName = "";
        photoRelativePath = null;
        chosenAnswers.clear();
        questionIndex = 0;
        showNameStep(onFriendAdded);
        stage.addActor(window);
        centerWindow();
    }

    private void centerWindow() {
        window.setPosition(
            (stage.getViewport().getWorldWidth() - window.getWidth()) / 2f,
            (stage.getViewport().getWorldHeight() - window.getHeight()) / 2f);
    }

    private void rebuild(OnFriendAdded onFriendAdded, Runnable builder) {
        content.clear();
        builder.run();
        window.pack();
        centerWindow();
    }

    private void showNameStep(OnFriendAdded onFriendAdded) {
        rebuild(onFriendAdded, () -> {
            content.add(new Label("What's your friend's name?", uiFactory.getSkin())).colspan(2).row();
            TextField nameField = new TextField("", uiFactory.getSkin());
            content.add(nameField).width(400).colspan(2).padTop(10).row();

            TextButton next = new TextButton("Next", uiFactory.getSkin());
            next.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    String typed = nameField.getText().trim();
                    friendName = typed.isEmpty() ? "Mystery Friend" : typed;
                    showPhotoStep(onFriendAdded);
                }
            });
            content.add(next).padTop(20);
        });
    }

    private void showPhotoStep(OnFriendAdded onFriendAdded) {
        rebuild(onFriendAdded, () -> {
            content.add(new Label("Choose a photo of " + friendName + " (optional).", uiFactory.getSkin()))
                .colspan(2).row();

            Label status = new Label(photoRelativePath == null ? "No photo selected yet." : "Photo selected!", uiFactory.getSkin());
            content.add(status).colspan(2).padTop(10).row();

            TextButton choosePhoto = new TextButton("Choose Photo", uiFactory.getSkin());
            choosePhoto.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    photoProvider.pickPhoto(saveManager.getPhotosDir(), new PhotoProvider.Callback() {
                        @Override
                        public void onPicked(String relativePath) {
                            photoRelativePath = relativePath;
                            Gdx.app.postRunnable(() -> showPhotoStep(onFriendAdded));
                        }

                        @Override
                        public void onCancelledOrFailed() {
                            // Keep whatever photo (or lack of one) was already chosen.
                        }
                    });
                }
            });
            content.add(choosePhoto).padTop(10);

            TextButton next = new TextButton("Next", uiFactory.getSkin());
            next.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showQuizStep(onFriendAdded);
                }
            });
            content.add(next).padTop(10);
        });
    }

    private void showQuizStep(OnFriendAdded onFriendAdded) {
        if (questionIndex >= quiz.getQuestions().size()) {
            showReveal(onFriendAdded);
            return;
        }
        QuizQuestion question = quiz.getQuestions().get(questionIndex);
        rebuild(onFriendAdded, () -> {
            Label progress = new Label("Question " + (questionIndex + 1) + " / " + quiz.getQuestions().size(), uiFactory.getSkin());
            content.add(progress).colspan(1).row();

            Label questionLabel = new Label(question.getText(), uiFactory.getSkin());
            questionLabel.setWrap(true);
            questionLabel.setAlignment(Align.center);
            content.add(questionLabel).width(460).padTop(10).row();

            for (QuizAnswer answer : question.getAnswers()) {
                TextButton answerButton = new TextButton(answer.getText(), uiFactory.getSkin());
                answerButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        chosenAnswers.add(answer);
                        questionIndex++;
                        showQuizStep(onFriendAdded);
                    }
                });
                content.add(answerButton).width(400).padTop(8).row();
            }
        });
    }

    private void showReveal(OnFriendAdded onFriendAdded) {
        Personality personality = quiz.computePersonality(chosenAnswers);
        CreatureType creature = creatureAssigner.assign(personality);

        Friend friend = new Friend(friendName);
        friend.setPhotoPath(photoRelativePath);
        friend.setPersonality(personality);
        friend.setCreatureType(creature);
        friend.setX(100 + random.nextFloat() * 400);
        friend.setY(100 + random.nextFloat() * 200);

        rebuild(onFriendAdded, () -> {
            content.add(new Label("ANALYZING PERSONALITY...", uiFactory.getSkin())).row();
            content.add(new Label(friendName + " IS...", uiFactory.getSkin())).padTop(10).row();
            content.add(new Label(creature.getDisplayName().toUpperCase(), uiFactory.getSkin())).padTop(4).row();
            Label description = new Label("\"" + creature.randomFunnyLine(random) + "\"", uiFactory.getSkin());
            description.setWrap(true);
            content.add(description).width(400).padTop(10).row();

            TextButton done = new TextButton("Add to Aquarium", uiFactory.getSkin());
            done.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    window.remove();
                    onFriendAdded.accept(friend);
                }
            });
            content.add(done).padTop(20);
        });
    }
}
