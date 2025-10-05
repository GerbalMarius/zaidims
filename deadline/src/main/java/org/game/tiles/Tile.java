package org.game.tiles;

import java.awt.image.BufferedImage;

public record Tile(BufferedImage image, boolean hasCollision) {
}
