package com.browl.seacreatures.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.browl.seacreatures.model.CreatureType;
import com.browl.seacreatures.model.Friend;
import com.browl.seacreatures.system.ImageManager;

/**
 * Draws a friend as a simple procedural cartoon body (via ShapeRenderer) with their real face
 * texture composited on top (via SpriteBatch). Body shapes are simplified silhouettes distinguished
 * by color, size and a few accent shapes per creature type - deliberately achievable without a
 * sprite/animation pipeline, per the "keep animation simple" project guidance.
 */
public class CreatureRenderer {
    private static final float BASE_SIZE = 60f;

    public static Color colorFor(CreatureType type) {
        switch (type) {
            case OCTOPUS: return new Color(0.85f, 0.35f, 0.55f, 1f);
            case CRAB: return new Color(0.90f, 0.30f, 0.20f, 1f);
            case SHARK: return new Color(0.55f, 0.60f, 0.65f, 1f);
            case PUFFERFISH: return new Color(0.95f, 0.80f, 0.30f, 1f);
            case SEA_TURTLE: return new Color(0.25f, 0.65f, 0.35f, 1f);
            case DOLPHIN: return new Color(0.40f, 0.60f, 0.85f, 1f);
            case JELLYFISH: return new Color(0.80f, 0.55f, 0.90f, 1f);
            case SQUID: return new Color(0.75f, 0.40f, 0.75f, 1f);
            case CLOWNFISH: return new Color(0.95f, 0.50f, 0.15f, 1f);
            case LOBSTER: return new Color(0.80f, 0.20f, 0.20f, 1f);
            case WHALE: return new Color(0.30f, 0.45f, 0.70f, 1f);
            case SHRIMP: return new Color(0.95f, 0.65f, 0.55f, 1f);
            default: return Color.LIGHT_GRAY;
        }
    }

    public static float sizeFor(CreatureType type) {
        switch (type) {
            case WHALE: return BASE_SIZE * 1.8f;
            case SHARK: return BASE_SIZE * 1.4f;
            case SEA_TURTLE:
            case DOLPHIN: return BASE_SIZE * 1.2f;
            case SHRIMP: return BASE_SIZE * 0.6f;
            default: return BASE_SIZE;
        }
    }

    /** Draws just the filled body + accents. Call between shapeRenderer.begin/end(Filled). */
    public void drawBody(ShapeRenderer sr, Friend friend, float animTime) {
        CreatureType type = friend.getCreatureType();
        float size = sizeFor(type);
        float x = friend.getX();
        float y = friend.getY() + (float) Math.sin(animTime * 2f + friend.getX()) * 3f;
        Color color = colorFor(type);
        sr.setColor(color);

        switch (type) {
            case OCTOPUS:
            case SQUID:
                sr.circle(x, y, size / 2f);
                sr.setColor(color.cpy().mul(0.85f, 0.85f, 0.85f, 1f));
                for (int i = 0; i < 5; i++) {
                    float angle = (float) (i * 0.5 + Math.sin(animTime * 3 + i) * 0.3);
                    float lx = x + (float) Math.cos(angle - 1.6) * size * 0.5f;
                    float ly = y - size * 0.4f - (float) Math.abs(Math.sin(angle)) * size * 0.3f;
                    sr.rectLine(x, y - size * 0.3f, lx, ly, 5f);
                }
                break;
            case CRAB:
            case LOBSTER:
                sr.ellipse(x - size / 2f, y - size * 0.35f, size, size * 0.7f);
                sr.setColor(color.cpy().mul(0.8f, 0.8f, 0.8f, 1f));
                sr.circle(x - size * 0.55f, y + size * 0.15f, size * 0.15f);
                sr.circle(x + size * 0.55f, y + size * 0.15f, size * 0.15f);
                break;
            case SHARK:
            case DOLPHIN:
                sr.ellipse(x - size / 2f, y - size * 0.3f, size, size * 0.6f);
                sr.triangle(x + size * 0.1f, y + size * 0.25f, x + size * 0.3f, y + size * 0.65f, x + size * 0.4f, y + size * 0.2f);
                sr.triangle(x - size / 2f, y, x - size * 0.8f, y + size * 0.15f, x - size * 0.8f, y - size * 0.15f);
                break;
            case PUFFERFISH:
                sr.circle(x, y, size / 2f);
                sr.setColor(color.cpy().mul(0.7f, 0.7f, 0.7f, 1f));
                for (int i = 0; i < 8; i++) {
                    float angle = (float) (i * Math.PI / 4);
                    float sx = x + (float) Math.cos(angle) * size * 0.5f;
                    float sy = y + (float) Math.sin(angle) * size * 0.5f;
                    float ex = x + (float) Math.cos(angle) * size * 0.7f;
                    float ey = y + (float) Math.sin(angle) * size * 0.7f;
                    sr.rectLine(sx, sy, ex, ey, 3f);
                }
                break;
            case SEA_TURTLE:
                sr.ellipse(x - size / 2f, y - size * 0.35f, size, size * 0.7f);
                sr.setColor(color.cpy().mul(0.7f, 0.9f, 0.7f, 1f));
                sr.circle(x - size * 0.55f, y + size * 0.05f, size * 0.18f);
                break;
            case JELLYFISH:
                sr.arc(x, y, size / 2f, 0, 180);
                sr.setColor(color.cpy().mul(0.9f, 0.9f, 0.9f, 1f));
                for (int i = -1; i <= 1; i++) {
                    float wobble = (float) Math.sin(animTime * 4 + i) * 6f;
                    sr.rectLine(x + i * size * 0.25f, y, x + i * size * 0.25f + wobble, y - size * 0.5f, 4f);
                }
                break;
            case CLOWNFISH:
                sr.ellipse(x - size / 2f, y - size * 0.35f, size, size * 0.7f);
                sr.setColor(Color.WHITE);
                sr.rectLine(x - size * 0.1f, y - size * 0.35f, x - size * 0.1f, y + size * 0.35f, 6f);
                sr.setColor(color);
                break;
            case WHALE:
                sr.ellipse(x - size / 2f, y - size * 0.32f, size, size * 0.64f);
                sr.triangle(x + size * 0.35f, y + size * 0.2f, x + size * 0.55f, y + size * 0.45f, x + size * 0.6f, y + size * 0.1f);
                break;
            case SHRIMP:
                sr.ellipse(x - size / 2f, y - size * 0.3f, size, size * 0.6f);
                break;
            default:
                sr.ellipse(x - size / 2f, y - size * 0.35f, size, size * 0.7f);
        }
    }

    /** Draws the composited face on top. Call between spriteBatch.begin/end(). */
    public void drawFace(SpriteBatch batch, Friend friend, ImageManager imageManager, float animTime) {
        TextureRegion face = imageManager.getFaceTexture(friend.getPhotoPath());
        float size = sizeFor(friend.getCreatureType()) * 0.55f;
        float x = friend.getX();
        float y = friend.getY() + (float) Math.sin(animTime * 2f + friend.getX()) * 3f;
        batch.draw(face, x - size / 2f, y - size / 2f, size, size);
    }
}
