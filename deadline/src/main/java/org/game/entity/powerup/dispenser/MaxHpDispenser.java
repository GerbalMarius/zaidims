package org.game.entity.powerup.dispenser;

import org.game.entity.powerup.MaxHpPowerUp;
import org.game.entity.powerup.PowerUp;

public final class MaxHpDispenser implements PowerUpDispenser {
    @Override
    public PowerUp dispensePowerUp(int x, int y) {
        return new MaxHpPowerUp(x,y);
    }
}
