package org.game.server;

import org.game.entity.Enemy;
import org.game.entity.Entity;
import org.game.tiles.Tile;
import org.game.tiles.TileManager;

import java.awt.*;
import java.util.Collection;
import java.util.List;

public final class CollisionChecker {

    private final TileManager tileManager;

    public CollisionChecker(TileManager tileManager) {
        this.tileManager = tileManager;
    }

    public void checkTile(Entity e) {
        int entityLeftWorldX = e.getGlobalX() + e.getHitbox().x;
        int entityRightWorldX = e.getGlobalX() + e.getHitbox().x + e.getHitbox().width;

        int entityTopWorldY = e.getGlobalY() + e.getHitbox().y;
        int entityBottomWorldY = e.getGlobalY() + e.getHitbox().y + e.getHitbox().height;

        int tileSize = WorldSettings.TILE_SIZE;

        int entityLeftCol = entityLeftWorldX / tileSize;
        int entityRightCol = entityRightWorldX / tileSize;
        int entityTopRow = entityTopWorldY / tileSize;
        int entityBottomRow = entityBottomWorldY / tileSize;

        switch (e.getDirection()) {
            case UP -> {
                entityTopRow = (entityTopWorldY - e.getSpeed()) / tileSize;
                checkCollision(e, entityTopRow, entityLeftCol, entityTopRow, entityRightCol);
            }
            case LEFT -> {
                entityLeftCol = (entityLeftWorldX - e.getSpeed()) / tileSize;
                checkCollision(e, entityTopRow, entityLeftCol, entityBottomRow, entityLeftCol);
            }
            case DOWN -> {
                entityBottomRow = (entityBottomWorldY + e.getSpeed()) / tileSize;
                checkCollision(e, entityBottomRow, entityLeftCol, entityBottomRow, entityRightCol);
            }
            case RIGHT -> {
                entityRightCol = (entityRightWorldX + e.getSpeed()) / tileSize;
                checkCollision(e, entityTopRow, entityRightCol, entityBottomRow, entityRightCol);
            }
        }
    }

    private void checkCollision(Entity e, int row1, int col1, int row2, int col2) {
        final List<Tile> tiles = tileManager.getTiles();

        int tileNum1 = tileManager.mapTileNum[row1][col1];
        int tileNum2 = tileManager.mapTileNum[row2][col2];

        e.setCollisionOn(tiles.get(tileNum1).hasCollision() || tiles.get(tileNum2).hasCollision());
    }

    public void checkEntityCollision(Entity e, Collection<? extends Entity> others) {
        Rectangle eHitbox = new Rectangle(
                e.getGlobalX() + e.getHitbox().x,
                e.getGlobalY() + e.getHitbox().y,
                e.getHitbox().width,
                e.getHitbox().height
        );

        boolean collided = others.stream()
                .filter(o -> o != e)
                .anyMatch(o -> eHitbox.intersects(
                        new Rectangle(
                                o.getGlobalX() + o.getHitbox().x,
                                o.getGlobalY() + o.getHitbox().y,
                                o.getHitbox().width,
                                o.getHitbox().height
                        )
                ));

        if (collided) {
            e.setCollisionOn(true);
        }

    }


    public TileManager getTileManager() {
        return tileManager;
    }
}

