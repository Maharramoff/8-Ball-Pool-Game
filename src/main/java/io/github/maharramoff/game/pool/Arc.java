package io.github.maharramoff.game.pool;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Arc
{
    public static final Color COLOR = new Color(239, 193, 82);

    public static final Map<HolePosition, int[]> ARCS = new HashMap<>();

    static {
        Arc.ARCS.put(HolePosition.TOP_LEFT, new int[]{20, 240});
        Arc.ARCS.put(HolePosition.TOP_CENTER, new int[]{7, 172});
        Arc.ARCS.put(HolePosition.TOP_RIGHT, new int[]{-70, 230});
        Arc.ARCS.put(HolePosition.BOTTOM_LEFT, new int[]{110, 230});
        Arc.ARCS.put(HolePosition.BOTTOM_CENTER, new int[]{187, 172});
        Arc.ARCS.put(HolePosition.BOTTOM_RIGHT, new int[]{200, 240});
    };
}