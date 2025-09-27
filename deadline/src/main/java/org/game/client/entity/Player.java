package org.game.client.entity;

import lombok.Getter;
import org.game.client.Camera;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;


@Getter
public final class Player extends Entity {

    private final String name;

    private final ClassType playerClass;

    private int lastRenderX;
    private int lastRenderY;

    public Player(ClassType type, String name, int x, int y) {
        Objects.requireNonNull(name);
        super(x, y);
        this.name = name;
        this.playerClass = type;

        getPlayerImage();

        this.lastRenderX = getRenderX();
        this.lastRenderY = getRenderY();

        this.hitbox = new Rectangle(8, 16, 32, 32);
    }

    private void getPlayerImage() {
        try {
            String prefix = playerClass.getClassPrefix();

            up1 = ImageIO.read(getClass().getResourceAsStream("/player/" + prefix + "_up_1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/player/" + prefix + "_up_2.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("/player/" + prefix + "_down_1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/player/" + prefix + "_down_2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/player/" + prefix + "_left_1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/player/" + prefix + "_left_2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/player/" + prefix + "_right_1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/player/" + prefix + "_right_2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateCameraPos(Camera camera, int screenWidth, int screenHeight, int worldWidth, int worldHeight) {
        double targetX = this.getRenderX();
        double targetY = this.getRenderY();

        camera.update(targetX, targetY);

        camera.clamp(screenWidth,  screenHeight,  worldWidth,  worldHeight);
    }

    public void draw(Graphics2D g2, int x, int y, int tileSize) {
        BufferedImage image = null;

        switch (direction) {
            case "up" -> image = (spriteNum == 1) ? up1 : up2;
            case "down" -> image = (spriteNum == 1) ? down1 : down2;
            case "left" -> image = (spriteNum == 1) ? left1 : left2;
            case "right" -> image = (spriteNum == 1) ? right1 : right2;
        }

        g2.drawImage(image, x, y, tileSize, tileSize, null);
    }

    public void updateDirectionByRender() {
        int dx = getRenderX() - lastRenderX;
        int dy = getRenderY() - lastRenderY;

        if (dx != 0 || dy != 0) {
            if (dx < 0) direction = "left";
            else if (dx > 0) direction = "right";
            else if (dy < 0) direction = "up";
            else direction = "down";

            spriteCounter++;
            if (spriteCounter > 12) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        }

        lastRenderX = getRenderX();
        lastRenderY = getRenderY();
    }

}
