package org.game.entity.damage_handler;

import org.game.entity.Player;

public final class RawDamageHandler extends CoreDamageHandler {
    @Override
    public boolean doContinue(DamageContext ctx) {
        Player target = ctx.getTarget();
        target.takeDamage(ctx.getDamage());
        return false;
    }
}
