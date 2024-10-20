package io.github.maharramoff.game.pool;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.IntStream;

import static java.lang.Math.abs;

public final class Game extends JPanel implements Runnable
{
    private Thread thread;
    protected boolean isRunning       = true;
    private   boolean whiteBallFallen = false;
    protected boolean readyForShoot   = true;
    public    boolean movingWhiteBall = true;

    protected int indexOfWhiteBall = -1;
    private RenderingHints hints;

    protected ArrayList<Ball>          balls;
    Sound sound = new Sound();
    private Shoot shoot;


    Game()
    {
        super();
        setDoubleBuffered(true);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        setPreferredSize(new Dimension(Table.WIDTH + GameSettings.SCREEN_MARGIN * 2, Table.HEIGHT + GameSettings.SCREEN_MARGIN * 2));
        setSize(Table.WIDTH + GameSettings.SCREEN_MARGIN * 2, Table.HEIGHT + GameSettings.SCREEN_MARGIN * 2);
        setFocusable(true);
        requestFocus();
        shoot = new Shoot(this);
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
    }

    private void tableUpdate()
    {
        int totalBall = balls.size();

        if(totalBall == 0 || (totalBall == 1 && balls.get(0).number == 0))
            createNewGame();

        for (Ball ball : balls)
            if (ball.dx != 0 || ball.dy != 0)
                return;

        readyForShoot = true;
        shoot.aiming = true;
    }

    private void redrawWhiteBall(Ball B)
    {
        boolean isWhiteBall = false;

        if (B.number == 0)
            isWhiteBall = true;

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

    private boolean handleBallCollision(Ball A, Ball B)
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

                return true;
            }
        }

        return false;
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
        long targetTime = 1000 / GameSettings.TARGET_FPS;

        while (isRunning)
        {
            startTime = System.nanoTime();


            updateBalls();
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
        // Background
        graphics2D.setColor(Color.BLACK.brighter());
        graphics2D.fillRect(0, 0, Table.WIDTH + GameSettings.SCREEN_MARGIN * 2, Table.HEIGHT + GameSettings.SCREEN_MARGIN * 2);


        // Rails
        RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(GameSettings.SCREEN_MARGIN, GameSettings.SCREEN_MARGIN, Table.WIDTH, Table.HEIGHT, 30, 30);
        graphics2D.setColor(Table.RAIL_COLOR);
        graphics2D.fill(roundedRectangle);

        // Play Field
        graphics2D.setColor(Table.FIELD_COLOR);
        graphics2D.fillRect(GameSettings.SCREEN_MARGIN + Table.RAIL_WIDTH, GameSettings.SCREEN_MARGIN + Table.RAIL_WIDTH, Table.WIDTH - Table.RAIL_WIDTH * 2, Table.HEIGHT - Table.RAIL_WIDTH * 2);

        // Head String
        graphics2D.setColor(Ball.BALL_WHITE);
        graphics2D.drawLine(GameSettings.SCREEN_MARGIN + Table.WIDTH - Table.WIDTH / 4, GameSettings.SCREEN_MARGIN + Table.RAIL_WIDTH, GameSettings.SCREEN_MARGIN + Table.WIDTH - Table.WIDTH / 4, GameSettings.SCREEN_MARGIN + Table.HEIGHT - Table.RAIL_WIDTH);

        // Foot Spot
        graphics2D.drawOval(GameSettings.SCREEN_MARGIN + Table.WIDTH / 4, GameSettings.SCREEN_MARGIN + Table.HEIGHT / 2, 2, 2);


        // Pockets
        if(GameSettings.holesEnabled)
        {
            Pocket.POSITION_MAP.forEach((PocketPosition key, int[] value) ->
             {
                 graphics2D.setColor(new Color(46, 24, 12));
                 graphics2D.fillOval(Pocket.POSITION_MAP.get(key)[0], Pocket.POSITION_MAP.get(key)[1], Pocket.RADIUS * 2, Pocket.RADIUS * 2);
                 // Pocket Liners
                 graphics2D.setColor(PocketLiner.COLOR);
                 graphics2D.setStroke(new BasicStroke(2));
                 graphics2D.drawArc(Pocket.POSITION_MAP.get(key)[0], Pocket.POSITION_MAP.get(key)[1], Pocket.RADIUS * 2, Pocket.RADIUS * 2, PocketLiner.POSITION_MAP.get(key)[0], PocketLiner.POSITION_MAP.get(key)[1]);

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

        Ball b1, b2;

        for (int i = 0; i < balls.size(); i++)
        {

            b1 = balls.get(i);

            if (whiteBallFallen && readyForShoot)
            {
                redrawWhiteBall(b1);
                whiteBallFallen = false;
                movingWhiteBall = true;
            }

            b1.update();

            if (ifPocket(b1))
            {
                b1.dx = 0;
                b1.dy = 0;
                balls.remove(i);

                if(b1.number == 0)
                {
                    whiteBallFallen = true;
                    indexOfWhiteBall = -1;
                }
                else
                {
                    if (!whiteBallFallen)
                    {
                        indexOfWhiteBall = getCurrentIndexOfWhiteBall(balls);
                    }

                    // TODO handle fall
                }
            }
            else
            {
                b1.handleBounds();

                for (int b = i + 1; b < balls.size(); b++)
                {
                    b2 = balls.get(b);
                    if (handleBallCollision(b1, b2))
                    {
                        b1.startFriction();
                        b2.startFriction();
                    }
                }
            }
        }

        tableUpdate();
    }

    private boolean ifPocket(Ball B)
    {
        if(!GameSettings.holesEnabled) return false;

        if (movingWhiteBall) return false;

        Map<PocketPosition, int[]> map = Pocket.POSITION_MAP;

        for (Map.Entry<PocketPosition, int[]> entry : map.entrySet())
        {
            double dx   = entry.getValue()[0] + Pocket.RADIUS - B.getCenterX();
            double dy   = entry.getValue()[1] + Pocket.RADIUS - B.getCenterY();
            double dist = Math.sqrt(dx * dx + dy * dy);
            double min  = Math.sqrt((Pocket.RADIUS + B.r) * (Pocket.RADIUS + B.r)) - (B.r - Pocket.MARGIN);

            if (dist <= min)
            {
                sound.play("pocket.wav", -10.0f);
                tableUpdate();
                return true;
            }
        }

        return false;
    }


    private void reDrawBalls(Graphics2D graphics2D)
    {
        for (int i = 0; i < balls.size(); i++)
        {
            Ball ball = balls.get(i);
            ball.draw(graphics2D);
        }
    }

}
