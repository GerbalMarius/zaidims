package org.game.entity.weapon;

import org.game.client.shoot.ShootImplementation;
import org.game.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class Weapon {
    protected final ShootImplementation implementation;
    protected long lastShotTime = 0;

    protected Weapon(ShootImplementation implementation) {
        this.implementation = implementation;
    }

    public abstract List<ShootImplementation.ProjectileData> fire(Player owner, UUID projectileId, long timestamp);

    public List<ShootImplementation.ProjectileData> update(long currentTime) {
        return Collections.emptyList(); // Default: no delayed shots
    }

    public abstract long getCooldownMs();
    public abstract int getProjectileSpeed();
    public abstract double getRange();
    public abstract int getDamage();
    public abstract String getWeaponName();

    protected boolean canShoot(long timestamp) {
        return timestamp - lastShotTime >= getCooldownMs();
    }

    protected void updateLastShot(long timestamp) {
        this.lastShotTime = timestamp;
    }
}
