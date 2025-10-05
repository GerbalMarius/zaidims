package org.game.entity;

import lombok.Getter;
import lombok.Setter;
import org.game.server.CollisionCheckerServer;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
public final class Enemy extends Entity {

    private  EnemyType  type;
    private  EnemySize  size;

    private int lastRenderX;
    private int lastRenderY;

    public Enemy(EnemyType type, EnemySize size, int x, int y) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(size);
        super(x, y);
        this.type = type;
        this.size = size;

        loadSprite(type.getTypePrefix(), "enemy");
        configureStats();

        this.hitbox = new Rectangle(8, 16, 11*scale, 11*scale);
    }

    private void configureStats() {
        switch(type) {
            case ZOMBIE -> {
                switch(size) {
                    case SMALL -> { hitPoints = 30; attack = 5; scale = 3; speed = 3; }
                    case MEDIUM -> { hitPoints = 60; attack = 10; scale = 4; speed = 2; }
                    case BIG -> { hitPoints = 120; attack = 25; scale = 5; speed = 1; }
                }
            }
            case SKELETON ->  {
                switch(size) {
                    case SMALL -> { hitPoints = 20; attack = 7; scale = 3; speed = 4; }
                    case MEDIUM -> { hitPoints = 40; attack = 12; scale = 4; speed = 3; }
                    case BIG -> { hitPoints = 90; attack = 25; scale = 5; speed = 2; }
                }
            }
            case GOBLIN -> {
                switch(size) {
                    case SMALL -> { hitPoints = 25; attack = 8; scale = 3; speed = 5; }
                    case MEDIUM -> { hitPoints = 50; attack = 15; scale = 4; speed = 4; }
                    case BIG -> { hitPoints = 80; attack = 22; scale = 5; speed = 3;}
                }
            }
        }
    }



    public void updateAI(Collection<Player> players, Map<Long, Enemy> allEnemies, CollisionCheckerServer checker) {
        Player target = getClosestPlayer(players);
        if (target == null) return;


        int dx = Integer.compare(target.getGlobalX(), this.getGlobalX());
        int dy = Integer.compare(target.getGlobalY(), this.getGlobalY());


        List<Enemy> otherEnemies = allEnemies.values()
                .stream()
                .filter( e -> e != this)
                .toList();

        // try move X
        if (dx != 0) {
            this.setDirection(dx > 0 ? FramePosition.RIGHT : FramePosition.LEFT);
            tryMove(dx * speed, 0, otherEnemies, checker);

        }


        if (dy != 0) {
            this.setDirection(dy > 0 ? FramePosition.DOWN : FramePosition.UP);
             tryMove(0, dy * speed, otherEnemies, checker);
        }
    }


    private void tryMove(int mx, int my, Collection<Enemy> otherEnemies, CollisionCheckerServer checker) {
        // store original position
        int origX = this.getGlobalX();
        int origY = this.getGlobalY();

        this.setCollisionOn(false);

        this.moveBy(mx, my);

        checker.checkEntity(this, otherEnemies);
        checker.checkTile(this);

        if (this.isCollisionOn()) {

            this.setGlobalX(origX);
            this.setGlobalY(origY);
            this.setCollisionOn(false);
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

}
