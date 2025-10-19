package org.game.tiles;

import lombok.Getter;
import org.game.client.Camera;
import org.game.server.WorldSettings;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

import static org.game.utils.ByteFiles.loadImage;

@Getter
public class TileManager {
    private List<Tile> tiles;
    public int[][] mapTileNum;

    public TileManager() {
        mapTileNum = new int[WorldSettings.MAX_WORLD_ROW][WorldSettings.MAX_WORLD_COL];

        loadAllTiles("assets/tiles");
        loadMap("assets/maps/world02.txt");
    }

    public void loadAllTiles(String root) {
        this.tiles = List.of(
                new Tile(loadImage(root +"/grass.png"), false),
                new Tile(loadImage(root+"/wall.png"), true),
                new Tile(loadImage(root+"/water.png"), true),
                new Tile(loadImage(root+"/earth.png"), false),
                new Tile(loadImage(root+"/tree.png"), true),
                new Tile(loadImage(root+"/sand.png"), false)
        );
    }


    public void loadMap(String filePath) {
        try (var reader = Files.newBufferedReader(Paths.get(filePath))) {
            int row = 0;
            int col = 0;
            int maxRows = WorldSettings.MAX_WORLD_ROW;
            int maxCols = WorldSettings.MAX_WORLD_COL;

            for (int ch; (ch = reader.read()) != -1 && row < maxRows; ) {
                char c = (char) ch;
                if (!Character.isDigit(c)) {
                    continue;
                }
                int num = c - '0';
                mapTileNum[row][col] = num;

                col++;
                if (col >= maxCols) {
                    col = 0;
                    row++;
                }
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public void draw(Graphics2D g2d, Camera camera, int viewportW, int viewportH) {
        final int tileSize = WorldSettings.TILE_SIZE;


        double viewLeft = camera.getX() - viewportW / 2.0;
        double viewTop =  camera.getY() - viewportH / 2.0;
        double viewRight = camera.getX() + viewportW / 2.0;
        double viewBottom = camera.getY() + viewportH / 2.0;


        int startCol = (int) Math.floor(viewLeft / tileSize);
        int endCol   = (int) Math.floor(viewRight / tileSize);
        int startRow = (int) Math.floor(viewTop / tileSize);
        int endRow   = (int) Math.floor(viewBottom / tileSize);


        startCol = Math.max(0, startCol - 1);
        startRow = Math.max(0, startRow - 1);
        endCol   = Math.min(WorldSettings.MAX_WORLD_COL - 1, endCol + 1);
        endRow   = Math.min(WorldSettings.MAX_WORLD_ROW - 1, endRow + 1);


        if (startCol > endCol || startRow > endRow) {
            return;
        }

        // draw only the visible tiles
        for (int row = startRow; row <= endRow; row++) {
            int worldY = row * tileSize;
            for (int col = startCol; col <= endCol; col++) {
                int tileNum = mapTileNum[row][col];
                int worldX = col * tileSize;

                g2d.drawImage(tiles.get(tileNum).image(), worldX, worldY, tileSize, tileSize, null);
            }
        }
    }

    public int[] findRandomSpawnPosition(Random rand, int maxGenAttempts) {
        int rows = mapTileNum.length;
        int cols = mapTileNum[0].length;
        int tileSize = WorldSettings.TILE_SIZE;

        for (int i = 0; i < maxGenAttempts; i++) {
            int row = rand.nextInt(rows);
            int col = rand.nextInt(cols);

            if (!isWalkable(row, col)) {
                continue;
            }

            int x = col * tileSize + tileSize / 2;
            int y = row * tileSize + tileSize / 2;
            return new int[]{x, y};
        }
        return new int[]{WorldSettings.CENTER_X, WorldSettings.CENTER_Y};
    }

    private boolean isWalkable(int y, int x) {
        return !tiles.get(mapTileNum[y][x]).hasCollision();
    }

}
