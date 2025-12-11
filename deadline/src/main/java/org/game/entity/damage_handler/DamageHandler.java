package org.game.entity.damage_handler;

public interface DamageHandler {

    boolean handle(DamageContext ctx);

    default int priority() {
        return Integer.MAX_VALUE;
    }

}
