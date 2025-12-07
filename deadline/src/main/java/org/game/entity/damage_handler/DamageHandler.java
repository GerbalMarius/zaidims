package org.game.entity.damage_handler;

public interface DamageHandler {

    DamageHandler linkNext(DamageHandler next);
    void handle(DamageContext ctx);

    default int priority() {
        return Integer.MAX_VALUE;
    }

    boolean doContinue(DamageContext ctx);

    DamageHandler getNext();
}
