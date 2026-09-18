package com.browl.seacreatures.android;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.browl.seacreatures.SeaCreaturesGame;

/** Launches the shared LibGDX game on Android. All game logic lives in the core module. */
public class AndroidLauncher extends AndroidApplication {
    private AndroidPhotoProvider photoProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useCompass = false;
        photoProvider = new AndroidPhotoProvider(this);
        initialize(new SeaCreaturesGame(photoProvider), config);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (photoProvider != null) {
            photoProvider.handleActivityResult(requestCode, resultCode, data);
        }
    }
}
