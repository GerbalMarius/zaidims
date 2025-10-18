package org.game.entity.powerup;

import lombok.Getter;

@Getter
public final class AttackPowerUp extends CorePowerUp {
    private final int flatAttackIncrease;

    public AttackPowerUp(int globalX, int globalY) {
        super(globalX, globalY);
        this.flatAttackIncrease = 3;
        loadSprite("attack");
    }

    @Override
    public PowerUpType getType() {
        return PowerUpType.ATTACK;
    }
}
