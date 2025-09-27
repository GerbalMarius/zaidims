package org.game.client.tiles;

import lombok.Getter;
import org.game.server.WorldSettings;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.game.utils.ByteFiles.loadImage;

// int[y][x] , y - row , x - col
@Getter
public class TileManager {
    private List<Tile> tiles;
    public int[][] mapTileNum;

    public TileManager() {
        mapTileNum = new int[WorldSettings.MAX_WORLD_ROW][WorldSettings.MAX_WORLD_COL];

        loadAllTiles("res/tiles");
        loadMap("res/maps/world01.txt");
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

    public void draw(Graphics2D g2d) {
        int worldCol = 0;
        int worldRow = 0;

        while (worldCol < WorldSettings.MAX_WORLD_COL && worldRow < WorldSettings.MAX_WORLD_ROW) {

            int tileNum = mapTileNum[worldRow][worldCol];

            int worldX = worldCol * WorldSettings.TILE_SIZE;
            int worldY = worldRow * WorldSettings.TILE_SIZE;

            g2d.drawImage(tiles.get(tileNum).image(), worldX, worldY, WorldSettings.TILE_SIZE, WorldSettings.TILE_SIZE, null);
            worldCol++;

            if (worldCol == WorldSettings.MAX_WORLD_COL) {
                worldCol = 0;
                worldRow++;
            }
        }
    }

}
