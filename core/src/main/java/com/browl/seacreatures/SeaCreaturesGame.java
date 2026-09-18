package com.browl.seacreatures;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.browl.seacreatures.screen.AquariumScreen;
import com.browl.seacreatures.system.EventManager;
import com.browl.seacreatures.system.GameState;
import com.browl.seacreatures.system.ImageManager;
import com.browl.seacreatures.system.PhotoProvider;
import com.browl.seacreatures.system.RelationshipManager;
import com.browl.seacreatures.system.SaveManager;
import com.browl.seacreatures.ui.UiFactory;

import java.io.File;

/** LibGDX application entry point shared by desktop and Android. Owns the persistent GameState. */
public class SeaCreaturesGame extends Game {
    private final PhotoProvider photoProvider;

    private GameState gameState;
    private SaveManager saveManager;
    private ImageManager imageManager;
    private EventManager eventManager;
    private UiFactory uiFactory;

    public SeaCreaturesGame(PhotoProvider photoProvider) {
        this.photoProvider = photoProvider;
    }

    @Override
    public void create() {
        File dataDir = new File(Gdx.files.getLocalStoragePath(), "SeaCreaturesSave");
        saveManager = new SaveManager(dataDir);
        gameState = saveManager.load();
        imageManager = new ImageManager(saveManager.getPhotosDir());
        eventManager = new EventManager();
        uiFactory = new UiFactory();

        setScreen(new AquariumScreen(this));
    }

    public GameState getGameState() {
        return gameState;
    }

    public SaveManager getSaveManager() {
        return saveManager;
    }

    public ImageManager getImageManager() {
        return imageManager;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public PhotoProvider getPhotoProvider() {
        return photoProvider;
    }

    public UiFactory getUiFactory() {
        return uiFactory;
    }

    public void save() {
        saveManager.save(gameState);
    }

    @Override
    public void dispose() {
        save();
        imageManager.dispose();
        uiFactory.dispose();
        super.dispose();
    }
}
