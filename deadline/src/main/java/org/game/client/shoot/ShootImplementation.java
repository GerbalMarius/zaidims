package org.game.client.shoot;

import org.game.entity.Player;
import org.game.entity.Projectile;

import java.util.List;
import java.util.UUID;

public interface ShootImplementation {
    List<ProjectileData> createProjectiles(Player player, UUID baseProjectileId, long timestamp);

    record ProjectileData(UUID id, Projectile projectile) {}
}