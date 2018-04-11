package pool;


import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.IntStream;

import static java.lang.Math.abs;

public final class Game extends JPanel implements Runnable, KeyListener
{
    private Thread thread;
    private   boolean isRunning       = true;
    private   boolean hit             = false;
    private   boolean whiteBallFallen = false;
    protected boolean readyForShoot   = true;
    public    boolean movingWhiteBall = true;
    private   boolean cueTaken        = false;


    protected int indexOfWhiteBall = -1;
    private RenderingHints hints;

    private   HashMap<String, Integer> cue;
    protected ArrayList<Ball>          balls;
    Sound sound = new Sound();
    private Shoot shoot = new Shoot(this);

    Game()
    {
        super();
        setDoubleBuffered(true);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        setPreferredSize(new Dimension(Helper.SW, Helper.SH));
        setSize(Helper.SW, Helper.SH);
        setFocusable(true);
        addKeyListener(this);
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

       // addKeyListener(this);
    }

    private void tableUpdate()
    {
        for (Ball ball : balls)
            if (ball.dx != 0 || ball.dy != 0)
                return;

        readyForShoot = true;

        if (hit)
        {
            double speed = 15 + (30.0 / Helper.FPS) * 4;
            System.out.println(speed);
            balls.get(indexOfWhiteBall).dx = speed;
            balls.get(indexOfWhiteBall).dy = 0.3;
            balls.get(indexOfWhiteBall).startFriction();
            hit = false;
            readyForShoot = false;
            movingWhiteBall = false;
            sound.play("clash-old.wav");
        }
    }

    private void redrawWhiteBall()
    {
        boolean isWhiteBall = false;
        for (Ball c : balls)
        {
            if (c.number == 0)
                isWhiteBall = true;
        }

        if (!isWhiteBall)
        {
            balls.add(new Ball(0));
            indexOfWhiteBall = getCurrentIndexOfWhiteBall(balls);
        }
    }

    protected int getCurrentIndexOfWhiteBall(ArrayList<Ball> balls)
    {
        IntStream.range(0, balls.size()).filter(i -> balls.get(i).number == 0).forEach(i -> indexOfWhiteBall = i);

        if (indexOfWhiteBall == -1)
        {
            System.out.println("White ball not found !!!!");
            System.exit(0);
        }

        return indexOfWhiteBall;
    }

    private void checkBallsInPockets()
    {

        if(!Helper.HA) return;

        if (movingWhiteBall) return;

        Helper.HOLES.forEach((String key, int[] value) ->
         {
             for (int b = 0; b < balls.size(); b++)
             {
                 Ball B = balls.get(b);

                 double dx       = value[0] - B.x;
                 double dy       = value[1] - B.y;
                 double distance = Math.hypot(dx, dy) - Helper.HR - B.r;
                 double minDist  = -B.r;
                 if (distance < minDist)
                 {
                     balls.remove(b);
                     if(B.number == 0) whiteBallFallen = true;
                     sound.play("pocket.wav", -10.0f);
                 }
             }
         });
    }

    private void checkBallsCollisions()
    {
        for (int i = 0; i < balls.size(); i++)
        {
            for (int j = i + 1; j < balls.size(); j++)
            {
                Ball A = balls.get(i), B = balls.get(j);

                handleBallCollision(A, B);
            }
        }
    }

    private void handleBallCollision(Ball A, Ball B)
    {
        double dx = A.getCenterX() - B.getCenterX();
        double dy = A.getCenterY() - B.getCenterY();
        double dist = dx * dx + dy * dy;

        if (dist <= (A.r + B.r) * (A.r + B.r))
        {
            double xSpeed    = B.dx - A.dx;
            double ySpeed    = B.dy - A.dy;
            double getVector = dx * xSpeed + dy * ySpeed;

            if (getVector > 0)
            {
                double newX = dx * getVector / dist;
                double newY = dy * getVector / dist;
                A.dx += newX;
                A.dy += newY;
                B.dx -= newX;
                B.dy -= newY;

                handleCollisionSound(newX, newY);

                A.startFriction();
                B.startFriction();
            }
        }
    }

    private void handleCollisionSound(double newX, double newY)
    {

        double xyVelo = (Math.abs(newX) + Math.abs(newY)) / 2;
        float vol = -30.0f;
        if(xyVelo > 7)
            vol = 0.0f;
        else if(xyVelo > 4)
            vol = -5.0f;
        else if(xyVelo > 3)
            vol = -10.0f;
        else if (xyVelo > 1)
            vol = -20.0f;

        sound.play("collision.wav", vol);
    }

    public void run()
    {
        hints = createRenderingHints();

        generateBalls();

        long startTime, timeMillis, waitTime;
        long targetTime = 1000 / Helper.FPS;

        while (isRunning)
        {
            startTime = System.nanoTime();


            updateBalls();
            checkBallsInPockets();
            checkBallsCollisions();
            tableUpdate();
            repaint();
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

    @Override
    public void paint (Graphics g)
    {
        super.paint(g);
        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setRenderingHints(hints);
        tableRender(graphics2D);
    }


    private void tableRender(Graphics2D graphics2D)
    {
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
        if(Helper.HA)
        {
            Helper.HOLES.forEach((String key, int[] value) ->
             {
                 graphics2D.setColor(Color.BLACK);
                 graphics2D.fillOval(Helper.HOLES.get(key)[0], Helper.HOLES.get(key)[1], Helper.HR * 2, Helper.HR * 2);
                 // Hole arcs
                 graphics2D.setColor(Helper.HAC);
                 graphics2D.setStroke(new BasicStroke(2));
                 graphics2D.drawArc(Helper.HOLES.get(key)[0], Helper.HOLES.get(key)[1], Helper.HR * 2, Helper.HR * 2, Helper.ARCS.get(key)[0], Helper.ARCS.get(key)[1]);

             });
        }



        reDrawBalls(graphics2D);
        shoot.draw(graphics2D);

    }

    public void createNewGame()
    {
        generateBalls();
        movingWhiteBall = true;
    }

    private void generateBalls()
    {
        balls = new ArrayList<>();

        IntStream.range(0, 16).forEach(i -> balls.add(new Ball(i)));
        indexOfWhiteBall = 0;
    }

    private RenderingHints createRenderingHints() {
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                                                  RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_INTERPOLATION,
                  RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        hints.put(RenderingHints.KEY_RENDERING,
                  RenderingHints.VALUE_RENDER_QUALITY);
        return hints;
    }

    private void updateBalls()
    {
        if (whiteBallFallen && readyForShoot)
        {
            redrawWhiteBall();
            whiteBallFallen = false;
            movingWhiteBall = true;
        }

        balls.forEach(Ball::update);
    }

    private void reDrawBalls(Graphics2D graphics2D)
    {
        balls.forEach(ball -> ball.draw(graphics2D));
    }

    public void keyTyped(KeyEvent e)
    {
    }

    public void keyPressed(KeyEvent e)
    {
    }

    public void keyReleased(KeyEvent e)
    {
        if (!readyForShoot)
        {
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE)
        {
            indexOfWhiteBall = getCurrentIndexOfWhiteBall(balls);
            hit = true;
        }
    }

}
