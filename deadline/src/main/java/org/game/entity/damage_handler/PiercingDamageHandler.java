package org.game.entity.damage_handler;

import org.game.entity.Enemy;

public final class PiercingDamageHandler extends CoreDamageHandler {
    @Override
    public boolean doContinue(DamageContext ctx) {
        Enemy source = ctx.getSource();
        ctx.setPiercingFactor(source.getPiercingFactor());
        return true;
    }
}
