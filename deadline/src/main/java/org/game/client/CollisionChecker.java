package org.game.client;

import org.game.entity.Entity;
import org.game.tiles.Tile;
import org.game.tiles.TileManager;
import org.game.server.WorldSettings;

import java.util.List;

public final class CollisionChecker {

    private final GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Entity e) {
        int entityLeftWorldX = e.getGlobalX() + e.getHitbox().x;
        int entityRightWorldX = e.getGlobalX() + e.getHitbox().x + e.getHitbox().width;

        int entityTopWorldY = e.getGlobalY() + e.getHitbox().y;
        int entityBottomWorldY = e.getGlobalY() + e.getHitbox().y + e.getHitbox().height;

        int tileSize = WorldSettings.TILE_SIZE;

        int entityLeftCol = entityLeftWorldX / tileSize;
        int entityRightCol = entityRightWorldX / tileSize;
        int entityTopRow =  entityTopWorldY / tileSize;
        int entityBottomRow = entityBottomWorldY / tileSize;


        switch (e.getDirection()) {
            case UP ->  {
                entityTopRow = (entityTopWorldY - e.getSpeed()) / tileSize;
                checkCollision(e, entityTopRow, entityLeftCol, entityTopRow, entityRightCol);
            }
            case LEFT -> {
                entityLeftCol = (entityLeftWorldX - e.getSpeed()) / tileSize;
                checkCollision(e, entityTopRow, entityLeftCol, entityBottomRow, entityLeftCol);
            }
            case DOWN ->  {
                entityBottomRow = (entityBottomWorldY + e.getSpeed()) / tileSize;
                checkCollision(e, entityBottomRow, entityLeftCol, entityBottomRow, entityLeftCol);
            }
            case RIGHT -> {
                entityRightCol = (entityRightWorldX + e.getSpeed()) / tileSize;
                checkCollision(e, entityTopRow, entityRightCol, entityBottomRow, entityRightCol);
            }
        }
    }
    private void checkCollision(Entity e, int row1, int col1, int row2, int col2) {
        final TileManager tileManager = gp.getTileManager();
        final List<Tile> tiles = tileManager.getTiles();

        int tileNum1, tileNum2;
        tileNum1 =  tileManager.mapTileNum[row1][col1];
        tileNum2 = tileManager.mapTileNum[row2][col2];

        if (tiles.get(tileNum1).hasCollision() || tiles.get(tileNum2).hasCollision()) {
            e.setCollisionOn(true);
        }
    }
    }



