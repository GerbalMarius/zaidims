package org.game.client;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.game.client.components.ChatUI;
import org.game.client.input.ControllerAdapter;
import org.game.client.input.KeyboardHandler;
import org.game.client.input.MouseHandler;
import org.game.client.mediator.GameView;
import org.game.client.mediator.Mediator;
import org.game.client.shoot.ClientShootImpl;
import org.game.client.shoot.ShootImplementation;
import org.game.entity.*;
import org.game.entity.ClassType;
import org.game.entity.Player;
import org.game.entity.Projectile;
import org.game.entity.powerup.PowerUp;
import org.game.entity.powerup.visitor.PowerUpApplicator;
import org.game.entity.weapon.Weapon;
import org.game.entity.weapon.WeaponFactory;
import org.game.server.CollisionChecker;
import org.game.server.WorldSettings;
import org.game.tiles.TileManager;
import org.game.utils.Panels;

import javax.swing.*;
import java.awt.*;

import java.util.*;
import java.util.List;

@Slf4j
public final class GamePanel extends JPanel implements Runnable, GameView {
    private static final int FPS = 60;

    @Getter
    private final GameState state;

    private final KeyboardHandler keyboardHandler;
    private final MouseHandler mouseHandler;
    private final ControllerAdapter controllerAdapter;

    private  Mediator mediator;

    private MovementCoordinator movementCoordinator;

    @Getter
    private final UUID clientId;

    private final Camera camera;

    private Weapon weapon;

    private Thread gameThread;

    private final ChatUI chatUI;

    @Getter
    private final TileManager tileManager;
    public CollisionChecker cChecker;

    private PlayerMemento quickSaveState = null;
    private boolean lastF5State = false;
    private boolean lastF9State = false;

    public GamePanel(UUID clientId,
                     GameState state,
                     KeyboardHandler keyboardHandler,
                     MouseHandler mouseHandler,
                     ControllerAdapter adapter,
                     ChatUI chatUI) {
        this.clientId = clientId;
        this.state = state;
        this.keyboardHandler = keyboardHandler;
        this.mouseHandler = mouseHandler;
        this.controllerAdapter = adapter;
        this.camera = new Camera(0.12, 80, 50);
        this.tileManager = new TileManager();
        this.cChecker = new CollisionChecker(tileManager);
        this.chatUI = chatUI;

        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this.keyboardHandler);
        addMouseListener(this.mouseHandler);
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

    private void updateGame() {
        mediator.processServerMessagesForFrame();

        long nowMillis = System.currentTimeMillis();

        if (movementCoordinator != null) {
            movementCoordinator.update(nowMillis, this.isFocusOwner());
        }

        Player currentPlayer = state.getPlayer(clientId);

        checkPlayerPowerUpCollision(currentPlayer);

        handleMementoInput(currentPlayer);

        if (currentPlayer != null) {
            currentPlayer.updateCameraPos(
                    this.camera,
                    this.getWidth(), this.getHeight(),
                    WorldSettings.WORLD_WIDTH, WorldSettings.WORLD_HEIGHT
            );
        }

        if ((mouseHandler.isPrimaryClicked() || controllerAdapter.isPrimaryClicked())
                && currentPlayer != null && weapon != null) {

            long now = System.currentTimeMillis();
            UUID baseProjectileId = UUID.randomUUID();

            List<ShootImplementation.ProjectileData> immediateShots =
                    weapon.fire(currentPlayer, baseProjectileId, now);

            for (var data : immediateShots) {
                mediator.onPlayerShoot(data.id());
            }
        }

        if (weapon != null) {
            long now = System.currentTimeMillis();
            List<ShootImplementation.ProjectileData> delayedShots = weapon.update(now);

            for (var data : delayedShots) {
                mediator.onPlayerShoot(data.id());
            }
        }

        controllerAdapter.update();
        updateProjectiles(state.getEnemies().values());
    }

