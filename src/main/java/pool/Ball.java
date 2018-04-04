package pool;

import java.awt.*;

public class Ball
{
    private double x, y;
    private int r, number, row, col;
    private double dx;
    private double dy;
    private double speed = 5;

    Ball(int number)
    {
        setSpeed(speed);
        r = 10;
        this.number = number;

        row = 6 - (int) Math.ceil((-1 + Math.sqrt(1 + 8 * number)) / 2);
        col = number - row * (row - 1) / 2;

        if (number == 0)
        {
            x = Helper.SW / 4;
            y = Helper.SH / 2;
        }
        else
        {
            x = (Helper.SW - Helper.SW / 4) + (r - (r * 2 * row));

            if(number > 10 && number <= 15)
            {
                y = Helper.SH / 2 + 2 * r - (r * 2 * (14 - number));
            }
            else if(number > 6 && number <= 10)
            {
                y = Helper.SH / 2 + r - (r * 2 * (9 - number));
            }
            else if(number > 3 && number <= 6)
            {
                y = (Helper.SH / 2) - (r * 2 * (5 - number));
            }
            else if(number > 1 && number <= 3)
            {
                y = (Helper.SH / 2) - r - (r * 2 * (2 - number));
            }
            else
            {
                y = (Helper.SH / 2);
            }
            /*else
            {
                x = (Helper.SW - Helper.SW / 4) + (r - (r * 2 * number));
            }*/


        }
        /*else
        {
            x = 100 + (number * 30) + Math.random() * Helper.TW / 2 + Helper.TW / 4;
            y = 60 + (number * 10)(int) (Math.random() * 100);
        }*/


        double angle  = Math.random() * 140 + 20;
        double radius = Math.toRadians(angle);

        //setDx(Math.cos(radius) * getSpeed());
        //setDy(Math.sin(radius) * getSpeed());
    }

    private double getSpeed()
    {
        return this.speed;
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
}


