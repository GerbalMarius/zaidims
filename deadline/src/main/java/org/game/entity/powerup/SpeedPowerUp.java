package org.game.entity.powerup;

import lombok.Getter;

@Getter
public final class SpeedPowerUp extends CorePowerUp {

    private final int flatSpeedIncrease;

    public SpeedPowerUp(int globalX, int globalY) {
        super(globalX, globalY);
        this.flatSpeedIncrease = 1;
        loadSprite("speed");
    }
}
