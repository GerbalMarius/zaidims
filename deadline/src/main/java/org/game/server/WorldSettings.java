package org.game.server;

public final class WorldSettings {


    // screen settings
    public static final int originalTileSize = 16; // 16x16 tile
    public static final int scale = 4;

    public static final int tileSize = originalTileSize * scale; // 48x48 tile
    public static final int maxScreenCol = 16;
    public static final int maxScreenRow = 12;
    public static final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    public static final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    // WORLD SETTINGS
    public static final int maxWorldCol = 50;
    public static final int maxWorldRow = 50;
    public static final int worldWidth = tileSize * maxWorldCol;
    public static final int worldHeight = tileSize * maxWorldRow;
}
