package engine.mobile;

import java.util.List;

public class EntityUtils {

    public static Entity findClosest(Entity source, List<Entity> entities) {
        Entity closest = null;
        double closestDist = Double.MAX_VALUE;
        for (Entity e : entities) {
            if (!e.isActive()) continue;
            double d = source.getDistanceTo(e);
            if (d < closestDist) {
                closestDist = d;
                closest = e;
            }
        }
        return closest;
    }

    public static <T extends Entity> T findClosestOfType(Entity source, List<T> entities, Class<T> type) {
        T closest = null;
        double closestDist = Double.MAX_VALUE;
        for (T e : entities) {
            if (!e.isActive()) continue;
            if (!type.isInstance(e)) continue;
            double d = source.getDistanceTo(e);
            if (d < closestDist) {
                closestDist = d;
                closest = type.cast(e);
            }
        }
        return closest;
    }

    public static Entity findClosestActive(Entity source, List<Entity> entities) {
        return findClosest(source, entities);
    }

    public static double getDistance(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double getDistance(Entity a, Entity b) {
        return getDistance(a.getX(), a.getY(), b.getX(), b.getY());
    }

    public static Entity findClosestNotOnTeam(Entity source, List<Entity> entities, int team) {
        Entity closest = null;
        double closestDist = Double.MAX_VALUE;
        for (Entity e : entities) {
            if (!e.isActive()) continue;
            if (e.getTeam() == team) continue;
            double d = source.getDistanceTo(e);
            if (d < closestDist) {
                closestDist = d;
                closest = e;
            }
        }
        return closest;
    }
}
