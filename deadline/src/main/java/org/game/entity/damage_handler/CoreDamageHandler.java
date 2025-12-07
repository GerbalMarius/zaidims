package org.game.entity.damage_handler;

public abstract class CoreDamageHandler implements DamageHandler {
    private DamageHandler next;

    @Override
    public DamageHandler linkNext(DamageHandler next) {
        this.next = next;
        return next;
    }

    @Override
    public DamageHandler getNext() {
        return next;
    }

    @Override
    public void handle(DamageContext ctx) {
        if (!doContinue(ctx)) {
            return;
        }
        if (next != null) {
            next.handle(ctx);
        }
    }
}
