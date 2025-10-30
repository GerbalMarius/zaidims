package org.game.client.shoot;

import lombok.extern.slf4j.Slf4j;
import org.game.client.GameState;
import org.game.entity.Player;
import org.game.entity.Projectile;

import java.util.UUID;

@Slf4j
public final class ClientShootBridge implements ShootBridge {

    private final GameState state;

    public ClientShootBridge(GameState state) {
        this.state = state;
    }

    @Override
    public void onPrimaryShoot(UUID clientId, Player player, long nowMillis) {
        if (player == null) return;

        Projectile localProj = player.tryAttack(nowMillis);
        if (localProj == null) {
            log.debug("No local projectile created (cooldown or no behavior) for client {}", clientId);
            return;
        }

        UUID projId = UUID.randomUUID();
        state.getProjectiles().put(projId, localProj);

        log.debug("Spawned local projectile {} class={} speed={} dmg={} range={}",
                projId, player.getPlayerClass(), localProj.getSpeed(), localProj.getDamage(), localProj.getMaxDistance());
    }
}
