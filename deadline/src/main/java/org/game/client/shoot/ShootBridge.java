package org.game.client.shoot;

import org.game.entity.Player;

import java.util.UUID;

public interface ShootBridge {

    void onPrimaryShoot(UUID clientId, Player player, long nowMillis);
}
