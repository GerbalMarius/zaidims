package org.game.client.mediator;

import org.game.entity.Enemy;
import org.game.entity.powerup.PowerUp;
import org.game.message.Message;

import java.util.UUID;

public interface Mediator {

    // events from GamePanel to server
    void onPlayerMove(int dx, int dy);
    void onPlayerShoot(UUID projectileId);
    void onEnemyHealthChanged(Enemy enemy);
    void onPowerUpPicked(PowerUp powerUp);

    void onServerMessage(Message message);

    void processServerMessagesForFrame();

    void start();
    void shutdown();
}
