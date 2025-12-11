package org.game.entity.powerup;

import org.game.entity.DamageApplier;
import org.game.entity.Player;
import org.game.entity.damage_handler.ArmorDamageHandler;
import org.game.entity.powerup.visitor.PowerUpVisitor;

public final class ArmorPowerUp extends CorePowerUp {
    private final int flatReduction;
    private final int numUsages;
    public ArmorPowerUp(int globalX, int globalY) {
        super(globalX, globalY);
        this.flatReduction = 5;
        this.numUsages = 3;
        loadSprite("armor");
    }

    @Override
    public PowerUpType getType() {
        return PowerUpType.ARMOR;
    }
    public void applyTo(Player player) {

        DamageApplier damageApplier = player.getDamageApplier();
        ArmorDamageHandler existing =  damageApplier.findHandler(ArmorDamageHandler.class);
        if (existing != null) {
            existing.addHits(numUsages, player.getMaxArmorCount());
            player.setArmorCount(Math.min(player.getArmorCount() + numUsages, player.getMaxArmorCount()));
        } else {
            ArmorDamageHandler handler = new ArmorDamageHandler(flatReduction);
            handler.addHits(numUsages, player.getMaxArmorCount());
            damageApplier.addHandler(handler);
            player.setArmorCount(numUsages);
        }
    }
    @Override
    public void accept(PowerUpVisitor visitor) {visitor.visit(this);}
}
