package org.game.client.entity;

import lombok.Data;

@Data
public sealed abstract class Entity permits Player {

    private static final long INTERP_MS = 100;

    protected int x, y;
    protected int prevX, prevY;
    protected int targetX, targetY;

    protected long lastUpdateTime;

    protected Entity(int x, int y) {
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
        this.prevX = x;
        this.prevY = y;

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
        this.x += dx;
        this.y += dy;
        this.prevX = this.x;
        this.prevY = this.y;
        this.targetX = this.x;
        this.targetY = this.y;
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
}