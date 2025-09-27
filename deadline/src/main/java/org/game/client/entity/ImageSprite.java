package org.game.client.entity;

import java.awt.image.BufferedImage;

public record ImageSprite(BufferedImage firstFrame, BufferedImage secondFrame) {

    public static ImageSprite upSprite(BufferedImage firstFrame, BufferedImage secondFrame) {
        return new ImageSprite(firstFrame, secondFrame);
    }
    public static ImageSprite leftSprite(BufferedImage firstFrame, BufferedImage secondFrame) {
        return new ImageSprite(firstFrame, secondFrame);
    }
    public static ImageSprite downSprite(BufferedImage firstFrame, BufferedImage secondFrame) {
        return new ImageSprite(firstFrame, secondFrame);
    }
    public static ImageSprite rightSprite(BufferedImage firstFrame, BufferedImage secondFrame) {
        return new ImageSprite(firstFrame, secondFrame);
    }
}
