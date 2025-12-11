package org.game.entity.damage_handler;

import org.game.entity.Enemy;

public final class PiercingDamageHandler implements DamageHandler {
    @Override
    public boolean handle(DamageContext ctx) {
        Enemy source = ctx.getSource();
        ctx.setPiercingFactor(source.getPiercingFactor());
        return true;
    }

    @Override
    public int priority() {
        return 1;
    }
}
