package org.game.entity.powerup.dispenser;

import org.game.entity.powerup.PowerUp;
import org.game.entity.powerup.SpeedPowerUp;

public final class SpeedDispenser implements PowerUpDispenser {
    @Override
    public PowerUp dispensePowerUp(int x, int y) {
        return new SpeedPowerUp(x,y);
    }
}
