package org.game.entity;

import lombok.Data;
import org.game.utils.ByteFiles;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;

import static org.game.entity.ImageSprite.*;
import static org.game.entity.ImageSprite.rightSprite;

@Data
public sealed abstract class Entity permits Enemy, Player, Projectile {

    private static final long INTERP_MS = 100;
    private static final int DEFAULT_SPEED = 5;

    protected int globalX, globalY;
    protected int prevX, prevY;
    protected int targetX, targetY;
    protected int lastRenderX,  lastRenderY;

    protected int scale;

    protected int speed;
    protected int maxHitPoints;
    protected int hitPoints;
    protected int attack;

    protected ImageSprite[] movementFrames;
    protected FramePosition direction;
    protected int spriteNum;
    protected int spriteCounter;

    protected Rectangle hitbox;
    protected boolean collisionOn = false;

    protected long lastUpdateTime;

    protected Entity(int x, int y) {
        this.globalX = x;
        this.globalY = y;
        this.targetX = x;
        this.targetY = y;
        this.prevX = x;
        this.prevY = y;

        this.speed = DEFAULT_SPEED;
        this.direction = FramePosition.DOWN;
        this.spriteNum = 1;
        this.spriteCounter = 0;

        this.lastUpdateTime = System.currentTimeMillis();

    }

    public synchronized void updateFromServer(int newX, int newY) {
        long now = System.currentTimeMillis();
        this.prevX = getRenderX();
        this.prevY = getRenderY();

        this.targetX = newX;
        this.targetY = newY;
        this.lastUpdateTime = now;
    }


    public  void moveBy(int dx, int dy) {
        this.globalX += dx;
        this.globalY += dy;
        this.prevX = this.globalX;
        this.prevY = this.globalY;
        this.targetX = this.globalX;
        this.targetY = this.globalY;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public int getRenderX(){
        double tick = currentTick();
        double rx = prevX + (targetX - prevX) * tick;
        return (int)Math.round(rx);
    }


    public int getRenderY(){
        double tick = currentTick();
        double ry = prevY + (targetY - prevY) * tick;
        return (int)Math.round(ry);
    }


    private double currentTick() {
        long now = System.currentTimeMillis();
        long dt  = now - this.lastUpdateTime;
        return Math.min(1.0, (double)dt / INTERP_MS);
    }

    protected void loadSprite(String prefix, String source) {

        ImageSprite[] movementFrames = {

                upSprite(loadImage(source, prefix, "up", 1), loadImage(source, prefix, "up", 2)),

                leftSprite(loadImage(source, prefix, "left", 1), loadImage(source, prefix, "left", 2)),

                downSprite(loadImage(source, prefix, "down", 1), loadImage(source, prefix, "down", 2)),

                rightSprite(loadImage(source, prefix, "right", 1), loadImage(source, prefix, "right", 2))
        };

        copyFrames4d(movementFrames);

    }

    private BufferedImage loadImage(String source, String prefix, String direction, int frame) {
        return ByteFiles.loadImage(MessageFormat.format("res/{0}/{1}_{2}_{3}.png", source, prefix, direction, frame));
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

    protected void copyFrames4d(ImageSprite[] frames) {
        int arraySize = 4;
        this.movementFrames = new ImageSprite[arraySize];
        if (frames.length != arraySize){
            throw new IllegalArgumentException("frames must contain 4 images");
        }
        System.arraycopy(frames, 0, this.movementFrames, 0, arraySize);
    }

    public void drawHealthBar(Graphics2D g2, int x, int y, int width, Color color) {
        if (getHitPoints() <= 0) return ;

        int barHeight = 6;
        int offsetY = -10; // virs galvos

        double hpRatio = (double) getHitPoints() / getMaxHitPoints();
        int filledWidth = (int) (width * hpRatio);

        g2.setColor(Color.BLACK);
        g2.fillRect(x, y + offsetY, width, barHeight);

        g2.setColor(color);
        g2.fillRect(x, y + offsetY, filledWidth, barHeight);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1));
        g2.drawRect(x, y + offsetY, width, barHeight);
    }

}