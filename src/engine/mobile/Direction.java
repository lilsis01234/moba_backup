package engine.mobile;

/**
 * Directions cardinales du jeu.
 * @author RAHARIMANANA Tianantenaina
 */
public enum Direction {
    UP("up"),
    DOWN("down"),
    LEFT("left"),
    RIGHT("right");
    
    private final String name;
    
    Direction(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public static Direction fromString(String str) {
        for (Direction dir : values()) {
            if (dir.name.equals(str)) {
                return dir;
            }
        }
        return DOWN;
    }
    
    public static Direction fromDelta(int dx, int dy, Direction current) {
        int absX = Math.abs(dx);
        int absY = Math.abs(dy);
        
        // mouvement small dont change direction
        if (absX < 2 && absY < 2) return current;

       
        double threshold = 1.2; 
        //to prevent flickering
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
