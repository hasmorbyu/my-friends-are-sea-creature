package com.browl.seacreatures.system;

/**
 * Platform-specific way of letting the player pick a friend's photo. Desktop can do this
 * synchronously with a file chooser; Android must do it asynchronously via an Activity result,
 * so the callback shape is shared by both.
 */
public interface PhotoProvider {
    interface Callback {
        /** relativePath is the path (relative to the photos directory) the image was copied to. */
        void onPicked(String relativePath);

        void onCancelledOrFailed();
    }

    /** Opens the platform's photo picker; must copy the chosen image under the given photos directory. */
    void pickPhoto(java.io.File photosDir, Callback callback);
}
