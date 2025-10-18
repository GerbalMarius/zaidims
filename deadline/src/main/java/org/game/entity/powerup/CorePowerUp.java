package org.game.entity.powerup;

import lombok.Getter;
import org.game.server.WorldSettings;
import org.game.utils.ByteFiles;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class CorePowerUp implements PowerUp {

    private static final int  SCALE = 3;

    protected long powerUpId;

    @Getter
    protected int globalX;

    @Getter
    protected int globalY;

    protected BufferedImage sprite;

    @Getter
    protected Rectangle hitbox;


    protected CorePowerUp(int globalX, int globalY){
        this.globalX = globalX;
        this.globalY = globalY;

        hitbox = new Rectangle(globalX, globalY, SCALE * 12, SCALE * 12);
    }

    protected void loadSprite(String powerUpName){
        this.sprite = ByteFiles.loadImage("assets/potions/" + powerUpName + ".png");
    }

    @Override
    public void draw(Graphics2D g2d) {
        int  tileSize = WorldSettings.ORIGINAL_TILE_SIZE;
        g2d.drawImage(sprite, globalX, globalY, tileSize * SCALE, tileSize * SCALE, null);
    }

    @Override
    public long getId() {
        return powerUpId;
    }

    @Override
    public void setId(long id) {
        this.powerUpId = id;
    }
}
