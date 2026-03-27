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

    public static Entity findClosestActive(Entity source, List<Entity> entities) {
        return findClosest(source, entities);
    }
}
