package org.game.entity;

import lombok.Getter;
import lombok.Setter;
import org.game.client.CollisionChecker;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.util.Objects;

import static org.game.entity.ImageSprite.*;
import static org.game.entity.ImageSprite.rightSprite;
import static org.game.utils.ByteFiles.loadImage;

@Getter
@Setter
public final class Enemy extends Entity {

    private  EnemyType  type;
    private  EnemySize  size;

    private int lastRenderX;
    private int lastRenderY;

    public Enemy(EnemyType type, EnemySize size, int x, int y) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(size);
        super(x, y);
        this.type = type;
        this.size = size;

        loadEnemySprite();
        configureStats();

        this.hitbox = new Rectangle(8, 16, 11*scale, 11*scale);
    }

    private void configureStats() {
        switch(type) {
            case ZOMBIE -> {
                switch(size) {
                    case SMALL -> { hitPoints = 30; attack = 5; scale = 3; speed = 3; }
                    case MEDIUM -> { hitPoints = 60; attack = 10; scale = 4; speed = 2; }
                    case BIG -> { hitPoints = 120; attack = 25; scale = 5; speed = 1; }
                }
            }
            case SKELETON ->  {
                switch(size) {
                    case SMALL -> { hitPoints = 20; attack = 7; scale = 3; speed = 4; }
                    case MEDIUM -> { hitPoints = 40; attack = 12; scale = 4; speed = 3; }
                    case BIG -> { hitPoints = 90; attack = 25; scale = 5; speed = 2; }
                }
            }
            case GOBLIN -> {
                switch(size) {
                    case SMALL -> { hitPoints = 25; attack = 8; scale = 3; speed = 5; }
                    case MEDIUM -> { hitPoints = 50; attack = 15; scale = 4; speed = 4; }
                    case BIG -> { hitPoints = 80; attack = 22; scale = 5; speed = 3;}
                }
            }
        }
    }

    private void loadEnemySprite() {
        String prefix = type.getClassPrefix();

        ImageSprite[] movementFrames = {
                upSprite(loadEnemyImage(prefix, "up", 1), loadEnemyImage(prefix, "up", 2)),
                leftSprite(loadEnemyImage(prefix, "left", 1), loadEnemyImage(prefix, "left", 2)),
                downSprite(loadEnemyImage(prefix, "down", 1), loadEnemyImage(prefix, "down", 2)),
                rightSprite(loadEnemyImage(prefix, "right", 1), loadEnemyImage(prefix, "right", 2))
        };
        super.copyFrames4d(movementFrames);

    }

    private BufferedImage loadEnemyImage(String prefix, String direction, int frame) {
        return loadImage(MessageFormat.format("res/enemy/{0}_{1}_{2}.png", prefix, direction, frame));
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

    public void updateAI(Player player, CollisionChecker checker) {
        int dx = Integer.compare(player.getGlobalX(), this.getGlobalX());
        int dy = Integer.compare(player.getGlobalY(), this.getGlobalY());

        // X
        if (dx != 0) {
            this.setDirection(dx > 0 ? FramePosition.RIGHT : FramePosition.LEFT);
            checker.checkTile(this);
            if (!this.isCollisionOn()) {
                this.moveBy(dx * speed, 0);
            }
            this.setCollisionOn(false); // reset for next move
        }

        // Y
        if (dy != 0) {
            this.setDirection(dy > 0 ? FramePosition.DOWN : FramePosition.UP);
            checker.checkTile(this);
            if (!this.isCollisionOn()) {
                this.moveBy(0, dy * speed);
            }
            this.setCollisionOn(false);
        }
    }

    public void updateDirectionAndSprite() {
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
