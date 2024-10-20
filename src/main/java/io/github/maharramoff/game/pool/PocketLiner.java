package io.github.maharramoff.game.pool;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PocketLiner
{
    public static final Color COLOR = new Color(239, 193, 82);

    public static final Map<PocketPosition, int[]> POSITION_MAP = new HashMap<>();

    static {
        PocketLiner.POSITION_MAP.put(PocketPosition.TOP_LEFT, new int[]{20, 240});
        PocketLiner.POSITION_MAP.put(PocketPosition.TOP_CENTER, new int[]{7, 172});
        PocketLiner.POSITION_MAP.put(PocketPosition.TOP_RIGHT, new int[]{-70, 230});
        PocketLiner.POSITION_MAP.put(PocketPosition.BOTTOM_LEFT, new int[]{110, 230});
        PocketLiner.POSITION_MAP.put(PocketPosition.BOTTOM_CENTER, new int[]{187, 172});
        PocketLiner.POSITION_MAP.put(PocketPosition.BOTTOM_RIGHT, new int[]{200, 240});
    };
}