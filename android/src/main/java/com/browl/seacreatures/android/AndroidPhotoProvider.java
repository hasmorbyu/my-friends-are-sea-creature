package com.browl.seacreatures.android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.browl.seacreatures.system.PhotoProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

/**
 * Android photo picking via the system gallery picker. Uses the classic
 * startActivityForResult/onActivityResult pair (registered with AndroidLauncher) since LibGDX's
 * AndroidApplication is a plain Activity, not a ComponentActivity with the newer Activity Result APIs.
 */
public class AndroidPhotoProvider implements PhotoProvider {
    private static final int PICK_IMAGE_REQUEST = 4242;

    private final AndroidLauncher activity;
    private Callback pendingCallback;
    private File pendingPhotosDir;

    public AndroidPhotoProvider(AndroidLauncher activity) {
        this.activity = activity;
    }

    @Override
    public void pickPhoto(File photosDir, Callback callback) {
        this.pendingCallback = callback;
        this.pendingPhotosDir = photosDir;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activity.startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /** Called by {@link AndroidLauncher#onActivityResult}. */
    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != PICK_IMAGE_REQUEST || pendingCallback == null) {
            return;
        }
        if (resultCode != Activity.RESULT_OK || data == null || data.getData() == null) {
            pendingCallback.onCancelledOrFailed();
            return;
        }
        try {
            Uri uri = data.getData();
            String relativeName = UUID.randomUUID() + ".png";
            File destination = new File(pendingPhotosDir, relativeName);
            try (InputStream in = activity.getContentResolver().openInputStream(uri);
                 FileOutputStream out = new FileOutputStream(destination)) {
                byte[] buffer = new byte[8192];
                int read;
                while (in != null && (read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            }
            pendingCallback.onPicked(relativeName);
        } catch (Exception e) {
            pendingCallback.onCancelledOrFailed();
        } finally {
            pendingCallback = null;
        }
    }
}
