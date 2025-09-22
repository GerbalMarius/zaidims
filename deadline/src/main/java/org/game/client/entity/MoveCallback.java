package org.game.client.entity;

@FunctionalInterface
public interface MoveCallback {

    void move(int dx, int dy);
}
