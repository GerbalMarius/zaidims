package org.game.entity;

import org.game.entity.decorator.AttackDecorator;
import org.game.entity.decorator.MaxHpDecorator;
import org.game.entity.decorator.SpeedDecorator;
import org.game.server.WorldSettings;

public class PlayerBuilder {
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
