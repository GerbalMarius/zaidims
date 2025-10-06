package org.game.entity;

import lombok.Getter;
import lombok.Setter;
import org.game.utils.ByteFiles;
import java.awt.*;
import java.awt.image.BufferedImage;

@Getter
@Setter
public final class Item {
    private final ItemType type;
    private final int globalX;
    private final int globalY;
    private BufferedImage sprite;
    private Rectangle hitbox;
    private boolean collected = false;

    public Item(ItemType type, int x, int y) {
        this.type = type;
        this.globalX = x;
        this.globalY = y;
        loadSprite();
        this.hitbox = new Rectangle(x, y, 32, 32);
    }

    private void loadSprite() {
        String path = String.format("res/items/%s.png", type.getFileName());
        this.sprite = ByteFiles.loadImage(path);
    }

    public void draw(Graphics2D g2d, int size) {
        if (!collected) {
            g2d.drawImage(sprite, globalX, globalY, size*2, size*2, null);
        }
    }
}