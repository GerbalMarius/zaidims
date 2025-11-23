package org.game.client;

import lombok.Getter;
import org.game.entity.ClassType;
import org.game.entity.Enemy;
import org.game.entity.EnemySize;
import org.game.entity.EnemyType;
import org.game.entity.FramePosition;
import org.game.entity.GlobalUI;
import org.game.entity.Player;
import org.game.entity.Projectile;

import org.game.entity.enemy.creator.EnemyMessageCreator;

import org.game.entity.iterator.EnemyIterator;
import org.game.entity.iterator.EntityIterator;
import org.game.entity.iterator.PlayerIterator;
import org.game.entity.iterator.ProjectileIterator;
import org.game.entity.powerup.AttackPowerUp;
import org.game.entity.powerup.CorePowerUp;
import org.game.entity.powerup.MaxHpPowerUp;
import org.game.entity.powerup.PowerUp;
import org.game.entity.powerup.PowerUpType;
import org.game.entity.powerup.SpeedPowerUp;
import org.game.message.EnemyCopy;

import java.awt.*;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public final class GameState {

    private final Map<UUID, Player> players = new ConcurrentHashMap<>();
    @Getter
    private  Map<Long, Enemy> enemies = new ConcurrentHashMap<>();
    @Getter
    private final Map<UUID, Projectile> projectiles = new ConcurrentHashMap<>();

    @Getter
    private final Map<Long, PowerUp> powerUps = new ConcurrentHashMap<>();

    private final EnemyMessageCreator messageCreator = new EnemyMessageCreator();

    // -- player
    public void addPlayer(UUID id, ClassType type, String name, int startingX, int startingY) {
        players.putIfAbsent(id, new Player(type, name, startingX, startingY));
    }

    public Player getPlayer(UUID id) {
        return players.get(id);
    }

    public void setPlayer(UUID id, Player value){
        players.put(id, value);
    }

    public void removePlayer(UUID id) {
        players.remove(id);
    }

    public Map<UUID, Player> getPlayers()  {
        return Map.copyOf(players);
    }


    // -- enemy
    public void spawnEnemyFromServer(long id, EnemyType type, EnemySize size, int x, int y) {
        Enemy enemy = messageCreator.spawn(type,size, id, x, y);
        enemies.put(id, enemy);
        GlobalUI.getInstance().incrementCounter();
    }

    public synchronized void copyAllEnemies(Map<Long, EnemyCopy> enemies) {

        this.enemies = new ConcurrentHashMap<>(enemies.size());

        for (var copy : enemies.entrySet()) {
            EnemyCopy enData = copy.getValue();

             Enemy fromMessage = messageCreator.spawn(
                    enData.enemyType(),

                    enData.enemySize(),

                    enData.id(),

                    enData.initialX(),

                    enData.initialY()
             );

            this.enemies.put(fromMessage.getId(), fromMessage);
            GlobalUI.getInstance().incrementCounter();

        }
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

    // projectiles
    public void spawnProjectile(UUID projId, UUID playerId, int startX, int startY, FramePosition dir,
                                int speed, int damage, double maxDistance) {
        if (this.getProjectiles().containsKey(projId)) return;
        Player player = this.getPlayer(playerId);

        Color attackColor = player.getPlayerClass().attackColor();

        Projectile p = new Projectile(startX, startY, dir, speed, damage, maxDistance, attackColor);
        this.getProjectiles().put(projId, p);
    }

    //powerups
    public void spawnPowerUp(long id, PowerUpType type, int x, int y) {

        CorePowerUp powerUp = switch (type) {
            case MAX_HP -> new MaxHpPowerUp(x, y);
            case ATTACK -> new AttackPowerUp(x, y);
            case SPEED -> new SpeedPowerUp(x, y);
        };
        powerUp.setId(id);

        powerUps.put(id, powerUp);
    }

    public void removePowerUp(long id) {
        powerUps.remove(id);
    }

    public EntityIterator<Player> getPlayerIterator() {
        return new PlayerIterator(players);
    }

    public EntityIterator<Enemy> getEnemyIterator() {
        return new EnemyIterator(enemies);
    }

    public EntityIterator<Projectile> getProjectileIterator() {
        return new ProjectileIterator(projectiles);
    }
}