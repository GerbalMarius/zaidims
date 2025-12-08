package org.game.entity.powerup;

import org.game.entity.Player;
import org.game.entity.damage_handler.ShieldDamageHandler;
import org.game.entity.powerup.visitor.PowerUpVisitor;

public final class ShieldPowerUp extends CorePowerUp {
    private final int blocks;
    public ShieldPowerUp(int globalX, int globalY) {
        super(globalX, globalY);
        this.blocks = 1;
        loadSprite("shield");
    }

    @Override
    public PowerUpType getType() {
        return PowerUpType.SHIELD;
    }
    public void applyTo(Player player) {
        if (player.isShieldActive()) {
            return;
        }

        ShieldDamageHandler existing = player.findShieldHandler();
        if (existing != null) {
            player.setShieldActive(true);
            existing.incBlockCount();
            return;
        }

        ShieldDamageHandler handler = new ShieldDamageHandler(blocks);
        player.addHandler(handler);

        player.setShieldActive(true);
    }
    @Override
    public void accept(PowerUpVisitor visitor) {visitor.visit(this);}
}
