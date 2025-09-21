package org.game.client.entity;

import lombok.Data;

@Data
public sealed abstract class Entity permits Player {
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
    }
}