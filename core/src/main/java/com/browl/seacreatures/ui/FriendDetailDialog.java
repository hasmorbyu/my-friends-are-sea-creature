package com.browl.seacreatures.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.browl.seacreatures.model.Friend;
import com.browl.seacreatures.model.PersonalityTrait;
import com.browl.seacreatures.model.RelationshipState;
import com.browl.seacreatures.system.GameState;
import com.browl.seacreatures.system.StatManager;

/** Shows one friend's stats, personality, relationships and care buttons in a popup window. */
public class FriendDetailDialog {
    private final Stage stage;
    private final UiFactory uiFactory;
    private Window window;

    public FriendDetailDialog(Stage stage, UiFactory uiFactory) {
        this.stage = stage;
        this.uiFactory = uiFactory;
    }

    public void show(Friend friend, GameState state, Runnable onChanged) {
        if (window != null) window.remove();
        window = new Window(friend.getName() + " the " + friend.getCreatureType().getDisplayName(), uiFactory.getSkin());
        window.setModal(true);

        Table content = new Table();
        content.pad(16);

        content.add(bar("Health", friend.getHealth())).colspan(2).row();
        content.add(bar("Hunger", friend.getHunger())).colspan(2).row();
        content.add(bar("Happiness", friend.getHappiness())).colspan(2).row();
        content.add(bar("Cleanliness", friend.getCleanliness())).colspan(2).row();
        content.add(bar("Energy", friend.getEnergy())).colspan(2).row();

        StringBuilder personalityText = new StringBuilder("Personality: ");
        for (PersonalityTrait trait : PersonalityTrait.values()) {
            int v = friend.getPersonality().get(trait);
            if (v >= 40) {
                personalityText.append(trait.name()).append(" ").append(v).append("  ");
            }
        }
        content.add(new Label(personalityText.toString(), uiFactory.getSkin())).colspan(2).padTop(10).row();

        StringBuilder relationships = new StringBuilder();
        for (Friend other : state.getFriends()) {
            if (other == friend) continue;
            RelationshipState rel = state.getRelationshipManager().getState(friend.getId(), other.getId());
            relationships.append(other.getName()).append(": ").append(rel).append("  ");
        }
        if (relationships.length() > 0) {
            content.add(new Label(relationships.toString(), uiFactory.getSkin())).colspan(2).padTop(6).row();
        }

        if (!friend.getEventHistory().isEmpty()) {
            content.add(new Label("Recent: " + friend.getEventHistory().get(0), uiFactory.getSkin()))
                .colspan(2).padTop(6).width(420).row();
        }

        Table buttons = new Table();
        buttons.add(careButton("Feed", () -> StatManager.feed(friend), onChanged)).pad(4);
        buttons.add(careButton("Play", () -> StatManager.play(friend), onChanged)).pad(4);
        buttons.add(careButton("Clean", () -> StatManager.clean(friend), onChanged)).pad(4);
        buttons.add(careButton("Rest", () -> StatManager.rest(friend), onChanged)).pad(4);
        content.add(buttons).colspan(2).padTop(16).row();

        TextButton close = new TextButton("Close", uiFactory.getSkin());
        close.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                window.remove();
            }
        });
        content.add(close).colspan(2).padTop(10);

        window.add(content);
        window.pack();
        window.setPosition(
            (stage.getViewport().getWorldWidth() - window.getWidth()) / 2f,
            (stage.getViewport().getWorldHeight() - window.getHeight()) / 2f);
        stage.addActor(window);
    }

    private Table bar(String label, int value) {
        Table row = new Table();
        row.add(new Label(label + " " + value, uiFactory.getSkin())).width(160).left();
        return row;
    }

    private TextButton careButton(String text, Runnable action, Runnable onChanged) {
        TextButton button = new TextButton(text, uiFactory.getSkin());
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
                onChanged.run();
                window.remove();
            }
        });
        return button;
    }
}
