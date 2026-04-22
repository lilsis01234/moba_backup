package engine.mobile;

public enum Direction {
    UP, DOWN, LEFT, RIGHT;

    public static Direction fromString(String str) {
        for (Direction dir : values()) {
            if (dir.name().equalsIgnoreCase(str)) {
                return dir;
            }
        }
        return DOWN;
    }

    public static Direction fromDelta(int dx, int dy, Direction current) {
        int absX = Math.abs(dx);
        int absY = Math.abs(dy);

        if (absX < 2 && absY < 2) return current;

        double threshold = 1.2;
        if (current == LEFT || current == RIGHT) {
            if (absY > absX * threshold) {
                return dy > 0 ? DOWN : UP;
            }
            return dx > 0 ? RIGHT : LEFT;
        } else {
            if (absX > absY * threshold) {
                return dx > 0 ? RIGHT : LEFT;
            }
            return dy > 0 ? DOWN : UP;
        }
    }
}