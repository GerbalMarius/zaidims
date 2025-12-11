package org.game.entity.damage_handler;

import org.game.entity.Player;

public final class ArmorDamageHandler implements DamageHandler {
    private final int flatReduction;

    private static final int DEFAULT_HIT_LIMIT = 3;
    private int remainingHits;

    public ArmorDamageHandler(int flatReduction) {
        this.flatReduction = flatReduction;
        this.remainingHits = DEFAULT_HIT_LIMIT;
    }
    public void addHits(int hits, int limit) {
        this.remainingHits = Math.min(remainingHits + hits, limit);
    }
    @Override
    public boolean handle(DamageContext ctx) {
        Player target = ctx.getTarget();

        if (ctx.isArmorApplied()) {
            return true;
        }

        int remainingHits = target.getArmorCount();
        if (this.remainingHits <= 0 || ctx.getDamage() <= 0){
            return true;
        }
        double pierce = ctx.getPiercingFactor();

        double effectiveReduction = flatReduction * (1.0 - pierce);
        int reduced = ctx.getDamage() - (int)Math.round(effectiveReduction);
        ctx.setDamage(Math.max(reduced, 0));
        target.setArmorCount(Math.max(0, remainingHits - 1));

        this.remainingHits--;

        ctx.markArmorApplied();

        return true;
    }

    @Override
    public int priority() {
        return 3;
    }
}
