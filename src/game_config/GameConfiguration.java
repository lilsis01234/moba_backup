package game_config;

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

    
    //Gold reward    
    public static final int GOLD_MINION = 20;
    public static final int GOLD_CHAR    = 100;
    public static final int GOLD_TOWER  = 150;
    public static final int STARTING_GOLD = 100;
    public static final int PASSIVE_GOLD_PER_SECOND = 10;
    
    //XP reward
    public static final int XP_MINION = 15;
    public static final int XP_CHAR    = 80;
    public static final int XP_TOWER  = 100;
    
	 // Level up stats Bonus
    public static final double LEVEL_HP_BONUS   = 20.0;
    public static final double LEVEL_MANA_BONUS = 15.0;
    public static final double LEVEL_DMG_BONUS  = 5.0;
    
    
    //technical
    public static final double ATTACK_MARGIN = TILE_SIZE * 0.75;
    public static final double RECALL_DURATION = 5.0;
    
    //retreat
    public static final double RETREAT_HP_THRESHOLD = 0.30;
    public static final double RETREAT_SAFE_RADIUS = TILE_SIZE * 6;

    // Camera
    public static final int MINIMAP_WIDTH  = 300;
    public static final int MINIMAP_HEIGHT = 300;
    public static final int MINIMAP_MARGIN = 10;

    // Player
    public static final double START_X    = TILE_SIZE * 4;
    public static final double START_Y    = TILE_SIZE * 56;
    public static final double PLAYER_MANA_REGEN = 2.0;
    
    

    
    

    // Tower
    public static final double TOWER_RANGE = 200.0; 
    public static final double TOWER_DAMAGE  = 10.0;
    public static final double TOWER_COLLISION_RADIUS = 32.0;  
    public static final double TOWER_MAX_HP  = 1500;

    //Base
    public static final double BASE_RANGE = 420.0;
    public static final double BASE_MAX_HP = 2000;
    public static final double BASE_DAMAGE = 100;

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

    // Bot
    public static final double WAYPOINT_CLOSE_THRESHOLD = 5.0;
    public static final double WAYPOINT_REACHED_THRESHOLD = 8.0;
    public static final double BOT_COLLISION_THRESHOLD = 0.6;

    // Game
    public static final double MAX_DELTA_TIME = 0.05;
    public static final int ASSIST_TIME_WINDOW = 5000;
    public static final int RESPAWN_BASE_TIME = 5;
}
