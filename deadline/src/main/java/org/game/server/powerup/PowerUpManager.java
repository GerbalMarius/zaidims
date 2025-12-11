package org.game.server.powerup;

import org.game.entity.powerup.*;
import org.game.entity.powerup.dispenser.*;
import org.game.server.Server;
import org.game.tiles.TileManager;

import java.awt.*;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class PowerUpManager {

    private final Server server;

    private static long powerUpId = 0;

    private final Random random = new Random();

    private final Map<PowerUpType, PowerUpDispenser> dispensers = Map.of(
            PowerUpType.SPEED,   new SpeedDispenser(),
            PowerUpType.ATTACK,  new AttackDispenser(),
            PowerUpType.MAX_HP,  new MaxHpDispenser(),
            PowerUpType.ARMOR,   new ArmorDispenser(),
            PowerUpType.SHIELD,  new ShieldDispenser()
    );

    // Spawn chances in one neat map (weights, not necessarily summing to 1.0)
    private final Map<PowerUpType, Double> spawnWeights = Map.of(
            PowerUpType.SPEED, 0.0,
            PowerUpType.ATTACK, 0.0,
            PowerUpType.MAX_HP, 0.0,
            PowerUpType.ARMOR, 0.5,
            PowerUpType.SHIELD, 0.5
    );

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public PowerUpManager(Server server) {
        this.server = server;
    }

    public void startDispensing(long initialDelay, long period, TimeUnit timeUnit) {
        scheduler.scheduleAtFixedRate(this::dispenseRandomPowerUp, initialDelay, period, timeUnit);
    }

    private void dispenseRandomPowerUp() {
        TileManager tileManager = server.getEntityChecker().getTileManager();
        Point pos = tileManager.findRandomSpawnPosition(random, 50);
        final int x = pos.x;
        final int y = pos.y;

        PowerUpType powerUpType = randomPowerUpType();
        PowerUpDispenser dispenser = dispensers.get(powerUpType);

        PowerUp selectedPowerUp = dispenser.dispensePowerUp(x, y);
        selectedPowerUp.setId(powerUpId++);

        Server.ServerActions.spawnPowerUp(server, selectedPowerUp, powerUpType, x, y);
    }

    private PowerUpType randomPowerUpType() {
        // sum weights
        double totalWeight = 0.0;
        for (double w : spawnWeights.values()) {
            totalWeight += w;
        }

        double r = random.nextDouble() * totalWeight;
        double cumulative = 0.0;

        for (Map.Entry<PowerUpType, Double> entry : spawnWeights.entrySet()) {
            cumulative += entry.getValue();
            if (r <= cumulative) {
                return entry.getKey();
            }
        }

        return PowerUpType.ATTACK;
    }

}
