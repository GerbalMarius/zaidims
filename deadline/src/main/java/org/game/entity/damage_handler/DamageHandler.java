package org.game.entity.damage_handler;

public interface DamageHandler {

    DamageHandler linkNext(DamageHandler next);
    void handle(DamageContext ctx);

    boolean doContinue(DamageContext ctx);

    DamageHandler getNext();
}
