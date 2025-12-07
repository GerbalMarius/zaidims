package org.game.entity.damage_handler;

import lombok.Getter;
import org.game.entity.Enemy;
import org.game.entity.Player;

@Getter
public final class DamageContext {
    private int damage;
    private final Player target;

    private final Enemy source;

    private boolean isArmorApplied = false;
    private boolean isShieldApplied = false;

    private double piercingFactor;

    public DamageContext(int damage, Player target, Enemy source) {
        this.damage = damage;
        this.target = target;
        this.source = source;
    }

    public void setDamage(int damage) {
        this.damage = Math.max(damage, 0);
    }

    public void markArmorApplied() {
        this.isArmorApplied = true;
    }
    public void markShieldApplied() {
        this.isShieldApplied = true;
    }

    public void setPiercingFactor(double piercingFactor) {
        this.piercingFactor = Math.max(0, Math.min(1.0, piercingFactor));
    }
}
