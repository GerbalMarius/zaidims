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

        hitbox = new Rectangle(globalX, globalY, SCALE * 11, SCALE * 11);
    }

    protected void loadSprite(String powerUpName){
        this.sprite = ByteFiles.loadImage("res/potions/" + powerUpName + ".png");
    }

    @Override
    public void draw(Graphics2D g2d) {
        int  tileSize = WorldSettings.TILE_SIZE;
        g2d.drawImage(sprite, globalX, globalY, tileSize, tileSize, null);
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
