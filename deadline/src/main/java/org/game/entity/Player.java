package org.game.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.game.client.Camera;

import java.awt.*;
import java.util.Objects;


@Getter
@Slf4j
public non-sealed class Player extends Entity {

    private final String name;

    private final ClassType playerClass;

    @Setter
    private int hpRegenAmount;

    private long hpRegenIntervalMs;

    private long lastRegenTimestamp;

    public Player(ClassType type, String name, int x, int y) {
        Objects.requireNonNull(name);
        super(x, y);
        this.name = name;
        this.playerClass = type;

        loadSprite(playerClass.getClassPrefix(), "player");

        this.lastRenderX = getRenderX();
        this.lastRenderY = getRenderY();

        this.scale = 3;
        this.hitbox = new Rectangle(8, 16, 11 * scale, 11 * scale);
        setStats();

        setClassRegenDefaults();
    }


    public void updateCameraPos(Camera camera, int screenWidth, int screenHeight, int worldWidth, int worldHeight) {
        double targetX = this.getRenderX();
        double targetY = this.getRenderY();

        camera.update(targetX, targetY);

        camera.clamp(screenWidth, screenHeight, worldWidth, worldHeight);
    }

    private void setStats() {
        switch (this.playerClass) {
            case WARRIOR -> {
                speed = 3;
                maxHitPoints = hitPoints = 100;
                attack = 25;
            }
            case WIZARD -> {
                speed = 4;
                maxHitPoints = hitPoints = 50;
                attack = 30;
            }
            case ROGUE -> {
                speed = 5;
                maxHitPoints = hitPoints = 70;
                attack = 15;
            }
        }
    }

    private void setClassRegenDefaults() {
        switch (this.playerClass) {
            case WARRIOR -> { hpRegenAmount = 3; hpRegenIntervalMs = 2_000; }
            case WIZARD  -> { hpRegenAmount = 2; hpRegenIntervalMs = 3_000; }
            case ROGUE   -> { hpRegenAmount = 1; hpRegenIntervalMs = 1_000; }
        }
        lastRegenTimestamp = System.currentTimeMillis();
    }

    public boolean regenIfNeeded(long nowMillis) {
        if (!isAlive()) return false;
        if (getHitPoints() >= getMaxHitPoints()) {
            lastRegenTimestamp = nowMillis;
            return false;
        }
        if (nowMillis - lastRegenTimestamp >= hpRegenIntervalMs) {
            int newHp = Math.min(getMaxHitPoints(), getHitPoints() + hpRegenAmount);
            if (newHp != getHitPoints()) {
                setHitPoints(newHp);
                lastRegenTimestamp = nowMillis;
                log.debug("{} regenerated {} HP -> {}/{}", name, hpRegenAmount, getHitPoints(), getMaxHitPoints());
                return true;
            } else {
                lastRegenTimestamp = nowMillis;
            }
        }
        return false;
    }

    public boolean isAlive() {
        return getHitPoints() > 0;
    }

    public void takeDamage(int dmg) {
        if (getHitPoints() <= 0) return;
        this.setHitPoints(this.getHitPoints() - dmg);
        if (this.getHitPoints() < 0) this.setHitPoints(0);
        log.debug("{} received {} dmg pts. HP left: {}", name, dmg, getHitPoints());
    }
}
