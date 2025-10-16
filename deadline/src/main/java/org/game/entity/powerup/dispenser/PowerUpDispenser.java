package org.game.entity.powerup.dispenser;

import org.game.entity.powerup.PowerUp;

public interface PowerUpDispenser {
    PowerUp dispensePowerUp(int x, int y);
}
