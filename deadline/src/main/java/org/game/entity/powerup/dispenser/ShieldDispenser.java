package org.game.entity.powerup.dispenser;

import org.game.entity.powerup.PowerUp;
import org.game.entity.powerup.ShieldPowerUp;

public final class ShieldDispenser implements  PowerUpDispenser{
    @Override
    public PowerUp dispensePowerUp(int x, int y) {
        return  new ShieldPowerUp(x, y);
    }
}
