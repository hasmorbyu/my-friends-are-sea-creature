package com.browl.seacreatures.system;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Saves/loads {@link GameState} as JSON on plain java.io.File paths. Deliberately avoids any
 * Gdx.app/Gdx.files dependency so it works identically in the graphical app and in headless
 * terminal mode. Corrupted or missing saves fall back to a fresh GameState rather than crashing.
 */
public class SaveManager {
    private final File saveFile;
    private final File photosDir;

    public SaveManager(File dataDirectory) {
        dataDirectory.mkdirs();
        this.saveFile = new File(dataDirectory, "savegame.json");
        this.photosDir = new File(dataDirectory, "photos");
        photosDir.mkdirs();
    }

    public File getPhotosDir() {
        return photosDir;
    }

    public void save(GameState state) {
        try {
            Json json = new Json(JsonWriter.OutputType.json);
            String output = json.prettyPrint(state);
            Files.write(saveFile.toPath(), output.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("Failed to save game: " + e.getMessage());
        }
    }

    public GameState load() {
        if (!saveFile.exists()) {
            return new GameState();
        }
        try {
            String content = new String(Files.readAllBytes(saveFile.toPath()), StandardCharsets.UTF_8);
            Json json = new Json();
            GameState state = json.fromJson(GameState.class, content);
            return state != null ? state : new GameState();
        } catch (Exception e) {
            System.err.println("Save file was corrupted, starting a fresh game: " + e.getMessage());
            return new GameState();
        }
    }
}
