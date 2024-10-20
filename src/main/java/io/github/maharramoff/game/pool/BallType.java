package io.github.maharramoff.game.pool;

import java.awt.*;

public enum BallType
{
    CUE(new Color(0xe7e5d5), new Color(0xe7e5d5), new Color(0xe7e5d5), null),
    ONE(new Color(0xf8c301), new Color(0xe7e5d5), new Color(0x0b0b0b), null),
    TWO(new Color(0x0419a8), new Color(0xe7e5d5), new Color(0x0b0b0b), null),
    THREE(new Color(0xf20000), new Color(0xe7e5d5), new Color(0x0b0b0b), null),
    FOUR(new Color(0x191047), new Color(0xe7e5d5), new Color(0x0b0b0b), null),
    FIVE(new Color(0xfc4e1d), new Color(0xe7e5d5), new Color(0x0b0b0b), null),
    SIX(new Color(0x005507), new Color(0xe7e5d5), new Color(0x0b0b0b), null),
    SEVEN(new Color(0x4c0108), new Color(0xe7e5d5), new Color(0x0b0b0b), null),
    EIGHT(new Color(0x0b0b0b), new Color(0xe7e5d5), new Color(0x0b0b0b), null),
    NINE(new Color(0xe7e5d5), new Color(0xf8c301), new Color(0xe7e5d5), new Color(0x0b0b0b)),
    TEN(new Color(0xe7e5d5), new Color(0x0419a8), new Color(0xe7e5d5), new Color(0x0b0b0b)),
    ELEVEN(new Color(0xe7e5d5), new Color(0xf20000), new Color(0xe7e5d5), new Color(0x0b0b0b)),
    TWELVE(new Color(0xe7e5d5), new Color(0x191047), new Color(0xe7e5d5), new Color(0x0b0b0b)),
    THIRTEEN(new Color(0xe7e5d5), new Color(0xfc4e1d), new Color(0xe7e5d5), new Color(0x0b0b0b)),
    FOURTEEN(new Color(0xe7e5d5), new Color(0x005507), new Color(0xe7e5d5), new Color(0x0b0b0b)),
    FIFTEEN(new Color(0xe7e5d5), new Color(0x4c0108), new Color(0xe7e5d5), new Color(0x0b0b0b));


    private final Color[] colors;

    BallType(Color... colors)
    {
        this.colors = colors;
    }

    public Color[] getColors()
    {
        return colors.clone();
    }
}
