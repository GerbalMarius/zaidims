package org.game.server.powerup;

import org.game.entity.powerup.*;
import org.game.entity.powerup.dispenser.AttackDispenser;
import org.game.entity.powerup.dispenser.MaxHpDispenser;
import org.game.entity.powerup.dispenser.SpeedDispenser;
import org.game.server.Server;
import org.game.tiles.TileManager;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class PowerUpManager {

    private final Server server;

    private static long powerUpId = 0;

    private final Random random = new Random();

    private final SpeedDispenser speedDispenser = new SpeedDispenser();
    private final  AttackDispenser attackDispenser = new AttackDispenser();
    private final  MaxHpDispenser maxHpDispenser = new MaxHpDispenser();

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public PowerUpManager(Server server) {
        this.server = server;
    }

    public void startDispensing(long initialDelay, long period, TimeUnit timeUnit) {

        scheduler.scheduleAtFixedRate(this::dispenseRandomPowerUp, initialDelay, period, timeUnit);
    }

    private  void dispenseRandomPowerUp() {

        TileManager tileManager = server.getEntityChecker().getTileManager();

        Point pos = tileManager.findRandomSpawnPosition(random, 50);
        final int x = pos.x;
        final int y = pos.y;

        double rand = random.nextDouble();
        PowerUpType powerUpType;

        if (rand < 0.10) {
            powerUpType = PowerUpType.SPEED; // 10%
        } else if (rand < 0.55) {
            powerUpType = PowerUpType.ATTACK; // 45%
        } else {
            powerUpType = PowerUpType.MAX_HP; // 45%
        }

        PowerUp selectedPowerUp = switch (powerUpType) {
            case SPEED -> speedDispenser.dispensePowerUp(x, y);
            case ATTACK -> attackDispenser.dispensePowerUp(x, y);
            case MAX_HP -> maxHpDispenser.dispensePowerUp(x, y);
        };

        selectedPowerUp.setId(powerUpId++);
        Server.ServerActions.spawnPowerUp(server, selectedPowerUp, powerUpType, x, y);
    }

}
