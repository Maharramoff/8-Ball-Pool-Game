package pool;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public final class Shoot implements MouseListener, MouseMotionListener
{
    private Game                     game;
    protected boolean aiming = false, cue = true;
    private   int     power  = 20, aim_r = 10;
    private double aim_oval_x = 0, aim_oval_y = 0;
    private int aim_line_x1, aim_line_x2, aim_line_y1, aim_line_y2;

    Shoot(Game g)
    {
        this.game = g;
        game.addMouseListener(this);
        game.addMouseMotionListener(this);
    }

    public void draw(Graphics2D g)
    {
        if (cue && !game.movingWhiteBall && game.readyForShoot)
        {
            g.setColor(Helper.BALL_WHITE);
            g.setStroke(new BasicStroke(1));
            g.drawOval((int) aim_oval_x, (int) aim_oval_y, aim_r * 2, aim_r * 2);
            g.drawLine(aim_line_x1, aim_line_y1, aim_line_x2, aim_line_y2);
        }
    }

    @Override public void mouseClicked(MouseEvent e)
    {

    }

    @Override public void mousePressed(MouseEvent e)
    {
        if (!game.readyForShoot)
        {
            return;
        }

        Ball b;
        for (int ball = 0; ball < game.balls.size(); ball++)
        {
            b = game.balls.get(ball);

            if (Math.abs((int) b.x - e.getPoint().x + b.r) <= b.r && Math.abs((int) (b.y) - e.getPoint().y + b.r) <= b.r)
            {
                if (ball == game.indexOfWhiteBall && !game.movingWhiteBall)
                {
                    aiming = true;
                }
            }
        }
    }

    @Override public void mouseEntered(MouseEvent e)
    {

    }

    @Override public void mouseExited(MouseEvent e)
    {

    }

    @Override public void mouseReleased(MouseEvent e)
    {
        if (!game.readyForShoot)
        {
            return;
        }

        Ball b = game.balls.get(game.indexOfWhiteBall);

        cue = false;

        if (game.movingWhiteBall)
        {
            game.movingWhiteBall = false;
            aiming = true;
        }
        else if(aiming)
        {
            game.readyForShoot = false;

            double vX = aim_oval_x - b.x;
            double vY = aim_oval_y - b.y;
            double len = Math.sqrt((vX*vX)+(vY*vY));

            b.dx = vX/len * power * 60 / Helper.FPS;
            b.dy = vY/len * power * 60 / Helper.FPS;

            b.startFriction();
            aiming = false;
            game.sound.play("clash-old.wav", -20.0f);
        }
    }


    @Override public void mouseDragged(MouseEvent e)
    {
        if (!game.readyForShoot)
        {
            return;
        }

        Ball b = game.balls.get(game.getCurrentIndexOfWhiteBall(game.balls));

        int middleX = e.getX();
        int middleY = e.getY();

        if (game.movingWhiteBall)
        {
            b.x = middleX;
            b.y = middleY;
        }
    }

    @Override public void mouseMoved(MouseEvent e)
    {
        if(game.indexOfWhiteBall == -1) return;

        Ball b = game.balls.get(game.indexOfWhiteBall);

        if (game.movingWhiteBall)
        {
            double nx = e.getX(), ny = e.getY();

            double xr = Helper.TB + b.r;
            double xl = Helper.SW - b.r - Helper.TB;

            double yb = Helper.SH - b.r - Helper.TB;
            double yt = Helper.TB + b.r;

            if (nx < xr)
                nx = xr;
            else if (nx > xl)
                nx = xl;
            if (ny > yb)
                ny = yb;
            else if (ny < yt)
                ny = yt;

            b.x = nx - b.r;
            b.y = ny - b.r;
        }
        else if(aiming)
        {

            aim_oval_x = e.getX() - aim_r;
            aim_oval_y = e.getY() - aim_r * 2;

            aim_line_x1 = (int) aim_oval_x + aim_r;
            aim_line_y1 = (int) aim_oval_y + aim_r;

            double pa = Math.hypot(aim_line_x1 - b.getCenterX(), aim_line_y1 - b.getCenterY()) - b.r * 2;
            double dx = (b.x) + b.r - aim_line_x1;
            double dy = (b.y) + b.r - aim_line_y1;
            double i  = pa / (Math.sqrt(dx * dx + dy * dy));

            aim_line_x2 = (int) ((dx * i) + aim_line_x1);
            aim_line_y2 = (int) ((dy * i) + aim_line_y1);

            cue = true;
        }
    }
}
