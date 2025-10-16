package org.game.server.powerup;

import org.game.entity.powerup.*;
import org.game.entity.powerup.dispenser.AttackDispenser;
import org.game.entity.powerup.dispenser.MaxHpDispenser;
import org.game.entity.powerup.dispenser.SpeedDispenser;
import org.game.server.Server;
import org.game.server.WorldSettings;
import org.game.tiles.TileManager;

import java.util.Arrays;
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

    private synchronized void dispenseRandomPowerUp() {
        PowerUpType[] availablePowerUpTypes = PowerUpType.values();

        int[] pos = findSpawnPosition();

        final int x = pos[0];
        final int y = pos[1];

        System.out.println("Available powerups: " + Arrays.toString(availablePowerUpTypes));

        PowerUpType powerUpType = availablePowerUpTypes[random.nextInt(availablePowerUpTypes.length)];

        PowerUp selectedPowerUp = switch (powerUpType) {
            case SPEED -> speedDispenser.dispensePowerUp(x, y);
            case ATTACK -> attackDispenser.dispensePowerUp(x, y);
            case MAX_HP -> maxHpDispenser.dispensePowerUp(x, y);
        };

        selectedPowerUp.setId(powerUpId++);

        Server.ServerActions.spawnPowerUp(server, selectedPowerUp, powerUpType, x, y);
    }

    private int[] findSpawnPosition() {
        int maxAttempts = 50;
        for (int i = 0; i < maxAttempts; i++) {
            int x = random.nextInt(server.getEntityChecker().getTileManager().getMapTileNum()[0].length * 32);
            int y = random.nextInt(server.getEntityChecker().getTileManager().getMapTileNum().length * 32);

            if (isWalkableTile(x, y)) {
                return new int[]{x, y};
            }
        }
        return new int[]{0, 0}; // fallback
    }


    private boolean isWalkableTile(int x, int y) {
        TileManager tileManager = server.getEntityChecker().getTileManager();

        int tileSize = WorldSettings.TILE_SIZE;
        int col = x / tileSize;
        int row = y / tileSize;

        int tileNum = tileManager.getMapTileNum()[row][col];
        return !tileManager.getTiles().get(tileNum).hasCollision();
    }
}
