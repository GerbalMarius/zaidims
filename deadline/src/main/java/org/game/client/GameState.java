package org.game.client;

import org.game.entity.*;
import org.game.entity.enemy.goblin.BigGoblin;
import org.game.entity.enemy.goblin.MediumGoblin;
import org.game.entity.enemy.goblin.SmallGoblin;
import org.game.entity.enemy.skeleton.MediumSkeleton;
import org.game.entity.enemy.skeleton.SmallSkeleton;
import org.game.entity.enemy.zombie.BigZombie;
import org.game.entity.enemy.zombie.MediumZombie;
import org.game.entity.enemy.zombie.SmallZombie;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public final class GameState {

    private final Map<UUID, Player> players = new ConcurrentHashMap<>();
    private final Map<Long, Enemy> enemies = new ConcurrentHashMap<>();
    private final Map<UUID, Projectile> projectiles = new ConcurrentHashMap<>();

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

    public Map<UUID, Player> getPlayers()  {
        return Map.copyOf(players);
    }


    // -- enemy
    public void spawnEnemyFromServer(long id, EnemyType type, EnemySize size, int x, int y) {
        enemies.putIfAbsent(id,  createFromMessage(type, size, x, y));
        GlobalUI.getInstance().incrementCounter();
    }

    private Enemy createFromMessage(EnemyType type, EnemySize size, int x, int y) {

        return switch (type) {
            case ZOMBIE -> switch (size) {
                case SMALL -> new SmallZombie(x, y);
                case MEDIUM -> new MediumZombie(x, y);
                case BIG -> new BigZombie(x, y);
            };
            case SKELETON -> switch (size) {
                case SMALL -> new SmallSkeleton(x, y);
                case MEDIUM -> new MediumSkeleton(x, y);
                case BIG -> new BigZombie(x, y);
            };
            case GOBLIN -> switch (size) {
                case SMALL -> new SmallGoblin(x, y);
                case MEDIUM -> new MediumGoblin(x, y);
                case BIG -> new BigGoblin(x, y);
            };
        };
    }

    public void updateEnemyPosition(long id, int newX, int newY) {
        Enemy enemy = enemies.get(id);
        if (enemy != null) {
            enemy.updateFromServer(newX, newY);
        }
    }

    public void removeEnemy(long id) {
        if(enemies.remove(id) != null){
            GlobalUI.getInstance().decrementCounter();
        }
    }

    public Set<Map.Entry<Long, Enemy>> getEnemiesEntries() {
        return Set.copyOf(enemies.entrySet());
    }

    public Map<Long, Enemy> getEnemies()  {
        return Map.copyOf(enemies);
    }

    // projectiles
    public void spawnProjectile(UUID projectileId, int x, int y, FramePosition dir) {
        Projectile p = new Projectile(x, y, dir, 8, 10);
        projectiles.put(projectileId, p);
    }

    public void updateProjectiles() {
        for (Projectile p : projectiles.values()) {
            p.update();
        }
    }

    public void removeProjectile(UUID projectileId) {
        projectiles.remove(projectileId);
    }

    public Map<UUID, Projectile> getProjectiles() {
        return Map.copyOf(projectiles);
    }

}
