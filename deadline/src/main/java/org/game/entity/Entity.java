package org.game.entity;

import lombok.Data;

import java.awt.*;

@Data
public sealed abstract class Entity permits Player, Enemy {

    private static final long INTERP_MS = 100;
    private static final int DEFAULT_SPEED = 5;

    protected int globalX, globalY;
    protected int prevX, prevY;
    protected int targetX, targetY;

    protected int scale;

    protected int speed;
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

    protected void copyFrames4d(ImageSprite[] frames) {
        int arraySize = 4;
        this.movementFrames = new ImageSprite[arraySize];
        if (frames.length != arraySize){
            throw new IllegalArgumentException("frames must contain 4 images");
        }
        System.arraycopy(frames, 0, this.movementFrames, 0, arraySize);
    }
}