package io.github.maharramoff.game.pool;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public final class Ball
{
    public static final Color BALL_WHITE = new Color(255, 247, 200);
    public static final Map<Integer, Color[]> BALL_COLORS = new HashMap<>();

    static {
        BALL_COLORS.put(0, new Color[]{BALL_WHITE, BALL_WHITE, BALL_WHITE, null});
        BALL_COLORS.put(1, new Color[]{new Color(245, 195, 18), BALL_WHITE, Color.BLACK, null});
        BALL_COLORS.put(2, new Color[]{new Color(49, 85, 171), BALL_WHITE, Color.BLACK, null});
        BALL_COLORS.put(3, new Color[]{new Color(255, 69, 0), BALL_WHITE, Color.BLACK, null});
        BALL_COLORS.put(4, new Color[]{new Color(82, 60, 171), BALL_WHITE, Color.BLACK, null});
        BALL_COLORS.put(5, new Color[]{new Color(214, 19, 29), BALL_WHITE, Color.BLACK, null});
        BALL_COLORS.put(6, new Color[]{new Color(37, 123, 61), BALL_WHITE, Color.BLACK, null});
        BALL_COLORS.put(7, new Color[]{new Color(128, 0, 0), BALL_WHITE, Color.BLACK, null});
        BALL_COLORS.put(8, new Color[]{Color.BLACK, new Color(255, 247, 200), Color.BLACK, null});
        BALL_COLORS.put(9, new Color[]{BALL_WHITE, new Color(245, 195, 18), BALL_WHITE, Color.BLACK});
        BALL_COLORS.put(10, new Color[]{BALL_WHITE, new Color(49, 85, 171), BALL_WHITE, Color.BLACK});
        BALL_COLORS.put(11, new Color[]{BALL_WHITE, new Color(255, 69, 0), BALL_WHITE, Color.BLACK});
        BALL_COLORS.put(12, new Color[]{BALL_WHITE, new Color(82, 60, 171), BALL_WHITE, Color.BLACK});
        BALL_COLORS.put(13, new Color[]{BALL_WHITE, new Color(214, 19, 29), BALL_WHITE, Color.BLACK});
        BALL_COLORS.put(14, new Color[]{BALL_WHITE, new Color(37, 123, 61), BALL_WHITE, Color.BLACK});
        BALL_COLORS.put(15, new Color[]{BALL_WHITE, new Color(128, 0, 0), BALL_WHITE, Color.BLACK});
    }

    protected double x, y;
    protected int    r, number;
    protected double dx, dy;
    private   double ddy, ddx;
    private Sound sound = new Sound();

    private void setXY(int number)
    {
        int row = (int) Math.ceil((-1 + Math.sqrt(1 + 8 * number)) / 2); // Using quadratic equation formula
        int col = row * (row - 1) / 2; // Perhaps this will be needed to further optimize the code

        if (number == 0)
        {
            x = GameSettings.SCREEN_MARGIN + Table.WIDTH / 4 - r;
            y = GameSettings.SCREEN_MARGIN + Table.HEIGHT / 2 - r;
        }
        else
        {
            x = (GameSettings.SCREEN_MARGIN + Table.WIDTH - Table.WIDTH / 4) + (- (r * 2 * (6 - row))) + 4 - row;
            y = (GameSettings.SCREEN_MARGIN + Table.HEIGHT / 2) - (r * 2 * (((row * (row + 1) / 2) - 1) - number)) + (row - 3) * r - r;
        }
    }

    Ball(int number)
    {
        r = 10;
        this.number = number;
        setXY(number);
    }


    public void update()
    {
        if (dy != 0 || dx != 0)
        {
            y += dy;
            x += dx;

            if(GameSettings.frictionEnabled) handleBallFriction();
        }
    }

    protected void handleBounds()
    {
        boolean bound = false;

        if (x > GameSettings.SCREEN_MARGIN + Table.WIDTH - (Table.RAIL_WIDTH + 2 * r))
        {
            dx = -Math.abs(dx);
            ddx = Math.abs(ddx);
            bound = true;
        }
        else if (x < GameSettings.SCREEN_MARGIN + Table.RAIL_WIDTH)
        {
            dx = Math.abs(dx);
            ddx = -Math.abs(ddx);
            bound = true;
        }

        if (y < GameSettings.SCREEN_MARGIN + Table.RAIL_WIDTH)
        {
            dy = Math.abs(dy);
            ddy = -Math.abs(ddy);
            bound = true;
        }
        else if (y > GameSettings.SCREEN_MARGIN + Table.HEIGHT - (Table.RAIL_WIDTH + 2 * r))
        {
            dy = -Math.abs(dy);
            ddy = Math.abs(ddy);
            bound = true;
        }

        if(bound)
        {
            boundSound(dx, dy);
            dx *= 0.98;
            dy *= 0.98;
        }
    }

    private void boundSound(double dx, double dy)
    {

        int xyVelo = (int) (Math.abs(dx) + Math.abs(dy));
        float vol = -20.0f;

        if(xyVelo > 10)
            vol = 1.0f;
        else if(xyVelo > 6)
            vol = -3.0f;
        else if(xyVelo > 3)
            vol = -6.0f;
        else if (xyVelo > 1)
            vol = -10.0f;

        sound.play("bump.wav", vol);

    }

    public void startFriction()
    {
        double k = dx * dx + dy * dy;

        k = Math.sqrt(k) * GameSettings.TARGET_FPS / 2;

        if (k == 0) return;

        ddy = -dy / k;
        ddx = -dx / k;
    }

    private void handleBallFriction()
    {
        dy += ddy;
        dx += ddx;

        if (dx > 0 == ddx > 0)
        {
            dx = 0;
        }

        if (dy > 0 == ddy > 0)
        {
            dy = 0;
        }
    }

    public void draw(Graphics2D graphics2D)
    {
        graphics2D.setColor(BALL_COLORS.get(number)[0]);
        graphics2D.fillOval((int) x, (int) y, 2 * r, 2 * r);
        graphics2D.setColor(BALL_COLORS.get(number)[1]);

        if (number > 8)
        {
            graphics2D.fillRoundRect((int) (x), (int) (y + r / 2), 20, 10, 7, 7);
            graphics2D.setColor(BALL_COLORS.get(number)[2]);
            graphics2D.fillOval((int) (x + r / 2), (int) (y + r / 2), r, r);
            graphics2D.setColor(BALL_COLORS.get(number)[3]);
        }
        else
        {
            graphics2D.fillOval((int) (x + r / 2), (int) (y + r / 2), r, r);
            graphics2D.setColor(BALL_COLORS.get(number)[2]);
        }

        graphics2D.setFont(new Font("Arial Bold", Font.BOLD, 8));
        graphics2D.drawString(String.valueOf(number), (float) (x + (number >= 10 ? r - 4.6 : r - 2.6)), (float) (y + r + 2.6));

        //graphics2D.drawString((int) getCenterX() + "-" + (int) x + ":" + (int) getCenterY() + "-" + (int) y, (float) x, (float) y);

    }

    public double getCenterX()
    {
        return this.x + r;
    }

    public double getCenterY()
    {
        return this.y + r;
    }
}


