package io.github.maharramoff.game.pool;

import java.util.HashMap;
import java.util.Map;

public class Pocket
{
    private Pocket() {}

    public static final int RADIUS = 14;
    public static final int MARGIN = 1;
    public static final Map<PocketPosition, int[]> POSITION_MAP = new HashMap<>();

    static {
        POSITION_MAP.put(PocketPosition.TOP_LEFT, new int[]{GameSettings.SCREEN_MARGIN + Table.RAIL_WIDTH / 2, GameSettings.SCREEN_MARGIN + Table.RAIL_WIDTH / 2});
        POSITION_MAP.put(PocketPosition.TOP_CENTER, new int[]{GameSettings.SCREEN_MARGIN + Table.WIDTH / 2, GameSettings.SCREEN_MARGIN + MARGIN});
        POSITION_MAP.put(PocketPosition.TOP_RIGHT, new int[]{GameSettings.SCREEN_MARGIN + Table.WIDTH - (Table.RAIL_WIDTH / 2 + (RADIUS * 2)), GameSettings.SCREEN_MARGIN + Table.RAIL_WIDTH / 2});
        POSITION_MAP.put(PocketPosition.BOTTOM_LEFT, new int[]{GameSettings.SCREEN_MARGIN + Table.RAIL_WIDTH / 2, GameSettings.SCREEN_MARGIN + Table.HEIGHT - (Table.RAIL_WIDTH / 2 + (RADIUS * 2))});
        POSITION_MAP.put(PocketPosition.BOTTOM_CENTER, new int[]{GameSettings.SCREEN_MARGIN + Table.WIDTH / 2, GameSettings.SCREEN_MARGIN + Table.HEIGHT - (MARGIN + (RADIUS * 2))});
        POSITION_MAP.put(PocketPosition.BOTTOM_RIGHT, new int[]{GameSettings.SCREEN_MARGIN + Table.WIDTH - (Table.RAIL_WIDTH / 2 + (RADIUS * 2)), GameSettings.SCREEN_MARGIN + Table.HEIGHT - (Table.RAIL_WIDTH / 2 + (RADIUS * 2))});
    }
}