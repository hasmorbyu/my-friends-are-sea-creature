package com.browl.seacreatures.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.browl.seacreatures.SeaCreaturesGame;

/** Launches the desktop (Linux/Windows) build of the shared LibGDX game. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (args.length > 0 && (args[0].equals("--terminal") || args[0].equals("-t"))) {
            TerminalMain.main(args);
            return;
        }

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("My Friends Are Sea Creatures");
        config.setWindowedMode(960, 600);
        config.useVsync(true);
        config.setForegroundFPS(60);
        new Lwjgl3Application(new SeaCreaturesGame(new DesktopPhotoProvider()), config);
    }
}
