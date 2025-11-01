package org.game.client;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.game.client.input.ControllerAdapter;
import org.game.client.input.KeyboardHandler;
import org.game.client.input.MouseHandler;
import org.game.client.shoot.ClientShootImpl;
import org.game.client.shoot.ShootImplementation;
import org.game.entity.*;
import org.game.entity.powerup.PowerUp;
import org.game.entity.powerup.PowerUpType;
import org.game.entity.decorator.AttackDecorator;
import org.game.entity.decorator.SpeedDecorator;
import org.game.entity.weapon.Weapon;
import org.game.entity.weapon.WeaponFactory;
import org.game.server.CollisionChecker;
import org.game.tiles.TileManager;
import org.game.message.*;
import org.game.server.WorldSettings;
import org.game.utils.Panels;
import javax.swing.*;
import java.awt.*;

import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Slf4j
public final class GamePanel extends JPanel implements Runnable {
    private static final int FPS = 60;

    @Getter
    private final GameState state;

    private final KeyboardHandler keyboardHandler;
    private final MouseHandler mouseHandler;
    private final ControllerAdapter controllerAdapter;

    @Getter
    private final UUID clientId;

    private final Camera camera;

    @Setter
    private BiConsumer<Integer, Integer> moveCallback;

    @Setter
    private Consumer<UUID> shootCallback;
    private Weapon weapon;
    @Setter
    private Consumer<? super Enemy> healthCallback;

    @Setter
    private Consumer<? super PowerUp> powerUpCallback;

    private final Queue<Message> incomingMessages = new ConcurrentLinkedQueue<>();

    private int pendingDx = 0;
    private int pendingDy = 0;
    private long lastSendTime = 0;
    private Thread gameThread;

    @Getter
    private final TileManager tileManager;
    public CollisionChecker cChecker;


    public GamePanel(UUID clientId, GameState state, KeyboardHandler keyboardHandler, MouseHandler mouseHandler, ControllerAdapter adapter) {
        this.clientId = clientId;
        this.state = state;
        this.keyboardHandler = keyboardHandler;
        this.mouseHandler = mouseHandler;
        this.camera = new Camera(0.12, 80, 50);
        this.tileManager = new TileManager();
        cChecker = new CollisionChecker(tileManager);
        this.controllerAdapter = adapter;

        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this.keyboardHandler);
        addMouseListener(this.mouseHandler);

