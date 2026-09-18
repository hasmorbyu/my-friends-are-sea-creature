package com.browl.seacreatures.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Map;

/**
 * Loads friend photos into round, cropped, aspect-preserving textures for placement on a
 * creature body, caching results so a texture is never created twice for the same photo.
 * Falls back to a generated placeholder face if the photo is missing or unreadable.
 */
public class ImageManager {
    private final java.io.File photosDir;
    private final Map<String, Texture> cache = new HashMap<>();
    private Texture placeholder;

    public ImageManager(java.io.File photosDir) {
        this.photosDir = photosDir;
    }

    public TextureRegion getFaceTexture(String relativePath) {
        String key = relativePath == null ? "__placeholder__" : relativePath;
        Texture cached = cache.get(key);
        if (cached != null) {
            return new TextureRegion(cached);
        }

        Texture texture = relativePath == null ? null : loadCroppedCircularTexture(relativePath);
        if (texture == null) {
            texture = getPlaceholder();
        }
        cache.put(key, texture);
        return new TextureRegion(texture);
    }

    private Texture loadCroppedCircularTexture(String relativePath) {
        try {
            FileHandle handle = Gdx.files.absolute(new java.io.File(this.photosDir, relativePath).getAbsolutePath());
            if (!handle.exists()) return null;

            Pixmap source = new Pixmap(handle);
            int size = Math.min(source.getWidth(), source.getHeight());
            int srcX = (source.getWidth() - size) / 2;
            int srcY = (source.getHeight() - size) / 2;

            int outSize = 128;
            Pixmap square = new Pixmap(outSize, outSize, Pixmap.Format.RGBA8888);
            square.drawPixmap(source, srcX, srcY, size, size, 0, 0, outSize, outSize);
            source.dispose();

            // Mask to a circle so it reads as a cartoon face rather than a pasted rectangle.
            Pixmap circular = new Pixmap(outSize, outSize, Pixmap.Format.RGBA8888);
            circular.setColor(0, 0, 0, 0);
            circular.fill();
            float radius = outSize / 2f;
            for (int y = 0; y < outSize; y++) {
                for (int x = 0; x < outSize; x++) {
                    float dx = x - radius;
                    float dy = y - radius;
                    if (dx * dx + dy * dy <= radius * radius) {
                        circular.drawPixel(x, y, square.getPixel(x, y));
                    }
                }
            }
            square.dispose();

            Texture texture = new Texture(circular);
            circular.dispose();
            return texture;
        } catch (Exception e) {
            Gdx.app.error("ImageManager", "Failed to load photo " + relativePath + ", using placeholder", e);
            return null;
        }
    }

    private Texture getPlaceholder() {
        if (placeholder == null) {
            int size = 128;
            Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
            pixmap.setColor(0, 0, 0, 0);
            pixmap.fill();
            pixmap.setColor(Color.LIGHT_GRAY);
            pixmap.fillCircle(size / 2, size / 2, size / 2);
            pixmap.setColor(Color.DARK_GRAY);
            pixmap.fillCircle(size / 2 - 20, size / 2 - 10, 8);
            pixmap.fillCircle(size / 2 + 20, size / 2 - 10, 8);
            pixmap.fillCircle(size / 2, size / 2 + 20, 14);
            placeholder = new Texture(pixmap);
            pixmap.dispose();
        }
        return placeholder;
    }

    public void dispose() {
        for (Texture t : cache.values()) {
            t.dispose();
        }
        cache.clear();
        if (placeholder != null) {
            placeholder.dispose();
            placeholder = null;
        }
    }
}
