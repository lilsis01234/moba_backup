package engine.map;

import game_config.GameConfiguration;
import log.LoggerUtility;
import org.apache.log4j.Logger;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TilesManager {

    private static final Logger logger = LoggerUtility.getLogger(TilesManager.class);
    private static TilesManager instance;
    public Tile[] tiles;
    public int[][] mapTileNum;

    private static final int MAP_COLS = GameConfiguration.TILE_COLS;
    private static final int MAP_ROWS = GameConfiguration.TILE_ROWS;

    private TilesManager() {
        tiles = new Tile[10];
        mapTileNum = new int[MAP_ROWS][MAP_COLS];
        getTileImage();
        loadMap("/game_config/map/map.txt");
    }

    public static TilesManager getInstance() {
        if (instance == null) {
            instance = new TilesManager();
        }
        return instance;
    }

    public static void reset() {
        instance = null;
    }

    private void getTileImage() {
        try {
            logger.info("Chargement des textures des tuiles");
            // Tile 0: Grass
            tiles[0] = new Tile();
            tiles[0].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/grass.png"));
            tiles[0].collision = false;

            // Tile 1: Floor
            tiles[1] = new Tile();
            tiles[1].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/sand.png"));
            tiles[1].collision = false;

            // Tile 2: Wall aka border
            tiles[2] = new Tile();
            tiles[2].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/wall.png"));
            tiles[2].collision = true;

            // Tile 3: bordered grass
            tiles[3] = new Tile();
            tiles[3].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/grassB.png"));
            tiles[3].collision = true;
            logger.info("Textures chargées avec succès.");

        } catch (Exception e) {
            logger.error("Erreur lors du chargement des images de tuiles : " + e.getMessage());
        }
    }

    private void loadMap(String mapPath) {
        try {
            BufferedReader br = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream(mapPath))
            );
            for (int row = 0; row < MAP_ROWS; row++) {
                String line = br.readLine();
                if (line == null) break;
                String[] numbers = line.trim().split("\\s+");
                for (int col = 0; col < MAP_COLS && col < numbers.length; col++) {
                    mapTileNum[row][col] = Integer.parseInt(numbers[col]);
                }
            }
            br.close();
        } catch (Exception e) {
            logger.fatal("Impossible de charger le fichier de map : " + mapPath, e);
        }
    }

    public void render(Graphics2D g2, int tileSize) {
        for (int row = 0; row < MAP_ROWS; row++) {
            for (int col = 0; col < MAP_COLS; col++) {
                int worldX = col * tileSize;
                int worldY = row * tileSize;

                int tileNum = mapTileNum[row][col];
                if (tiles[tileNum] != null && tiles[tileNum].image != null) {
                    g2.drawImage(tiles[tileNum].image, worldX, worldY, tileSize, tileSize, null);
                }
            }
        }
    }

    public boolean isSolidTile(double worldX, double worldY, int tileSize) {
        int col = (int)(worldX / tileSize);
        int row = (int)(worldY / tileSize);

        if (row < 0 || row >= MAP_ROWS || col < 0 || col >= MAP_COLS) return true;

        int tileNum = mapTileNum[row][col];
        return tiles[tileNum] != null && tiles[tileNum].collision;
    }
}
