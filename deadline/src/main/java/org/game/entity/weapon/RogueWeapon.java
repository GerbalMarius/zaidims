// src/main/java/org/game/entity/weapon/RogueWeapon.java
package org.game.entity.weapon;

import org.game.client.shoot.ShootImplementation;
import org.game.entity.Player;
import org.game.entity.attack.ProjectileAttack;

import java.util.*;

public final class RogueWeapon extends Weapon {
    private static final int BURST_COUNT = 3;
    private static final long BURST_SHOT_DELAY_MS = 60;

    private final Queue<ScheduledShot> pendingShots = new LinkedList<>();

    public RogueWeapon(ShootImplementation implementation) {
        super(implementation);
    }

    @Override
    public List<ShootImplementation.ProjectileData> fire(Player owner, UUID projectileId, long timestamp) {
        if (!canShoot(timestamp)) {
            return Collections.emptyList();
        }
        int finalDamage = (getDamage() + owner.getAttack())/3;
        owner.setAttackBehavior(new ProjectileAttack(
                getProjectileSpeed(),
                finalDamage,
                getRange(),
                getCooldownMs()
        ));

        for (int i = 0; i < BURST_COUNT; i++) {
            UUID id = (i == 0) ? projectileId : UUID.randomUUID();
            long shotTime = timestamp + (i * BURST_SHOT_DELAY_MS);
            pendingShots.offer(new ScheduledShot(owner, id, shotTime));
        }

        updateLastShot(timestamp);
        return Collections.emptyList();
    }

    public List<ShootImplementation.ProjectileData> update(long currentTime) {
        List<ShootImplementation.ProjectileData> firedProjectiles = new ArrayList<>();

        while (!pendingShots.isEmpty() && pendingShots.peek().timestamp <= currentTime) {
            ScheduledShot shot = pendingShots.poll();
            List<ShootImplementation.ProjectileData> projectiles =
                    implementation.createProjectiles(shot.owner, shot.projectileId, shot.timestamp);
            firedProjectiles.addAll(projectiles);
        }

        return firedProjectiles;
    }

    @Override
    public long getCooldownMs() {
        return 500;
    }

    @Override
    public int getProjectileSpeed() {
        return 8;
    }

    @Override
    public double getRange() {
        return 300;
    }

    @Override
    public int getDamage() {
        return 5;
    }

    @Override
    public String getWeaponName() {
        return "Dual Daggers";
    }

    private record ScheduledShot(Player owner, UUID projectileId, long timestamp) {}
}
