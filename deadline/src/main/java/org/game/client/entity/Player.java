package org.game.client.entity;

import lombok.Getter;
import org.game.client.Camera;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.util.Objects;

import static org.game.client.entity.ImageSprite.*;
import static org.game.utils.ByteFiles.loadImage;


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

        loadPlayerSprite();

        this.lastRenderX = getRenderX();
        this.lastRenderY = getRenderY();

        this.hitbox = new Rectangle(8, 16, 32, 32);
    }

    private void loadPlayerSprite() {
        String prefix = playerClass.getClassPrefix();

        ImageSprite[] movementFrames = {
                upSprite(loadPlayerImage(prefix, "up", 1), loadPlayerImage(prefix, "up", 2)),
                leftSprite(loadPlayerImage(prefix, "left", 1), loadPlayerImage(prefix, "left", 2)),
                downSprite(loadPlayerImage(prefix, "down", 1), loadPlayerImage(prefix, "down", 2)),
                rightSprite(loadPlayerImage(prefix, "right", 1), loadPlayerImage(prefix, "right", 2))
        };
        super.copyFrames4d(movementFrames);

    }

    private BufferedImage loadPlayerImage(String prefix, String direction, int frame) {
        return loadImage(MessageFormat.format("res/player/{0}_{1}_{2}.png", prefix, direction, frame));
    }

    public void updateCameraPos(Camera camera, int screenWidth, int screenHeight, int worldWidth, int worldHeight) {
        double targetX = this.getRenderX();
        double targetY = this.getRenderY();

        camera.update(targetX, targetY);

        camera.clamp(screenWidth, screenHeight, worldWidth, worldHeight);
    }

    public void draw(Graphics2D g2, int x, int y, int tileSize) {
        BufferedImage image = switch (direction) {
            case UP -> getImageSprite(FramePosition.UP);
            case LEFT -> getImageSprite(FramePosition.LEFT);
            case DOWN -> getImageSprite(FramePosition.DOWN);
            case RIGHT -> getImageSprite(FramePosition.RIGHT);
        };

        g2.drawImage(image, x, y, tileSize, tileSize, null);
    }

    private BufferedImage getImageSprite(FramePosition framePosition) {
        if (spriteNum % 2 != 0) {
            return movementFrames[framePosition.ordinal()].firstFrame();
        }
        return movementFrames[framePosition.ordinal()].secondFrame();
    }

    public void updateDirectionByRender() {
        int dx = getRenderX() - lastRenderX;
        int dy = getRenderY() - lastRenderY;

        if (dx != 0 || dy != 0) {
            changeDirection(dx, dy);
            spriteCounter++;
            updateSprite();
        }

        lastRenderX = getRenderX();
        lastRenderY = getRenderY();
    }

    private void changeDirection(int dx, int dy) {
        if (dx < 0) {
            direction = FramePosition.LEFT;
        }
        else if (dx > 0) {
            direction = FramePosition.RIGHT;
        }
        else if (dy < 0) {
            direction = FramePosition.UP;
        }
        else {
            direction = FramePosition.DOWN;
        }
    }

    private void updateSprite() {
        if (spriteCounter > 10) {
            spriteNum = (spriteNum + 1) % 2;
            spriteCounter = 0;
        }
    }

}
