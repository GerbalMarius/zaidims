package org.game.entity.damage_handler;

import org.game.entity.Player;

public final class RawDamageHandler implements DamageHandler {
    @Override
    public boolean handle(DamageContext ctx) {
        Player target = ctx.getTarget();
        target.takeDamage(ctx.getDamage());
        return false;
    }
}
