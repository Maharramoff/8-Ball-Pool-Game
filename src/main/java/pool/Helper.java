package pool;

import java.awt.*;
import java.util.HashMap;

public final class Helper
{
    static final int SW = 640, SH = 358; // Screen width and height
    static final int TB = 20; // Table border width
    static final int HR = 14; // Hole radius
    static final int TW = SW - (TB * 2), TH = SH - (TB * 2); // Table width and height

    private static final int HM = 6; // Hole margin from border

    static final Color BC         = new Color(136, 49, 23); // Border color
    static final Color TC         = new Color(60, 180, 7); // Table color
    static final Color BALL_WHITE = new Color(255, 247, 200); // Milky white
    static final Color HAC        = new Color(239, 193, 82); // Hole arc color


    static final HashMap<String, int[]> HOLES = new HashMap<String, int[]>()
    {{
        put("TL", new int[]{TB / 2, TB / 2});
        put("TC", new int[]{SW / 2, HM});
        put("TR", new int[]{SW - (TB / 2 + (HR * 2)), TB / 2});
        put("BL", new int[]{TB / 2, SH - (TB / 2 + (HR * 2))});
        put("BC", new int[]{SW / 2, SH - (HM + (HR * 2))});
        put("BR", new int[]{SW - (TB / 2 + (HR * 2)), SH - (TB / 2 + (HR * 2))});
    }};

    static final HashMap<String, int[]> ARCS = new HashMap<String, int[]>()
    {{
        put("TL", new int[]{20, 240});
        put("TC", new int[]{7, 172});
        put("TR", new int[]{-70, 230});
        put("BL", new int[]{110, 230});
        put("BC", new int[]{187, 172});
        put("BR", new int[]{200, 240});
    }};

    static final HashMap<Integer, Color[]> BALL_COLORS = new HashMap<Integer, Color[]>()
    {{
        put(0, new Color[]{BALL_WHITE, BALL_WHITE, BALL_WHITE, null});
        put(1, new Color[]{new Color(245, 195, 18), BALL_WHITE, Color.BLACK, null});
        put(2, new Color[]{new Color(49, 85, 171), BALL_WHITE, Color.BLACK, null});
        put(3, new Color[]{new Color(255, 69, 0), BALL_WHITE, Color.BLACK, null});
        put(4, new Color[]{new Color(82, 60, 171), BALL_WHITE, Color.BLACK, null});
        put(5, new Color[]{new Color(214, 19, 29), BALL_WHITE, Color.BLACK, null});
        put(6, new Color[]{new Color(37, 123, 61), BALL_WHITE, Color.BLACK, null});
        put(7, new Color[]{new Color(128, 0, 0), BALL_WHITE, Color.BLACK, null});
        put(8, new Color[]{Color.BLACK, new Color(255, 247, 200), Color.BLACK, null});
        put(9, new Color[]{BALL_WHITE, new Color(245, 195, 18), BALL_WHITE, Color.BLACK});
        put(10, new Color[]{BALL_WHITE, new Color(49, 85, 171), BALL_WHITE, Color.BLACK});
        put(11, new Color[]{BALL_WHITE, new Color(255, 69, 0), BALL_WHITE, Color.BLACK});
        put(12, new Color[]{BALL_WHITE, new Color(82, 60, 171), BALL_WHITE, Color.BLACK});
        put(13, new Color[]{BALL_WHITE, new Color(214, 19, 29), BALL_WHITE, Color.BLACK});
        put(14, new Color[]{BALL_WHITE, new Color(37, 123, 61), BALL_WHITE, Color.BLACK});
        put(15, new Color[]{BALL_WHITE, new Color(128, 0, 0), BALL_WHITE, Color.BLACK});
    }};
}