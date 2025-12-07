package org.game.entity.damage_handler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ShieldDamageHandler extends CoreDamageHandler {
    private int remainingBlocks;
    public ShieldDamageHandler(int blocks) {
        this.remainingBlocks = blocks;
    }

    public void incBlockCount(){
        if (remainingBlocks <= 0) {
            remainingBlocks = 1;
        }
    }

    @Override
    public boolean doContinue(DamageContext ctx) {
        if (ctx.getDamage() <= 0) {
            return true;
        }
        if (ctx.getPiercingFactor() >= 1.0) {
            return true;
        }

        if (ctx.isShieldApplied()) {
            return true;
        }

        if (remainingBlocks <= 0) {
            ctx.getTarget().setShieldActive(false);
            return true;
        }


        remainingBlocks--;
        ctx.setDamage(0);
        ctx.markShieldApplied();
        if (remainingBlocks == 0){
            ctx.getTarget().setShieldActive(false);
        }
        return true;
    }

    @Override
    public int priority() {
        return 2;
    }
}
