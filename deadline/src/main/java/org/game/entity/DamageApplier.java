package org.game.entity;

import org.game.entity.damage_handler.DamageContext;
import org.game.entity.damage_handler.DamageHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public final class DamageApplier {
    private final SortedMap<Integer, List<DamageHandler>> damageHandlers;

    public DamageApplier() {
        damageHandlers = new TreeMap<>();
    }

    public DamageApplier(DamageHandler... handlers) {
        this();
        for (DamageHandler handler : handlers) {
            addHandler(handler);
        }
    }

    public DamageApplier(DamageApplier other) {
        damageHandlers = new TreeMap<>(other.damageHandlers);
    }

    public void addHandler(DamageHandler handler){
        damageHandlers
                .computeIfAbsent(handler.priority(), _ -> new ArrayList<>())
                .add(handler);
    }

    public void applyDamage(DamageContext ctx) {
        for (List<DamageHandler> handlers : damageHandlers.values()) {
            for (DamageHandler handler : handlers) {
                boolean processFurther = handler.handle(ctx);
                if (!processFurther) return;
            }
        }
    }

    public <T extends DamageHandler> T findHandler(Class<T> clazz) {
        for (List<DamageHandler> handlers : damageHandlers.values()) {
            for (DamageHandler handler : handlers) {
                if (clazz.isInstance(handler)) {
                    return clazz.cast(handler);
                }
            }
        }
        return null;
    }
}
