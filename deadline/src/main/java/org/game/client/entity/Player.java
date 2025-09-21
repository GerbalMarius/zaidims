package org.game.client.entity;

import lombok.Getter;

import java.util.Objects;
import java.util.UUID;


@Getter
public final class Player extends Entity {

    private final String name;


    public Player(String name, int x, int y) {
        Objects.requireNonNull(name);
        super(x, y);
        this.name = name;
    }


    public void updateFromServer(int newX, int newY) {
        this.x = newX;
        this.y = newY;
        this.targetX = newX;
        this.targetY = newY;
        this.lastUpdateTime = System.currentTimeMillis();
    }


    public void moveBy(int dx, int dy) {
        this.x += dx;
        this.y += dy;
        this.targetX = this.x;
        this.targetY = this.y;
        this.lastUpdateTime = System.currentTimeMillis();
    }


}
