package org.game.client.mediator;

import org.game.entity.Enemy;
import org.game.entity.powerup.PowerUp;
import org.game.message.Message;
import org.game.entity.Player;


import java.util.UUID;

public interface Mediator {

    // events from GamePanel to server
    void onPlayerMove(int dx, int dy);
    void onPlayerShoot(UUID projectileId);
    void onEnemyHealthChanged(Enemy enemy);
    void onPowerUpPicked(PowerUp powerUp);

    void onPlayerStateRestored(Player player);

    void onServerMessage(Message message);

    void processServerMessagesForFrame();

    void sendChatMessage(String message);

    void start();
    void shutdown();
}
