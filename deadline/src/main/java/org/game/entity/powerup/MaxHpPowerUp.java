package org.game.entity.powerup;

import lombok.Getter;

@Getter
public final class MaxHpPowerUp extends CorePowerUp {
    private final int flatHpIncrease;

    public MaxHpPowerUp(int globalX, int globalY) {
        super(globalX, globalY);
        flatHpIncrease = 100;
        loadSprite("health");
    }

    @Override
    public PowerUpType getType() {
        return PowerUpType.MAX_HP;
    }
}
