package org.game.entity.weapon;

import org.game.client.shoot.ShootImplementation;
import org.game.entity.ClassType;

public final class WeaponFactory {
    private WeaponFactory() {}

    public static Weapon createFor(ClassType classType, ShootImplementation implementation) {
        return switch (classType) {
            case WARRIOR -> new WarriorWeapon(implementation);
            case WIZARD -> new WizardWeapon(implementation);
            case ROGUE -> new RogueWeapon(implementation);
        };
    }
}