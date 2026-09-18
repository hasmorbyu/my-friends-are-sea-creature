package com.browl.seacreatures.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;
import com.browl.seacreatures.system.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A tiny "tap the bubble before it disappears" mini-game. Rewards coins and happiness for all
 * friends. Deliberately simple: bubbles are plain generated-texture Images placed at random
 * stage positions, each with its own disappear timer.
 */
public class BubblePopDialog {
    private final Stage stage;
    private final UiFactory uiFactory;
    private final Random random = new Random();
    private Texture bubbleTexture;

    public BubblePopDialog(Stage stage, UiFactory uiFactory) {
        this.stage = stage;
        this.uiFactory = uiFactory;
    }

    public void show(GameState state, Runnable onChanged) {
        Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();
        pixmap.setColor(new Color(0.6f, 0.85f, 1f, 0.75f));
        pixmap.fillCircle(32, 32, 30);
        bubbleTexture = new Texture(pixmap);
        pixmap.dispose();

        Window window = new Window("Bubble Pop", uiFactory.getSkin());
        window.setModal(true);
        window.setSize(500, 400);
        window.setPosition(
            (stage.getViewport().getWorldWidth() - window.getWidth()) / 2f,
            (stage.getViewport().getWorldHeight() - window.getHeight()) / 2f);

        int[] score = {0};
        Label scoreLabel = new Label("Coins popped: 0", uiFactory.getSkin());
        scoreLabel.setPosition(20, 340);
        window.addActor(scoreLabel);

        List<Timer.Task> spawnTasks = new ArrayList<>();
        boolean[] running = {true};

        Runnable spawnBubble = () -> {
            if (!running[0]) return;
            Image bubble = new Image(new TextureRegionDrawable(bubbleTexture));
            float bx = 20 + random.nextFloat() * 420;
            float by = 40 + random.nextFloat() * 260;
            bubble.setPosition(bx, by);
            bubble.setSize(48, 48);
            bubble.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    bubble.remove();
                    score[0]++;
                    scoreLabel.setText("Coins popped: " + score[0]);
                }
            });
            window.addActor(bubble);
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    bubble.remove();
                }
            }, 1.2f);
        };

        Timer.Task spawner = Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                spawnBubble.run();
            }
        }, 0.2f, 0.45f);
        spawnTasks.add(spawner);

        TextButton finish = new TextButton("Finish", uiFactory.getSkin());
        finish.setPosition(190, 20);
        finish.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                running[0] = false;
                for (Timer.Task task : spawnTasks) task.cancel();
                state.addCoins(score[0] * 2);
                for (var friend : state.getFriends()) {
                    friend.setHappiness(friend.getHappiness() + Math.min(10, score[0]));
                }
                state.notify("Bubble Pop finished: " + score[0] + " bubbles popped, " + (score[0] * 2) + " coins earned!");
                onChanged.run();
                window.remove();
                bubbleTexture.dispose();
            }
        });
        window.addActor(finish);

        stage.addActor(window);
    }
}
