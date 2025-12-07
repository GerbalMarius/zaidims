package org.game.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.game.client.Camera;
import org.game.entity.attack.AttackBehavior;
import org.game.entity.damage_handler.*;
import org.game.utils.ByteFiles;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;


@Getter
@Slf4j
public non-sealed class Player extends Entity {

    private final String name;

    private final ClassType playerClass;

    @Setter
    private int hpRegenAmount;

    private long hpRegenIntervalMs;

    private long lastRegenTimestamp;

    private DamageHandler damageHandler;

    @Setter
    private boolean isShieldActive = false;

    @Setter
    private int armorCount = 0;

    private int maxArmorCount;

    private static final BufferedImage ARMOR_ICON;

    static {
        BufferedImage icon = null;
        try {
            icon = ByteFiles.loadImage("assets/powerups/armor.png");
        } catch (Exception e) {
            log.error("Failed to load armor icon", e);
        }
        ARMOR_ICON = icon;
    }

    @Setter
    private AttackBehavior attackBehavior;

    private long lastAttackTimestamp;

    public Player(ClassType type, String name, int x, int y) {
        Objects.requireNonNull(name);
        super(x, y);
        this.name = name;
        this.playerClass = type;

        loadSprite(playerClass.getClassPrefix(), "player");

        this.lastRenderX = getRenderX();
        this.lastRenderY = getRenderY();

        this.scale = 3;
        this.hitbox = new Rectangle(8, 16, 11 * scale, 11 * scale);
        setStats();

        setClassRegenDefaults();

        this.lastAttackTimestamp = 0L;

        //def damage appliers + piercing respect
        this.damageHandler = new RawDamageHandler();
        this.addHandler(new PiercingDamageHandler());
    }


    public void updateCameraPos(Camera camera, int screenWidth, int screenHeight, int worldWidth, int worldHeight) {
        double targetX = this.getRenderX();
        double targetY = this.getRenderY();

        camera.update(targetX, targetY);

        camera.clamp(screenWidth, screenHeight, worldWidth, worldHeight);
    }

    private void setStats() {
        switch (this.playerClass) {
            case WARRIOR -> {
                speed = 3;
                maxArmorCount = 10;
                maxHitPoints = hitPoints = 100;
                attack = 25;
            }
            case WIZARD -> {
                speed = 4;
                maxArmorCount = 5;
                maxHitPoints = hitPoints = 50;
                attack = 30;
            }
            case ROGUE -> {
                speed = 5;
                maxArmorCount = 3;
                maxHitPoints = hitPoints = 70;
                attack = 15;
            }
        }
    }

    private void setClassRegenDefaults() {
        switch (this.playerClass) {
            case WARRIOR -> { hpRegenAmount = 3; hpRegenIntervalMs = 2_000; }
            case WIZARD  -> { hpRegenAmount = 2; hpRegenIntervalMs = 3_000; }
            case ROGUE   -> { hpRegenAmount = 1; hpRegenIntervalMs = 1_000; }
        }
        lastRegenTimestamp = System.currentTimeMillis();
    }

    public boolean regenIfNeeded(long nowMillis) {
        if (isDead()) return false;
        if (getHitPoints() >= getMaxHitPoints()) {
            lastRegenTimestamp = nowMillis;
            return false;
        }
        if (nowMillis - lastRegenTimestamp >= hpRegenIntervalMs) {
            int newHp = Math.min(getMaxHitPoints(), getHitPoints() + hpRegenAmount);
            if (newHp != getHitPoints()) {
                setHitPoints(newHp);
                lastRegenTimestamp = nowMillis;
                log.debug("{} regenerated {} HP -> {}/{}", name, hpRegenAmount, getHitPoints(), getMaxHitPoints());
                return true;
            } else {
                lastRegenTimestamp = nowMillis;
            }
        }
        return false;
    }

    public void addHandler(DamageHandler handler) {
        handler.linkNext(this.damageHandler);
        this.damageHandler = handler;
    }

    public ArmorDamageHandler findArmorHandler() {
        for (DamageHandler handler = this.damageHandler; handler != null; handler = handler.getNext()) {
            if (handler instanceof ArmorDamageHandler armorHandler) {
                return armorHandler;
            }
        }
        return null;
    }

    public ShieldDamageHandler findShieldHandler() {
        for (DamageHandler handler = this.damageHandler; handler != null; handler = handler.getNext()) {
            if (handler instanceof ShieldDamageHandler shieldHandler) {
                return shieldHandler;
            }
        }
        return null;
    }

    public void receiveHit(int rawDamage, Enemy source) {
        if (isDead()) return;

        DamageContext ctx = new DamageContext(rawDamage, this, source);
        damageHandler.handle(ctx);

    }

    public void takeDamage(int dmg) {
        if (getHitPoints() <= 0) return;
        this.setHitPoints(this.getHitPoints() - dmg);
        if (this.getHitPoints() < 0) this.setHitPoints(0);
        //log.debug("{} received {} dmg pts. HP left: {}", name, dmg, getHitPoints());
    }

    public void drawHealthAndArmorBar(Graphics2D g2, int x, int y, int width, Color hpColor) {
        super.drawHealthBar(g2, x, y, width, hpColor);

        if (armorCount <= 0 || ARMOR_ICON == null) {
            return;
        }

        int barHeight = 6;
        int offsetY = -10;

        int usesToShow = Math.min(armorCount, maxArmorCount);
        if (usesToShow <= 0) {
            return;
        }

        int iconSize = 16;
        double scaleFactor = (double) iconSize / ARMOR_ICON.getWidth();

        int iconWidth  = (int) (ARMOR_ICON.getWidth()  * scaleFactor);
        int iconHeight = (int) (ARMOR_ICON.getHeight() * scaleFactor);

        int barTopY    = y + offsetY;
        int barCenterY = barTopY + barHeight / 2;
        int iconY      = barCenterY - iconHeight / 2;

        int gap = 2;
        int startX = x + width + 6;

        for (int i = 0; i < usesToShow; i++) {
            int iconX = startX + i * (iconWidth + gap);

            if (isShieldActive) {
                Color oldColor = g2.getColor();
                g2.setColor(new Color(135, 206, 250, 120));
                g2.fillRoundRect(iconX - 1, iconY - 1, iconWidth + 2, iconHeight + 2, 4, 4);
                g2.setColor(oldColor);
            }

            g2.drawImage(ARMOR_ICON, iconX, iconY, iconWidth, iconHeight, null);
        }
    }
}
