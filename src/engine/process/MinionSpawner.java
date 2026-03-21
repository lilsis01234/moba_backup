package engine.process;

import engine.mobile.Minion;
import engine.mobile.Player;
import game_config.GameConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MinionSpawner {

    private static final double SPAWN_INTERVAL = 14.0;
    private static final int MINIONS_PER_WAVE = 3;

    private double timer = 0;
    private List<Minion> minions = new ArrayList<>();

    private static List<double[]> getWaypoints(int lane, int team) {
        int T = GameConfiguration.TILE_SIZE;
        List<double[]> wp = new ArrayList<>();
        if (team == 0) {
            switch (lane) {
                case 0: // top lane ally
                    wp.add(new double[]{10 * T, 48 * T});
                    wp.add(new double[]{10 * T, 35 * T});
                    wp.add(new double[]{10 * T, 20 * T});
                    wp.add(new double[]{10 * T, 11 * T});
                    wp.add(new double[]{25 * T, 10 * T});
                    wp.add(new double[]{48 * T, 10 * T});
                    break;
                case 1: // mid lane ally
                    wp.add(new double[]{13 * T, 48 * T});
                    wp.add(new double[]{15 * T, 45 * T});
                    wp.add(new double[]{21 * T, 39 * T});
                    wp.add(new double[]{27 * T, 33 * T});
                    wp.add(new double[]{32 * T, 28 * T});
                    wp.add(new double[]{38 * T, 22 * T});
                    wp.add(new double[]{45 * T, 15 * T});
                    wp.add(new double[]{48 * T, 12 * T});
                    break;
                case 2: // bot lane ally
                    wp.add(new double[]{15 * T, 50 * T});
                    wp.add(new double[]{30 * T, 50 * T});
                    wp.add(new double[]{45 * T, 50 * T});
                    wp.add(new double[]{50 * T, 45 * T});
                    wp.add(new double[]{50 * T, 12 * T});
                    break;
            }
        } else {
            switch (lane) {
                case 0: // top lane enemy
                    wp.add(new double[]{48 * T, 10 * T});
                    wp.add(new double[]{25 * T, 10 * T});
                    wp.add(new double[]{11 * T, 10 * T});
                    wp.add(new double[]{10 * T, 25 * T});
                    wp.add(new double[]{10 * T, 48 * T});
                    break;
                case 1: // mid lane enemy
                    wp.add(new double[]{48 * T, 12 * T});
                    wp.add(new double[]{45 * T, 15 * T});
                    wp.add(new double[]{38 * T, 22 * T});
                    wp.add(new double[]{32 * T, 28 * T});
                    wp.add(new double[]{27 * T, 33 * T});
                    wp.add(new double[]{21 * T, 39 * T});
                    wp.add(new double[]{15 * T, 45 * T});
                    wp.add(new double[]{13 * T, 48 * T});
                    break;
                case 2: // bot lane enemy
                    wp.add(new double[]{50 * T, 12 * T});
                    wp.add(new double[]{50 * T, 30 * T});
                    wp.add(new double[]{50 * T, 48 * T});
                    wp.add(new double[]{45 * T, 50 * T});
                    wp.add(new double[]{15 * T, 50 * T});
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

    public List<Minion> getMinions() { return minions; }
}
