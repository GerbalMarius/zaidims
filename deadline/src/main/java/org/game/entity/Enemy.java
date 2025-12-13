package org.game.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.game.entity.enemy.template.EnemyAI;
import org.game.entity.state.EnemyState;
import org.game.entity.state.EnemyStateContext;
import org.game.entity.state.PatrolState;
import org.game.json.Json;
import org.game.message.Message;
import org.game.message.PlayerDefenseUpdateMessage;
import org.game.message.PlayerHealthUpdateMessage;
import org.game.server.*;

import java.awt.*;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import static org.game.json.JsonLabelPair.labelPair;

@Getter
@Setter
@Slf4j
public abstract non-sealed class Enemy extends Entity implements Prototype {

    private long id;

    protected EnemyType  type;
    protected EnemySize  size;

    protected EnemyAI ai;

    private long lastAttackTime = 0;
    private long attackCooldown = 1000;
    private double attackRange = 50.0;

    //----------------------
    private int groupId;

    private boolean groupLeader;

    private Enemy groupLeaderRef;
    //-------------------------

    private double piercingFactor = 0.0;

    private EnemyStateContext stateContext = new EnemyStateContext();

    protected Enemy(int x, int y) {
        super(x, y);
    }

    public EnemyState getState() {
        return stateContext.getCurrentState();
    }

    public PatrolState createPatrolState() {
        int x = getGlobalX();
        int y = getGlobalY();

        int[][] patrolRoute = {
                {x, y},
                {x + 150, y},
                {x + 150, y + 150},
                {x, y + 150}
        };
        return new PatrolState(patrolRoute);
    }

    protected void createHitbox() {
        this.hitbox = new Rectangle(8, 16, 11*scale, 11*scale);
    }

    public void updateAI(Collection<Player> players, Map<Long, Enemy> allEnemies, CollisionChecker checker, Server server) {
        ai.updateAI(this, players, allEnemies, checker, server);
    }

    public void tryAttack(Player target, Server server) {
        double distance = calculateDistanceTo(target);

        if (distance <= attackRange) {
            long now = System.currentTimeMillis();
            if (now - lastAttackTime >= attackCooldown) {
                attack(target, server);
                lastAttackTime = now;
            }
        }
    }

    public double calculateDistanceTo(Player target) {
        double dx = target.getGlobalX() - this.getGlobalX();
        double dy = target.getGlobalY() - this.getGlobalY();
        return Math.hypot(dx, dy);
    }

    private void attack(Player target, Server server) {
        if (target.isDead()) {
            return;
        }
        int damage = this.attack;
        target.receiveHit(damage, this);


       // log.debug("{} hit {} with {} damage", this.type, target.getName(), damage);

        UUID targetId = server.getClients().values().stream()
                .filter(cs -> cs.getName().equals(target.getName()))
                .map(ClientState::getId)
                .findFirst()
                .orElse(null);

        if (targetId != null) {
            var healthMsg = new PlayerHealthUpdateMessage(targetId, target.getHitPoints());
            var defMsg = new PlayerDefenseUpdateMessage(targetId, target.getArmorCount(), target.isShieldActive());
            Json json = server.getJson();

            server.sendToAll(json.toJson(defMsg, labelPair(Message.JSON_LABEL, "playerDefense")));
            server.sendToAll(json.toJson(healthMsg,
                    labelPair(Message.JSON_LABEL, "playerHealth")));

            if (target.getHitPoints() <= 0) {
                log.info("{} is dead! Respawn...", target.getName());

                target.setHitPoints(target.getMaxHitPoints());

                int respawnX = WorldSettings.CENTER_X;
                int respawnY = WorldSettings.CENTER_Y;
                target.setGlobalX(respawnX);
                target.setGlobalY(respawnY);

                server.respawnPlayer(targetId, respawnX, respawnY);
            }
        }
    }

    public void tryMove(int mx, int my, Collection<Enemy> otherEnemies, CollisionChecker checker) {
        int steps = Math.max(Math.abs(mx), Math.abs(my));
        if (steps == 0) return;

        int stepX = mx / steps;
        int stepY = my / steps;

        for (int i = 0; i < steps; i++) {
            setGlobalX(getGlobalX() + stepX);
            setGlobalY(getGlobalY() + stepY);
            setCollisionOn(false);

            checker.checkTile(this);

            checker.checkEntityCollision(this, otherEnemies);

            if (isCollisionOn()) {
                setGlobalX(getGlobalX() - stepX);
                setGlobalY(getGlobalY() - stepY);
                return;
            }

            moveBy(stepX, stepY);
        }
    }

    public Player getClosestPlayer(Collection<Player> players) {
        Player closest = null;
        double minDistanceSq = Double.MAX_VALUE;

        for (Player p : players) {
            double dx = p.getGlobalX() - this.getGlobalX();
            double dy = p.getGlobalY() - this.getGlobalY();
            double distSq = dx * dx + dy * dy;

            if (distSq < minDistanceSq) {
                minDistanceSq = distSq;
                closest = p;
            }
        }

        return closest;
    }

    protected void copyCombatStatsTo(Enemy e) {
        e.attack = this.attack;
        e.speed  = this.speed;
        e.hitPoints = this.hitPoints;
        e.maxHitPoints = this.maxHitPoints;
        e.size = this.size;
        e.type = this.type;
        e.piercingFactor = this.piercingFactor;
    }

    @Override
    public Prototype createShallowCopy() {
        try {
            return (Enemy) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Unable to clone entity : " + this.getClass().getSimpleName(), e);
        }
    }

}
