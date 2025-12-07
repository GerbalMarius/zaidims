package org.game.entity.powerup.dispenser;

import org.game.entity.powerup.ArmorPowerUp;
import org.game.entity.powerup.PowerUp;

public final class ArmorDispenser implements PowerUpDispenser {
    @Override
    public PowerUp dispensePowerUp(int x, int y) {
        return new ArmorPowerUp(x, y);
    }
}
