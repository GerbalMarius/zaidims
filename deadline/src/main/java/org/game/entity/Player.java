package org.game.entity;

import lombok.Getter;
import org.game.client.Camera;

import java.awt.*;
import java.util.Objects;


@Getter
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

    public void takeDamage(int dmg) {
        if (hitPoints <= 0) return; // jau miręs, ignoruojam
        this.hitPoints -= dmg;
        if (this.hitPoints < 0) this.hitPoints = 0;
        System.out.println(name + " gavo " + dmg + " zalos. Liko HP: " + hitPoints);
    }

}