    private void handleMementoInput(Player player) {
        if (player == null) return;

        boolean f5 = keyboardHandler.isSavePressed();
        boolean f9 = keyboardHandler.isLoadPressed();

        if (f5 && !lastF5State) {
            this.quickSaveState = player.createMemento();
            log.info("Checkpoint saved! HP: {}", player.getHitPoints());
        }

        if (f9 && !lastF9State) {
            if (this.quickSaveState != null) {

                player.restoreMemento(this.quickSaveState);
                log.info("Checkpoint loaded!");

                this.camera.snapTo(
                        player.getGlobalX(),
                        player.getGlobalY(),
                        getHeight(),
                        getWidth(),
                        WorldSettings.WORLD_WIDTH,
                        WorldSettings.WORLD_HEIGHT
                );

                if (movementCoordinator != null) {
                    movementCoordinator.teleportTo(player.getGlobalX(), player.getGlobalY());
                }

                if (mediator != null) {
                    mediator.onPlayerStateRestored(player);
                }
            } else {
                log.warn("No checkpoint to load!");
            }
        }

        lastF5State = f5;
        lastF9State = f9;
    }

    public void initializeWeapon(ClassType classType) {
        ShootImplementation clientImpl = new ClientShootImpl(state);
        this.weapon = WeaponFactory.createFor(classType, clientImpl);
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

            PowerUpApplicator applicator = new PowerUpApplicator(player);
            powerUp.accept(applicator);

            Player decoratedPlayer = applicator.getResultingPlayer();

            state.setPlayer(clientId, decoratedPlayer);

            mediator.onPowerUpPicked(powerUp);

            state.removePowerUp(id);
        }
    }

    public void updateProjectiles(Collection<? extends Enemy> enemies) {
        for (Projectile p : state.getProjectiles().values()) {

            p.update(enemies, cChecker, mediator::onEnemyHealthChanged);
        }

        state.getProjectiles().entrySet().removeIf(e -> !e.getValue().isActive());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        Graphics2D g2 = (Graphics2D) g;

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

        chatUI.render(g2, getWidth(), getHeight());

        g2d.dispose();
        Graphics2D uiGraphics = (Graphics2D) g.create();

        Player localPlayer = state.getPlayer(clientId);
        PlayerStatsUI.draw(uiGraphics, localPlayer);

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
            int size = tileSize * playerData.getScale();

            playerData.draw(g2d, x, y, size);

            if (playerData.isShieldActive()) {
                Color oldColor = g2d.getColor();
                Stroke oldStroke = g2d.getStroke();

                g2d.setColor(new Color(135, 206, 250, 180));
                g2d.setStroke(new BasicStroke(3f));

                int padding = 3;
                g2d.drawRoundRect(
                        x - padding,
                        y - padding,
                        size + padding * 2,
                        size + padding * 2,
                        10, 10
                );

                g2d.setColor(oldColor);
                g2d.setStroke(oldStroke);
            }
            Panels.drawNameBox(g2d, name, x, y, tileSize * playerData.getScale());
            playerData.drawHealthAndArmorBar(g2d, x, y,
                    tileSize * playerData.getScale(), Color.GREEN);
        }
    }

    // ---------------- GameView callbacks (called by mediator) ----------------

    @Override
    public void onLocalPlayerJoined(ClassType playerClass, int x, int y) {
        initializeWeapon(playerClass);

        Player player = state.getPlayer(clientId);
        if (player != null) {
            camera.snapTo(
                    player.getGlobalX(),
                    player.getGlobalY(),
                    getHeight(),
                    getWidth(),
                    WorldSettings.WORLD_WIDTH,
                    WorldSettings.WORLD_HEIGHT
            );
        }
    }

    @Override
    public void onLocalPlayerMoveFromServer(int x, int y) {
        movementCoordinator.updateServerKnownPosition(x, y);
    }

    @Override
    public void onLocalPlayerRespawn(int respawnX, int respawnY) {
       movementCoordinator.updateServerKnownPosition(respawnX, respawnY);

        JOptionPane.showMessageDialog(this,
                "lmao you dead!",
                "Respawn",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
        this.movementCoordinator = new MovementCoordinator(
                clientId,
                state,
                keyboardHandler,
                mouseHandler,
                controllerAdapter,
                cChecker,
                mediator
        );
    }
}
