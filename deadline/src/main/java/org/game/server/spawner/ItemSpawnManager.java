package org.game.server.spawner;

import org.game.entity.Item;
import org.game.entity.ItemType;
import org.game.server.Server;
import org.game.server.WorldSettings;
import org.game.server.factory.AttackPotionFactory;
import org.game.server.factory.HealthPotionFactory;
import org.game.server.factory.ItemFactory;
import org.game.server.factory.SpeedPotionFactory;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class ItemSpawnManager {

    private final Server server;
    private final Random random = new Random();

    private final ItemFactory healthPotionFactory = new HealthPotionFactory();
    private final ItemFactory attackPotionFactory = new AttackPotionFactory();
    private final ItemFactory speedPotionFactory = new SpeedPotionFactory();

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public ItemSpawnManager(Server server) {
        this.server = server;
    }

    public void startSpawning(long initialDelay, long period, TimeUnit timeUnit) {
        scheduler.scheduleAtFixedRate(this::spawnRandomItem, initialDelay, period, timeUnit);
    }

    private void spawnRandomItem() {

        var itemTypes = ItemType.values();
        ItemFactory spawn = switch (itemTypes[random.nextInt(itemTypes.length)]) {
            case HEALTH_POTION -> healthPotionFactory;
            case ATTACK_POTION -> attackPotionFactory;
            case SPEED_POTION -> speedPotionFactory;
        };

        int x = random.nextInt(WorldSettings.WORLD_WIDTH / 2);
        int y = random.nextInt(WorldSettings.WORLD_HEIGHT / 2);

        Item item = spawn.createItem(x, y);

        Server.ServerActions.spawnItem(server, item, x, y);
    }

}
