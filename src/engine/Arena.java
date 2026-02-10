package engine;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Arena {
    private List<Lane> lanes;

    public Arena() {
        lanes = new ArrayList<>();
        lanes.add(new Lane(Lane.Type.top));
        lanes.add(new Lane(Lane.Type.middle));
        lanes.add(new Lane(Lane.Type.bot));
    }

    public void render(Graphics2D g2, int width, int height) {
        g2.setColor(new Color(20, 80, 20));
        g2.fillRect(0, 0, width, height);

        int lane_width = width / 12; 

        for (Lane lane : lanes) {
            lane.render(g2, width, height, lane_width);
        }
    }
}