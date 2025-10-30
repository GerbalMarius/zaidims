package org.game.entity.attack;

import org.game.entity.ClassType;

public final class AttackFactory {
    private AttackFactory() {}

    public static AttackBehavior defaultFor(ClassType classType) {
        return switch (classType) {
            case WARRIOR -> new ProjectileAttack(6, 35, 150, 600);
            case WIZARD  -> new ProjectileAttack(4, 50, 800, 900);
            case ROGUE   -> new ProjectileAttack(8, 15, 300, 100);
        };
    }
}