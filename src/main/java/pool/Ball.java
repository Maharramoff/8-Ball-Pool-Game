package pool;

import java.awt.*;

public class Ball
{
    private double x, y;
    private int r, number;
    private double dx, dy, speed = 5;

    Ball(int number)
    {
        setSpeed(speed);
        r = 10;
        this.number = number;
        setXY(number);
    }

    private void setXY(int number)
    {
        int row = (int) Math.ceil((-1 + Math.sqrt(1 + 8 * number)) / 2); // Using quadratic equation formula
        int col = row * (row - 1) / 2; // Perhaps this will be needed to further optimize the code

        if (number == 0)
        {
            x = Helper.SW / 4;
            y = Helper.SH / 2;
        }
        else
        {
            x = (Helper.SW - Helper.SW / 4) + (r - (r * 2 * (6 - row)));
            y = (Helper.SH / 2) - (r * 2 * (((row * (row + 1) / 2) - 1) - number)) + (row - 3) * r;
        }

        setX(x);
        setY(y);
    }

    private void setSpeed(double s)
    {
        this.speed = s;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public void setDx(double dx)
    {
        this.dx = dx;
    }

    public void setDy(double dy)
    {
        this.dy = dy;
    }

    public double getDx()
    {
        return dx;
    }

    public double getDy()
    {
        return dy;
    }

    public int getR()
    {
        return r;
    }

    public void update()
    {
        dx *= 0.992;
        dy *= 0.992;

        if ((Math.abs(dx) + Math.abs(dy)) < 0.5)
        {
            dx *= 0.8;
            dy *= 0.8;
        }

        x += dx;
        y += dy;

        if (x - Helper.TB < r && dx < 0)
        {
            setDx(-dx);
        }
        if (y - Helper.TB < r && dy < 0)
        {
            setDy(-dy);
        }
        if (x - Helper.TB > Helper.TW - r && dx > 0)
        {
            setDx(-dx);
        }
        if (y - Helper.TB > Helper.TH - r && dy > 0)
        {
            setDy(-dy);
        }
    }

    public void draw(Graphics2D graphics2D)
    {
        graphics2D.setColor(Helper.BALL_COLORS.get(number)[0]);
        graphics2D.fillOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
        graphics2D.setColor(Helper.BALL_COLORS.get(number)[1]);

        if (number > 8)
        {
            graphics2D.fillRoundRect((int) (x - r), (int) (y - r / 2), 20, 10, 7, 7);
            graphics2D.setColor(Helper.BALL_COLORS.get(number)[2]);
            graphics2D.fillOval((int) (x - r / 2), (int) (y - r / 2), r, r);
            graphics2D.setColor(Helper.BALL_COLORS.get(number)[3]);
        }
        else
        {
            graphics2D.fillOval((int) (x - r / 2), (int) (y - r / 2), r, r);
            graphics2D.setColor(Helper.BALL_COLORS.get(number)[2]);
        }

        graphics2D.setFont(new Font("Arial Bold", Font.BOLD, 8));
        graphics2D.drawString(String.valueOf(number), (float) (x - (number >= 10 ? 4.6 : 2.6)), (float) (y + 2.6));

    }

    private void setX(double x)
    {
        this.x = x;
    }

    private void setY(double y)
    {
        this.y = y;
    }
}


