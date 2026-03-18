package config;

public class GameConfiguration {
    // i started using pixels

    public static int WINDOW_WIDTH  = 1920;
    public static int WINDOW_HEIGHT = 1080;

    // tiles
    public static final int TILE_ROWS = 60;
    public static final int TILE_COLS = 60;
    public static final int TILE_SIZE    = 128;
    public static final int WORLD_WIDTH  = TILE_SIZE * TILE_ROWS;  // 60 * 128 = 7680
    public static final int WORLD_HEIGHT = TILE_SIZE * TILE_COLS;  // 60 * 128 = 7680

    // Camera
    public static final int MINIMAP_WIDTH  = 300;
    public static final int MINIMAP_HEIGHT = 300;
    public static final int MINIMAP_MARGIN = 10;

    // Player
    public static final double PLAYER_START_X    = TILE_SIZE * 12;
    public static final double PLAYER_START_Y    = TILE_SIZE * 50;
    public static final double PLAYER_SPEED      = 400.0; // pixels/sec
    public static final double PLAYER_MAX_HP     = 100;
    public static final double PLAYER_MAX_MANA   = 100;
    public static final double PLAYER_START_HP   = 20;
    public static final double PLAYER_START_MANA = 20;
    public static final double PLAYER_MANA_REGEN = 2.0;

    // Bot
    public static final double BOT_SPEED    = 300.0;//pixels/sec
    public static final double BOT_RANGE    = 200.0;  // pixels
    public static final double BOT_DAMAGE    = 5.0;
    public static final double BOT_MAX_HP   = 100;
    public static final double BOT_MAX_MANA = 100;

    // Tower
    public static final double TOWER_RANGE            = 200.0; // pixels
    public static final double TOWER_DAMAGE  = 10.0;
    public static final double TOWER_COLLISION_RADIUS = 32.0;  // pixels
    public static final double TOWER_MAX_HP           = 100;

    //Base
    public static final double BASE_RANGE = 384.0;
    public static final double BASE_MAX_HP = 500;
    public static final double BASE_DAMAGE = 0.1;

    // Fountain
    public static final double FOUNTAIN_RADIUS = 350.0;
    public static final double FOUNTAIN_HEAL_PER_SEC = 20.0;
    public static final double FOUNTAIN_DAMAGE = 5.0;

    // Game loop
    public static final int GAME_SPEED = 10;

    // Minion
    public static final double MINION_DMG    = 3.0;
    public static final double MINION_RANGE = 250; // pixels
    public static final double MINION_MAX_HP = 60;
    public static final double MINION_ATTACK_COOLDOWN = 0.9;
    public static final double MINION_SPEED = 300.0; // pixels/sec
}
