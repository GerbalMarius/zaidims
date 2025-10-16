package org.game.server.spawner;

import org.game.entity.Enemy;
import org.game.entity.Player;
import org.game.server.ClientState;
import org.game.server.Server;
import org.game.server.Server.ServerActions;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public final class EnemyUpdateManager {

    private final Server server;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public EnemyUpdateManager(Server server) {
        this.server = server;
    }

    public void startUpdating() {
        scheduler.scheduleAtFixedRate(this::updateEnemies, 0, 50, TimeUnit.MILLISECONDS); // kas 100ms
    }

    private void updateEnemies() {
        if (server.getEnemies().isEmpty() || server.getClients().isEmpty()) {
            return;
        }

        List<Player> players = server.getClients().values().stream()
                .filter(cs -> cs.getId() != null)
                .map(ClientState::getPlayer)
                .filter(Objects::nonNull)
                .toList();

        var enemies = server.getEnemies();

        for (var entry : enemies.entrySet()) {
            long id = entry.getKey();
            Enemy enemy = entry.getValue();

            enemy.updateAI(players, enemies, server.getEntityChecker(), server);
            ServerActions.broadcastEnemyMove(id, enemy.getGlobalX(), enemy.getGlobalY(), server);
        }
    }
}
