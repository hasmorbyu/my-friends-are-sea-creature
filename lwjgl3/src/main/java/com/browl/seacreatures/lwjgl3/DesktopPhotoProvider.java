package com.browl.seacreatures.lwjgl3;

import com.browl.seacreatures.system.PhotoProvider;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/** Desktop photo picking via a Swing file chooser; copies the chosen file into the save directory. */
public class DesktopPhotoProvider implements PhotoProvider {
    @Override
    public void pickPhoto(File photosDir, Callback callback) {
        SwingUtilities.invokeLater(() -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Images", "png", "jpg", "jpeg", "bmp"));
            int result = chooser.showOpenDialog(null);
            if (result != JFileChooser.APPROVE_OPTION) {
                callback.onCancelledOrFailed();
                return;
            }
            try {
                File selected = chooser.getSelectedFile();
                String extension = getExtension(selected.getName());
                String relativeName = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);
                File destination = new File(photosDir, relativeName);
                Files.copy(selected.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                callback.onPicked(relativeName);
            } catch (Exception e) {
                System.err.println("Failed to import photo: " + e.getMessage());
                callback.onCancelledOrFailed();
            }
        });
    }

    private String getExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot >= 0 ? fileName.substring(dot + 1).toLowerCase() : "";
    }
}
