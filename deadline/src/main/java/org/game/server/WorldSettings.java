package org.game.server;

public final class WorldSettings {


    // screen settings
    public static final int ORIGINAL_TILE_SIZE = 16; // 16x16 tile
    public static final int SCALE = 4;

    public static final int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE; // 48x48 tile

    // WORLD SETTINGS
    public static final int MAX_WORLD_COL = 50;
    public static final int MAX_WORLD_ROW = 50;
    public static final int WORLD_WIDTH = TILE_SIZE * MAX_WORLD_COL;
    public static final int WORLD_HEIGHT = TILE_SIZE * MAX_WORLD_ROW;

    public static final int CENTER_X = TILE_SIZE * 23;
    public static final int CENTER_Y = TILE_SIZE * 21;
}
