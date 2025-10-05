package org.game.client;

import org.game.entity.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class GameState {

    private final Map<UUID, Player> players = new ConcurrentHashMap<>();
    private final Map<Integer, Enemy> enemies = new ConcurrentHashMap<>();

    // -- player
    public void addPlayer(UUID id, ClassType type, String name, int startingX, int startingY) {
        players.putIfAbsent(id, new Player(type, name, startingX, startingY));
    }

    public Player getPlayer(UUID id) {
        return players.get(id);
    }

    public void removePlayer(UUID id) {
        players.remove(id);
    }

    public Set<Map.Entry<UUID, Player>> getPlayerEntries()  {
        return Set.copyOf(players.entrySet());
    }

    // -- enemy
    public void spawnEnemyFromServer(int id, EnemyType type, EnemySize size, int x, int y) {
        enemies.putIfAbsent(id, new Enemy(type, size, x, y));
    }

    public void updateEnemyPosition(int id, int newX, int newY) {
        Enemy enemy = enemies.get(id);
        if (enemy != null) {
            enemy.updateFromServer(newX, newY);
        }
    }

    public void removeEnemy(int id) {
        enemies.remove(id);
    }

    public Set<Map.Entry<Integer, Enemy>> getEnemiesEntries() {
        return Set.copyOf(enemies.entrySet());
    }

}
