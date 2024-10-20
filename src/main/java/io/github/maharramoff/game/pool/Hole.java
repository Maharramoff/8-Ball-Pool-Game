package io.github.maharramoff.game.pool;

import java.util.HashMap;
import java.util.Map;

public class Hole
{
    private Hole() {}

    public static final int HOLE_RADIUS = 14;
    public static final int HOLE_MARGIN = 1;
    public static final Map<HolePosition, int[]> HOLES = new HashMap<>();

    static {
        HOLES.put(HolePosition.TOP_LEFT, new int[]{GameSettings.SCREEN_MARGIN + Table.RAIL_WIDTH / 2, GameSettings.SCREEN_MARGIN + Table.RAIL_WIDTH / 2});
        HOLES.put(HolePosition.TOP_CENTER, new int[]{GameSettings.SCREEN_MARGIN + Table.WIDTH / 2, GameSettings.SCREEN_MARGIN + HOLE_MARGIN});
        HOLES.put(HolePosition.TOP_RIGHT, new int[]{GameSettings.SCREEN_MARGIN + Table.WIDTH - (Table.RAIL_WIDTH / 2 + (HOLE_RADIUS * 2)), GameSettings.SCREEN_MARGIN + Table.RAIL_WIDTH / 2});
        HOLES.put(HolePosition.BOTTOM_LEFT, new int[]{GameSettings.SCREEN_MARGIN + Table.RAIL_WIDTH / 2, GameSettings.SCREEN_MARGIN + Table.HEIGHT - (Table.RAIL_WIDTH / 2 + (HOLE_RADIUS * 2))});
        HOLES.put(HolePosition.BOTTOM_CENTER, new int[]{GameSettings.SCREEN_MARGIN + Table.WIDTH / 2, GameSettings.SCREEN_MARGIN + Table.HEIGHT - (HOLE_MARGIN + (HOLE_RADIUS * 2))});
        HOLES.put(HolePosition.BOTTOM_RIGHT, new int[]{GameSettings.SCREEN_MARGIN + Table.WIDTH - (Table.RAIL_WIDTH / 2 + (HOLE_RADIUS * 2)), GameSettings.SCREEN_MARGIN + Table.HEIGHT - (Table.RAIL_WIDTH / 2 + (HOLE_RADIUS * 2))});
    }
}