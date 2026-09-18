package com.browl.seacreatures.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.Pixmap.Format;

/**
 * Builds a small, self-contained scene2d Skin at runtime from generated pixmaps and the built-in
 * bitmap font, so the game needs no external UI atlas/skin JSON to feel colorful and readable.
 */
public class UiFactory {
    private final Skin skin;
    private final Texture whiteTexture;
    private final BitmapFont font;

    public UiFactory() {
        Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whiteTexture = new Texture(pixmap);
        pixmap.dispose();

        font = new BitmapFont();
        font.getData().setScale(1.4f);

        skin = new Skin();
        skin.add("white", whiteTexture);
        skin.add("default-font", font);

        NinePatch panelPatch = new NinePatch(whiteTexture, 0, 0, 0, 0);
        skin.add("panel", new NinePatchDrawable(panelPatch));

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = coloredDrawable(new Color(0.20f, 0.55f, 0.85f, 1f));
        buttonStyle.down = coloredDrawable(new Color(0.12f, 0.40f, 0.65f, 1f));
        buttonStyle.over = coloredDrawable(new Color(0.28f, 0.62f, 0.92f, 1f));
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        skin.add("default", buttonStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        skin.add("default", labelStyle);

        Window.WindowStyle windowStyle = new Window.WindowStyle(font, Color.WHITE,
            coloredDrawable(new Color(0.05f, 0.15f, 0.30f, 0.92f)));
        skin.add("default", windowStyle);

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.background = coloredDrawable(new Color(0.15f, 0.25f, 0.35f, 1f));
        textFieldStyle.cursor = coloredDrawable(Color.WHITE);
        textFieldStyle.selection = coloredDrawable(new Color(0.3f, 0.5f, 0.8f, 0.5f));
        skin.add("default", textFieldStyle);
    }

    private com.badlogic.gdx.scenes.scene2d.utils.Drawable coloredDrawable(Color color) {
        Pixmap pixmap = new Pixmap(8, 8, Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(texture);
    }

    public Skin getSkin() {
        return skin;
    }

    public BitmapFont getFont() {
        return font;
    }

    public void dispose() {
        skin.dispose();
        whiteTexture.dispose();
        font.dispose();
    }
}
