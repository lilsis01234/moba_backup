package engine.process;

import engine.mobile.Minion;
import engine.mobile.Player;
import game_config.GameConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MinionSpawner {

    private static final double SPAWN_INTERVAL = 14.0;
    private static final int MINIONS_PER_WAVE = 3;
    private static MinionSpawner instance;

    private double timer = 0;
    private List<Minion> minions = new ArrayList<>();

    private MinionSpawner() {}

    public static MinionSpawner getInstance() {
        if (instance == null) {
            instance = new MinionSpawner();
        }
        return instance;
    }

    public static void reset() {
        instance = null;
    }

    private static List<double[]> getWaypoints(int lane, int team) {
        int T = GameConfiguration.TILE_SIZE;
        List<double[]> wp = new ArrayList<>();
        if (team == 0) {
            switch (lane) {
            case 0: // top ally
                wp.add(new double[]{6 * T, 52 * T});
                wp.add(new double[]{6 * T, 39 * T});
                wp.add(new double[]{6 * T, 24 * T});
                wp.add(new double[]{6 * T, 7 * T});
                wp.add(new double[]{21 * T, 6 * T});
                wp.add(new double[]{52 * T, 6 * T});
                break;
            case 1: // mid ally
                wp.add(new double[]{9 * T, 52 * T});
                wp.add(new double[]{11 * T, 49 * T});
                wp.add(new double[]{17 * T, 43 * T});
                wp.add(new double[]{23 * T, 37 * T});
                wp.add(new double[]{28 * T, 32 * T});
                wp.add(new double[]{34 * T, 26 * T});
                wp.add(new double[]{41 * T, 19 * T});
                wp.add(new double[]{52 * T, 8 * T});
                break;
            case 2: // bot ally
                wp.add(new double[]{8 * T, 54 * T});
                wp.add(new double[]{26* T, 54 * T});
                wp.add(new double[]{52 * T, 54 * T});
                wp.add(new double[]{54 * T, 41 * T});
                wp.add(new double[]{54 * T, 9 * T});
                break;

            }
        } else {
            switch (lane) {
            case 0: // top enemy
                wp.add(new double[]{52 * T, 6 * T});
                wp.add(new double[]{29 * T, 6 * T});
                wp.add(new double[]{7 * T, 6 * T});
                wp.add(new double[]{6 * T, 21 * T});
                wp.add(new double[]{6 * T, 52 * T});
                break;
            case 1: // mid enemy
                wp.add(new double[]{52 * T, 8 * T});
                wp.add(new double[]{41 * T, 19 * T});
                wp.add(new double[]{34 * T, 26 * T});
                wp.add(new double[]{28 * T, 32 * T});
                wp.add(new double[]{23 * T, 37 * T});
                wp.add(new double[]{17 * T, 43 * T});
                wp.add(new double[]{11 * T, 49 * T});
                wp.add(new double[]{9 * T, 52 * T});
                break;
            case 2: // bot enemy
                wp.add(new double[]{54 * T, 8 * T});
                wp.add(new double[]{54 * T, 26 * T});
                wp.add(new double[]{54 * T, 52 * T});
                wp.add(new double[]{41 * T, 54 * T});
                wp.add(new double[]{9 * T, 54 * T});
                break;
            }
        }
        return wp;
    }

    public void update(double deltaTime, Player player) {
        timer += deltaTime;

        if (timer >= SPAWN_INTERVAL) {
            timer = 0;
            spawnWave();
        }
        // removes dead minions
        minions.removeIf(m -> !m.isActive());
    }

    private void spawnWave() {
        for (int lane = 0; lane < 3; lane++) {
            for (int i = 0; i < MINIONS_PER_WAVE; i++) {
                double offset = i * GameConfiguration.TILE_SIZE;

                // ally spawn — bottom left
                List<double[]> wpAlly = getWaypoints(lane, 0);
                double[] startA = wpAlly.get(0);
                minions.add(new Minion(startA[0] + offset, startA[1], 0, wpAlly));

                // enemy spawn — top right
                List<double[]> wpEnemy = getWaypoints(lane, 1);
                double[] startE = wpEnemy.get(0);
                minions.add(new Minion(startE[0], startE[1] + offset, 1, wpEnemy));
            }
        }
    }

    public List<Minion> getMinions() { return new ArrayList<>(minions); }
}
