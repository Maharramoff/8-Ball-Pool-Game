package pool;

import java.awt.*;

public final class Ball
{
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
            x = Helper.SW / 4 - r;
            y = Helper.SH / 2 - r;
        }
        else
        {
            x = (Helper.SW - Helper.SW / 4) + (- (r * 2 * (6 - row))) + 4 - row;
            y = (Helper.SH / 2) - (r * 2 * (((row * (row + 1) / 2) - 1) - number)) + (row - 3) * r - r;
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

            if(Helper.FR) handleBallFriction();
        }
    }

    protected void handleBounds()
    {
        boolean bound = false;

        if (x > Helper.SW - (Helper.TB + 2 * r))
        {
            dx = -Math.abs(dx);
            ddx = Math.abs(ddx);
            bound = true;
        }
        else if (x < Helper.TB)
        {
            dx = Math.abs(dx);
            ddx = -Math.abs(ddx);
            bound = true;
        }

        if (y < Helper.TB)
        {
            dy = Math.abs(dy);
            ddy = -Math.abs(ddy);
            bound = true;
        }
        else if (y > Helper.SH - (Helper.TB + 2 * r))
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

        k = Math.sqrt(k) * Helper.FPS / 2;

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
        graphics2D.setColor(Helper.BALL_COLORS.get(number)[0]);
        graphics2D.fillOval((int) x, (int) y, 2 * r, 2 * r);
        graphics2D.setColor(Helper.BALL_COLORS.get(number)[1]);

        if (number > 8)
        {
            graphics2D.fillRoundRect((int) (x), (int) (y + r / 2), 20, 10, 7, 7);
            graphics2D.setColor(Helper.BALL_COLORS.get(number)[2]);
            graphics2D.fillOval((int) (x + r / 2), (int) (y + r / 2), r, r);
            graphics2D.setColor(Helper.BALL_COLORS.get(number)[3]);
        }
        else
        {
            graphics2D.fillOval((int) (x + r / 2), (int) (y + r / 2), r, r);
            graphics2D.setColor(Helper.BALL_COLORS.get(number)[2]);
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


