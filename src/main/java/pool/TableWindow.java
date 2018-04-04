package pool;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.stream.IntStream;

import static java.lang.Math.abs;

public class TableWindow extends JPanel implements Runnable, KeyListener
{
    private static final int FPS = 60;
    private Thread thread;
    private boolean isRunning = true;
    private boolean isStarted = false;
    private BufferedImage bufferedImage;
    private Graphics2D    graphics2D;

    private static ArrayList<Ball> balls;
    private Sound sound = new Sound();

    TableWindow()
    {
        super();
        setPreferredSize(new Dimension(Helper.SW, Helper.SH));
        setSize(Helper.SW, Helper.SH);
        setMaximumSize(new Dimension(Helper.SW, Helper.SH));
        setFocusable(true);
        requestFocus();

    }

    @Override
    public void addNotify()
    {
        super.addNotify();
        if (thread == null)
        {
            thread = new Thread(this);
            thread.start();
        }

        addKeyListener(this);
    }

    public void run()
    {
        bufferedImage = new BufferedImage(Helper.SW, Helper.SH, BufferedImage.TYPE_INT_RGB);
        graphics2D = (Graphics2D) bufferedImage.getGraphics();
        graphics2D.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        graphics2D.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        balls = new ArrayList<>();

        IntStream.range(0, 16).forEach(i -> balls.add(new Ball(i)));

        long startTime, timeMillis, waitTime;
        long targetTime = 1000 / FPS;

        while (isRunning)
        {
            startTime = System.nanoTime();

            tableUpdate();
            tableRender();
            tableDraw();

            timeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = abs(targetTime - timeMillis);

            try
            {
                Thread.sleep(waitTime);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }


    private void tableUpdate()
    {
        checkBallsInPockets();
        updateBalls();

        if(isStarted)
        checkBallsCollisions();
    }

    private void tableRender()
    {
        // Screen
        graphics2D.setColor(Color.WHITE);
        graphics2D.fillRect(0, 0, Helper.SW, Helper.SH);

        // Borders
        RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(0, 0, Helper.SW, Helper.SH, 30, 30);
        graphics2D.setColor(Helper.BC);
        graphics2D.fill(roundedRectangle);

        // Table
        graphics2D.setColor(Helper.TC);
        graphics2D.fillRect(Helper.TB, Helper.TB, Helper.SW - Helper.TB * 2, Helper.SH - Helper.TB * 2);

        // Table white line and dot
        graphics2D.setColor(Helper.BALL_WHITE);
        graphics2D.drawLine(Helper.SW - Helper.SW / 4, Helper.TB, Helper.SW - Helper.SW / 4, Helper.SH - Helper.TB);
        graphics2D.drawOval(Helper.SW / 4, Helper.SH / 2, 2, 2);


        // Holes
        Helper.HOLES.forEach((String key, int[] value) ->
         {
             graphics2D.setColor(Color.BLACK);
             graphics2D.fillOval(Helper.HOLES.get(key)[0], Helper.HOLES.get(key)[1], Helper.HR * 2, Helper.HR * 2);
             // Hole arcs
             graphics2D.setColor(Helper.HAC);
             graphics2D.setStroke(new BasicStroke(2));
             graphics2D.drawArc(Helper.HOLES.get(key)[0], Helper.HOLES.get(key)[1], Helper.HR * 2, Helper.HR * 2, Helper.ARCS.get(key)[0], Helper.ARCS.get(key)[1]);

         });


        reDrawBalls();

    }

    private void tableDraw()
    {
        Graphics draw = this.getGraphics();
        draw.drawImage(bufferedImage, 0, 0, null);
        draw.dispose();
    }

    public void keyTyped(KeyEvent e)
    {
    }

    public void keyPressed(KeyEvent e)
    {
    }

    public void keyReleased(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_SPACE)
            setStarted(true);
    }

    private void updateBalls()
    {
        balls.forEach(Ball::update);
    }

    private void reDrawBalls()
    {
        balls.forEach(ball -> ball.draw(graphics2D));
    }

    private void checkBallsCollisions()
    {
        for (int j = 0; j < balls.size(); j++)
        {
            for (int k = 0; k < balls.size(); k++)
            {
                if (j != k)
                {
                    double dx       = balls.get(k).getX() - balls.get(j).getX();
                    double dy       = balls.get(k).getY() - balls.get(j).getY();
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    double minDist  = balls.get(k).getR() + balls.get(j).getR() + 6;
                    if (distance < minDist)
                    {
                        double angle   = Math.atan2(dy, dx);
                        double targetX = balls.get(j).getX() + Math.cos(angle) * minDist;
                        double targetY = balls.get(j).getY() + Math.sin(angle) * minDist;
                        double ax      = (targetX - balls.get(k).getX()) * 0.05;
                        double ay      = (targetY - balls.get(k).getY()) * 0.05;
                        balls.get(j).setDx(balls.get(j).getDx() - ax);
                        balls.get(j).setDy(balls.get(j).getDy() - ay);
                        balls.get(k).setDx(balls.get(k).getDx() + ax);
                        balls.get(k).setDy(balls.get(k).getDy() + ay);
                    }
                }
            }
        }
    }

    public boolean isStarted()
    {
        return isStarted;
    }

    public void setStarted(boolean started)
    {
        isStarted = started;
    }

    private void checkBallsInPockets()
    {
        Helper.HOLES.forEach((String key, int[] value) ->
        {
            for (int b = 0; b < balls.size(); b++)
            {

                 double dx       = balls.get(b).getX() - value[0] - 15;
                 double dy       = balls.get(b).getY() - value[1] - 10;
                 double distance = Math.sqrt(dx * dx + dy * dy);
                 double minDist  = balls.get(b).getR() + Helper.HR - 10;
                 if (distance < minDist)
                 {
                     balls.remove(b);
                     sound.play("pocket.wav");
                 }
            }
        });
    }
}
