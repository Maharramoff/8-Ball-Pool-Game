package pool;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;

public final class Shoot implements MouseListener, MouseMotionListener
{
    private Game                     game;
    private HashMap<String, Integer> cue;
    private   boolean cueTaken        = false;

    Shoot(Game g)
    {
        this.game = g;
        game.addMouseListener(this);
        game.addMouseMotionListener(this);
    }

    public void draw(Graphics g)
    {
        if (cueTaken)
        {
            g.setColor(Color.LIGHT_GRAY);
            if (cue == null)
                return;
            g.drawLine(
                    cue.get("x1"), cue.get("y1"),
                    cue.get("x2"), cue.get("y2"));
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

        System.out.println("Mouse click " + e.getPoint());

        Ball b;
        for (int ball = 0; ball < game.balls.size(); ball++)
        {
            b = game.balls.get(ball);

            if (Math.abs((int) b.x - e.getPoint().x + b.r) <= b.r && Math.abs((int) (b.y) - e.getPoint().y + b.r) <= b.r)
            {
                if (ball == game.indexOfWhiteBall && !game.movingWhiteBall)
                    cueTaken = true;
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

        System.out.println("Mouse release " + e.getX() + "," + e.getY());

        cue = null;

        if (cueTaken)
        {
            game.readyForShoot = false;

            b.dx = (b.x + 10 - e.getX()) / 25;
            b.dy = (b.y + 10 - e.getY()) / 25;

            System.out.println("Ball " + b.dx + ", " + b.dy);

            b.startFriction();

            cueTaken = false;
            game.sound.play("clash-old.wav");
        }
        else if (game.movingWhiteBall)
        {
            game.movingWhiteBall = false;
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

        if (cueTaken)
        {
            cue = new HashMap<>();
            cue.put("x1", middleX);
            cue.put("y1", middleY);

            int    pa = 300;
            double dx = (b.x) + 10 - middleX;
            double dy = (b.y) + 10 - middleY;
            double i  = pa / (Math.sqrt(dx * dx + dy * dy));

            cue.put("x2", (int) ((dx * i) + middleX));
            cue.put("y2", (int) ((dy * i) + middleY));

        }
        else if (game.movingWhiteBall)
        {
            b.x = middleX;
            b.y = middleY;
        }
    }

    @Override public void mouseMoved(MouseEvent e)
    {
        if (game.movingWhiteBall)
        {
            Ball b = game.balls.get(game.indexOfWhiteBall);

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
    }
}
