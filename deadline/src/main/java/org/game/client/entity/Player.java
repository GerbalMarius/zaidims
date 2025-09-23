package org.game.client.entity;

import lombok.Getter;
import org.game.client.Camera;

import java.util.Objects;


@Getter
public final class Player extends Entity {

    private final String name;


    public Player(String name, int x, int y) {
        Objects.requireNonNull(name);
        super(x, y);
        this.name = name;
    }

    public void updateCameraPos(Camera camera, int screenWidth, int screenHeight, int worldWidth, int worldHeight) {
        double targetX = this.getRenderX();
        double targetY = this.getRenderY();

        camera.update(targetX, targetY);

        camera.clamp(screenWidth,  screenHeight,  worldWidth,  worldHeight);
    }

}
