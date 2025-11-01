package org.game.entity.weapon;

import org.game.client.shoot.ShootImplementation;
import org.game.entity.Player;
import org.game.entity.Projectile;
import org.game.entity.attack.ProjectileAttack;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class WizardWeapon extends Weapon {

    public WizardWeapon(ShootImplementation implementation) {
        super(implementation);
    }

    @Override
    public List<ShootImplementation.ProjectileData> fire(Player owner, UUID projectileId, long timestamp) {
        if (!canShoot(timestamp)) {
            return Collections.emptyList();
        }
        int finalDamage = getDamage() + owner.getAttack();
        owner.setAttackBehavior(new ProjectileAttack(
                getProjectileSpeed(),
                finalDamage,
                getRange(),
                getCooldownMs()
        ));

        List<ShootImplementation.ProjectileData> result =
                implementation.createProjectiles(owner, projectileId, timestamp);

        updateLastShot(timestamp);
        return result;
    }
    @Override
    public long getCooldownMs() {
        return 900;
    }

    @Override
    public int getProjectileSpeed() {
        return 4;
    }
    @Override
    public int getDamage() {
        return 10;
    }
    @Override
    public double getRange() {
        return 800;
    }

    @Override
    public String getWeaponName() {
        return "Magic Staff";
    }
}