        initialSnap(state.getPlayer(clientId));
    }



    public void startGameLoop() {
        gameThread = Thread.ofPlatform()
                .name("Game Window")
                .start(this);

    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000D / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                updateGame();
                repaint();
                delta--;
            }
        }
        controllerAdapter.shutdown();
    }
    public void initializeWeapon(ClassType classType) {
        ShootImplementation clientImpl = new ClientShootImpl(state);
        this.weapon = WeaponFactory.createFor(classType, clientImpl);
    }
    private void updateGame() {

        Player currentPlayer = state.getPlayer(clientId);
        optimisticMove(currentPlayer);
        checkPlayerPowerUpCollision(currentPlayer);

        long nowMillis = System.currentTimeMillis();
        if (nowMillis - lastSendTime > 50) {
            if (pendingDx != 0 || pendingDy != 0) {
                sendBatchedMove();
                pendingDx = 0;
                pendingDy = 0;
            }
            lastSendTime = nowMillis;
        }
        processNetworkMessages();

        if (currentPlayer != null) {
            currentPlayer.updateCameraPos(this.camera, this.getWidth(), this.getHeight(), WorldSettings.WORLD_WIDTH, WorldSettings.WORLD_HEIGHT);
        }

        if ((mouseHandler.isPrimaryClicked() || controllerAdapter.isPrimaryClicked())
                && currentPlayer != null && weapon != null) {

            long now = System.currentTimeMillis();
            UUID baseProjectileId = UUID.randomUUID();

            List<ShootImplementation.ProjectileData> immediateShots =
                    weapon.fire(currentPlayer, baseProjectileId, now);

            for (var data : immediateShots) {
                if (shootCallback != null) {
                    shootCallback.accept(data.id());
                }
            }
        }
        if (weapon != null) {
            long now = System.currentTimeMillis();
            List<ShootImplementation.ProjectileData> delayedShots = weapon.update(now);

            for (var data : delayedShots) {
                if (shootCallback != null) {
                    shootCallback.accept(data.id());
                }
            }
        }
        controllerAdapter.update();
        updateProjectiles(state.getEnemies().values());
    }

    private void checkPlayerPowerUpCollision(Player player) {
        if (player == null) return;

        for (var entry : state.getPowerUps().entrySet()) {
            long id = entry.getKey();
            PowerUp powerUp = entry.getValue();

            Rectangle playerHitbox = new Rectangle(
                    player.getGlobalX(), player.getGlobalY(),
                    WorldSettings.ORIGINAL_TILE_SIZE, WorldSettings.ORIGINAL_TILE_SIZE
            );

            if (!playerHitbox.intersects(powerUp.getHitbox())) {
                continue;
            }


            Player decoratedPlayer = switch (powerUp.getClass().getSimpleName().toLowerCase()) {
                case String s when s.contains("attack") -> new AttackDecorator(player, 5);
                case String s when s.contains("speed") -> new SpeedDecorator(player, 1);
                default -> player;
            };

            state.setPlayer(clientId, decoratedPlayer);

            if (powerUpCallback != null) {
                powerUpCallback.accept(powerUp);
            }

            state.removePowerUp(id);

        }
    }

    public void updateProjectiles(Collection<? extends Enemy> enemies) {
        for (Projectile p : state.getProjectiles().values()) {
            p.update(enemies, cChecker, healthCallback);
        }

        state.getProjectiles().entrySet().removeIf(e -> !e.getValue().isActive());
    }

    private void optimisticMove(Player player) {
        if (player == null ||  (!keyboardHandler.anyKeyPressed()
                               && !controllerAdapter.anyKeyPressed()
                               && !mouseHandler.anyKeyPressed())
                   || !player.isAlive() || !this.isFocusOwner()) {
            return;
        }

        int dx = 0, dy = 0;

        int speed = player.getSpeed();

        if (keyboardHandler.isLeftPressed() || controllerAdapter.isLeftPressed()) {
            dx -= speed;
        }

        if (keyboardHandler.isRightPressed() || controllerAdapter.isRightPressed()) {
            dx += speed;
        }

        if (keyboardHandler.isUpPressed() || controllerAdapter.isUpPressed()) {
            dy -= speed;
        }

        if (keyboardHandler.isDownPressed() || controllerAdapter.isDownPressed()) {
            dy += speed;
        }


        if (dx != 0) {
            player.setCollisionOn(false);
            player.setDirection(dx > 0 ? FramePosition.RIGHT : FramePosition.LEFT);
            cChecker.checkTile(player);
            if (!player.isCollisionOn()) {
                player.moveBy(dx, 0);
                pendingDx += dx;
            }
        }

        if (dy != 0) {
            player.setCollisionOn(false);
            player.setDirection(dy > 0 ? FramePosition.DOWN : FramePosition.UP);
            cChecker.checkTile(player);
            if (!player.isCollisionOn()) {
                player.moveBy(0, dy);
                pendingDy += dy;
            }
        }
    }


    private void sendBatchedMove() {
        if (moveCallback != null) {
            moveCallback.accept(pendingDx, pendingDy);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        double targetX = getWidth() / 2.0 - camera.getX();
        double targetY = getHeight() / 2.0 - camera.getY();
        g2d.translate(targetX, targetY);

        tileManager.draw(g2d, this.camera, getWidth(), getHeight());

        Map<Long, PowerUp> powerUps = state.getPowerUps();
        Map<UUID, Player> players = state.getPlayers();

        redrawPowerUps(g2d, powerUps);

        redrawPlayers(players, g2d);
        redrawEnemies(g2d);

        for (Projectile p : state.getProjectiles().values()) {
            p.draw(g2d);
        }

        g2d.dispose();
        GlobalUI.getInstance().drawCounter(g2d, getWidth());

        Graphics2D uiGraphics = (Graphics2D) g.create();
        GlobalUI.getInstance().drawCounter(uiGraphics, getWidth());
        uiGraphics.dispose();
    }

    private void redrawPowerUps(Graphics2D g2d, Map<Long, PowerUp> powerUps) {
        for (var powerup : powerUps.values()) {
            powerup.draw(g2d);
        }
    }

    private void redrawEnemies(Graphics2D g2d) {
        final int tileSize = WorldSettings.ORIGINAL_TILE_SIZE;
        for (var enemyEntry : state.getEnemiesEntries()) {
            Enemy enemy = enemyEntry.getValue();
            int enemyX = enemy.getRenderX();
            int enemyY = enemy.getRenderY();

            enemy.updateDirectionByRender();
            enemy.draw(g2d, enemyX, enemyY, tileSize * enemy.getScale());
            enemy.drawHealthBar(g2d, enemyX, enemyY, tileSize * enemy.getScale(), Color.RED);
        }
    }

    private void redrawPlayers(Map<UUID, Player> players, Graphics2D g2d) {
        final int tileSize = WorldSettings.ORIGINAL_TILE_SIZE;
        for (var playerEntry : players.entrySet()) {
            Player playerData = playerEntry.getValue();

            playerData.updateDirectionByRender();

            int x, y;
            if (playerEntry.getKey().equals(clientId)) {
                x = playerData.getGlobalX();
                y = playerData.getGlobalY();
            } else {
                x = playerData.getRenderX();
                y = playerData.getRenderY();
            }

            String name = playerData.getName();

            playerData.draw(g2d, x, y, tileSize * playerData.getScale());
            Panels.drawNameBox(g2d, name, x, y, tileSize * playerData.getScale());
            playerData.drawHealthBar(g2d, x, y, tileSize * playerData.getScale(), Color.GREEN);
        }
    }

    public void processMessage(final Message message) {
        incomingMessages.offer(message);
    }

    private void processNetworkMessages() {
        Message msg;
        int processed = 0;
        int maxMsgPerTick = 100;
        while (processed < maxMsgPerTick && (msg = incomingMessages.poll()) != null) {
            switch (msg) {
                case JoinMessage(UUID playerId, ClassType playerClass, String name, int x, int y) -> {
                    state.addPlayer(playerId, playerClass, name, x, y);
                    if (playerId.equals(clientId)) {
                        initializeWeapon(playerClass);
                    }
                }
                case LeaveMessage(UUID playerId) -> state.removePlayer(playerId);
                case MoveMessage(UUID playerId, int x, int y) -> {
                    if (!playerId.equals(clientId)) {
                        Player player = state.getPlayer(playerId);
                        if (player != null) {
                            player.updateFromServer(x, y);
                        }
                    }
                }
                case EnemySpawnMessage(var enemyId, EnemyType type, EnemySize size, int newX, int newY) ->
                        state.spawnEnemyFromServer(enemyId, type, size, newX, newY);
                case EnemyRemoveMessage(var enemyId) -> state.removeEnemy(enemyId);
                case EnemyMoveMessage(var enemyId, int newX, int newY) ->
                        state.updateEnemyPosition(enemyId, newX, newY);
                case ProjectileSpawnMessage(int startX, int startY, FramePosition dir, UUID projId, UUID playerId, int speed, int damage, double maxDistance) ->
                        state.spawnProjectile(projId, playerId, startX, startY, dir, speed, damage, maxDistance);

                case EnemyBulkCopyMessage(Map<Long, EnemyCopy> enemies) -> state.copyAllEnemies(enemies);
                case EnemyHealthUpdateMessage(long enemyId, int newHealth) -> {
                    Enemy enemy = state.getEnemies().get(enemyId);
                    if (enemy != null) {
                        enemy.setHitPoints(newHealth);
                    }
                }
                case PlayerHealthUpdateMessage(UUID playerId, int newHealth) -> {
                    Player player = state.getPlayer(playerId);
                    if (player != null) {
                        player.setHitPoints(newHealth);
                    }
                }
                case PlayerRespawnMessage(UUID playerId, int respawnX, int respawnY) -> {
                    Player player = state.getPlayer(playerId);
                    if (player != null) {
                        player.setGlobalX(respawnX);
                        player.setGlobalY(respawnY);
                        player.setHitPoints(player.getMaxHitPoints());

                        if (playerId.equals(clientId)) {
                            JOptionPane.showMessageDialog(this, "lmao you dead!", "Respawn", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }

                case PowerUpRemoveMessage(long powerUpId) -> state.removePowerUp(powerUpId);
                case PowerUpSpawnMessage(long powerUpId, PowerUpType powerUp, int x, int y) ->
                        state.spawnPowerUp(powerUpId, powerUp, x, y);
                case PlayerStatsUpdateMessage(
                        UUID playerId, int hitPoints, int maxHitPoints, int attack, int speed
                ) -> {
                    Player player = state.getPlayer(playerId);
                    if (player != null) {
                        player.setHitPoints(hitPoints);
                        player.setMaxHitPoints(maxHitPoints);
                        player.setAttack(attack);
                        player.setSpeed(speed);
                    }
                }


            }
            processed++;
        }
    }

    private void initialSnap(Player player) {
        if (player == null) {
            return;
        }
        this.camera.snapTo(player.getGlobalX(), player.getGlobalY(), getHeight(), getWidth(), WorldSettings.WORLD_WIDTH, WorldSettings.WORLD_HEIGHT);
    }
}
