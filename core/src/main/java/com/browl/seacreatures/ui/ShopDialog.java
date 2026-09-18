package com.browl.seacreatures.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.browl.seacreatures.system.AquariumUpgrade;
import com.browl.seacreatures.system.GameState;

/** Lets the player spend coins to upgrade the aquarium to the next tier. */
public class ShopDialog {
    private final Stage stage;
    private final UiFactory uiFactory;

    public ShopDialog(Stage stage, UiFactory uiFactory) {
        this.stage = stage;
        this.uiFactory = uiFactory;
    }

    public void show(GameState state, Runnable onChanged) {
        Window window = new Window("Shop", uiFactory.getSkin());
        window.setModal(true);
        Table content = new Table();
        content.pad(16);

        content.add(new Label("Coins: " + state.getCoins(), uiFactory.getSkin())).row();
        content.add(new Label("Current tank: " + state.getAquariumUpgrade().getDisplayName(), uiFactory.getSkin())).padTop(6).row();

        AquariumUpgrade next = state.getAquariumUpgrade().next();
        if (next == null) {
            content.add(new Label("Your aquarium is fully upgraded!", uiFactory.getSkin())).padTop(10).row();
        } else {
            content.add(new Label("Next: " + next.getDisplayName() + " (" + next.getCost() + " coins)", uiFactory.getSkin()))
                .padTop(10).row();
            TextButton buy = new TextButton("Upgrade", uiFactory.getSkin());
            buy.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (state.spendCoins(next.getCost())) {
                        state.setAquariumUpgrade(next);
                        state.notify("Upgraded the aquarium to the " + next.getDisplayName() + "!");
                        onChanged.run();
                        window.remove();
                    }
                }
            });
            content.add(buy).padTop(10).row();
        }

        TextButton close = new TextButton("Close", uiFactory.getSkin());
        close.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                window.remove();
            }
        });
        content.add(close).padTop(16);

        window.add(content);
        window.pack();
        window.setPosition(
            (stage.getViewport().getWorldWidth() - window.getWidth()) / 2f,
            (stage.getViewport().getWorldHeight() - window.getHeight()) / 2f);
        stage.addActor(window);
    }
}
