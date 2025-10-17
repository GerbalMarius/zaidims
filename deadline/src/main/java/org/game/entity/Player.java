package org.game.entity;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.game.client.Camera;

import java.awt.*;
import java.util.Objects;


@Getter
@Slf4j
public final class Player extends Entity {

    private final String name;

    private final ClassType playerClass;


    public Player(ClassType type, String name, int x, int y) {
        Objects.requireNonNull(name);
        super(x, y);
        this.name = name;
        this.playerClass = type;

        loadSprite(playerClass.getClassPrefix(), "player");

        this.lastRenderX = getRenderX();
        this.lastRenderY = getRenderY();

        this.scale = 3;
        this.hitbox = new Rectangle(8, 16, 11*scale, 11*scale);
        setStats();
    }


    public void updateCameraPos(Camera camera, int screenWidth, int screenHeight, int worldWidth, int worldHeight) {
        double targetX = this.getRenderX();
        double targetY = this.getRenderY();

        camera.update(targetX, targetY);

        camera.clamp(screenWidth, screenHeight, worldWidth, worldHeight);
    }

    private void setStats() {
        switch(this.playerClass) {
            case WARRIOR ->  {
                speed = 3;
                maxHitPoints = hitPoints = 100;
                attack = 25;
            }
            case WIZARD ->  {
                speed = 4;
                maxHitPoints = hitPoints = 50;
                attack = 40;
            }
            case ROGUE ->  {
                speed = 5;
                maxHitPoints = hitPoints = 70;
                attack = 25;
            }
        }
    }

    public boolean isAlive() {
        return hitPoints > 0;
    }

    public void takeDamage(int dmg) {
        int clamped = Math.max(0, dmg);//to avoid negative damage that would heal the player

        this.hitPoints = Math.max(0, this.hitPoints - clamped);//neg hp case

        log.debug("{} received {} damage. HP left: {}", name, clamped, hitPoints);
    }

}
