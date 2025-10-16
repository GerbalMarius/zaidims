package org.game.entity.powerup.dispenser;

import org.game.entity.powerup.AttackPowerUp;
import org.game.entity.powerup.PowerUp;

public final class AttackDispenser implements PowerUpDispenser {

    @Override
    public PowerUp dispensePowerUp(int x, int y) {
        return new AttackPowerUp(x,y);
    }
}
