package org.game.entity.powerup;

import lombok.Getter;
import org.game.entity.powerup.visitor.PowerUpVisitor;

@Getter
public final class AttackPowerUp extends CorePowerUp {
    private final int flatAttackIncrease;

    public AttackPowerUp(int globalX, int globalY) {
        super(globalX, globalY);
        this.flatAttackIncrease = 5;
        loadSprite("attack");
    }

    @Override
    public PowerUpType getType() {
        return PowerUpType.ATTACK;
    }

    @Override
    public void accept(PowerUpVisitor visitor) {visitor.visit(this);}
}
