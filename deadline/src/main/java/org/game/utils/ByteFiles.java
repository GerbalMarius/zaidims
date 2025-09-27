package org.game.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public final class ByteFiles {

    private ByteFiles() {}

    public static BufferedImage loadImage(String path) {
        try(var imageStream = Files.newInputStream(Paths.get(path), StandardOpenOption.READ)) {
            return ImageIO.read(imageStream);
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
