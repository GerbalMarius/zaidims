package org.game.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.game.client.Camera;
import org.game.entity.decorator.AttackDecorator;
import org.game.entity.decorator.MaxHpDecorator;
import org.game.entity.decorator.SpeedDecorator;
import org.game.server.WorldSettings;

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

    public static PlayerBuilder builder() {
        return new PlayerBuilder();
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

    public static class PlayerBuilder {
        private ClassType classType;
        private String name = "Player";
        private int x = WorldSettings.CENTER_X;
        private int y = WorldSettings.CENTER_Y;
        private Integer customHitPoints;
        private Integer customAttack;
        private Integer customSpeed;
        private boolean withAttackBonus = false;
        private boolean withSpeedBonus = false;
        private boolean withMaxHpBonus = false;
        private int attackBonusAmount = 0;
        private int speedBonusAmount = 0;
        private int maxHpBonusAmount = 0;

        private PlayerBuilder() {}

        public PlayerBuilder ofClass(ClassType classType) {
            this.classType = classType;
            return this;
        }

        public PlayerBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public PlayerBuilder at(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public PlayerBuilder withHitPoints(int hitPoints) {
            this.customHitPoints = hitPoints;
            return this;
        }

        public PlayerBuilder withAttack(int attack) {
            this.customAttack = attack;
            return this;
        }

        public PlayerBuilder withSpeed(int speed) {
            this.customSpeed = speed;
            return this;
        }

        public PlayerBuilder withAttackBonus(int amount) {
            this.withAttackBonus = true;
            this.attackBonusAmount = amount;
            return this;
        }

        public PlayerBuilder withSpeedBonus(int amount) {
            this.withSpeedBonus = true;
            this.speedBonusAmount = amount;
            return this;
        }

        public PlayerBuilder withMaxHpBonus(int amount) {
            this.withMaxHpBonus = true;
            this.maxHpBonusAmount = amount;
            return this;
        }

        public Player build() {

            Player player = new Player(classType, name, x, y);

            if (customHitPoints != null) {
                player.setHitPoints(customHitPoints);
                player.setMaxHitPoints(customHitPoints);
            }

            if (customAttack != null) {
                player.setAttack(customAttack);
            }

            if (customSpeed != null) {
                player.setSpeed(customSpeed);
            }

            if (withAttackBonus) {
                player = new AttackDecorator(player, attackBonusAmount);
            }

            if (withSpeedBonus) {
                player = new SpeedDecorator(player, speedBonusAmount);
            }

            if (withMaxHpBonus) {
                player = new MaxHpDecorator(player, maxHpBonusAmount);
            }

            return player;
        }
    }
}
