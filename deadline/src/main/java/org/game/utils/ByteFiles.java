package org.game.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

public final class ByteFiles {

    private ByteFiles() {}

    public static BufferedImage loadImage(String path) {
        try(var imageStream = new FileInputStream(path)) {
            return ImageIO.read(imageStream);
        }catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
