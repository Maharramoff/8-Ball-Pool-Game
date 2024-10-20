package io.github.maharramoff.game.pool;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public final class Ball
{
    public static final Color BALL_WHITE = new Color(255, 247, 200);
    public static final Map<Integer, Color[]> BALL_COLORS = new HashMap<>();

    static
    {
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
    protected int r, number;
    protected double dx, dy;
    private double ddy, ddx;
    private Sound sound = new Sound();

    private static final class RackConstants
    {
        // Table position fractions
        static final double CUE_BALL_X_FRACTION = 1.0 / 4.0;  // Cue ball at 1/4 of table width
        static final double FOOT_SPOT_X_FRACTION = 3.0 / 4.0; // Rack position at 3/4 of table width
        static final double VERTICAL_CENTER_FRACTION = 1.0 / 2.0; // Vertical center of table

        // Ball spacing and positioning
        static final double BALL_SPACING = 2.0; // Spacing between balls (2 * radius)
        static final double RACK_ROW_HORIZONTAL_OFFSET = 2.0; // Horizontal offset between rack rows

        // Quadratic formula components for row calculation
        static final double TRIANGLE_SEQUENCE_MULTIPLIER = 8.0; // From expanding n(n+1)/2
        static final double QUADRATIC_CONSTANT_TERM = 1.0; // Constant in quadratic equation
        static final double QUADRATIC_B_COEFFICIENT = -1.0; // -b term from quadratic formula
        static final double QUADRATIC_DENOMINATOR = 2.0; // Denominator from quadratic formula

        static final int FIRST_ROW_INDEX = 1;  // Rows are 1-based indexed
        static final double HALF = 2.0;        // Used in division by 2
        static final int VERTICAL_RACK_OFFSET = 3;  // Vertical offset for rack positioning

        // Position adjustment constants
        static final double POSITION_ADJUSTMENT = 1.0;  // Used for position adjustments in calculations
    }

    private static final class BallPosition
    {
        final double x;
        final double y;

        BallPosition(double x, double y)
        {
            this.x = x;
            this.y = y;
        }
    }

    private void setXY(int number)
    {
        BallPosition position = calculateBallPosition(number);
        this.x = position.x;
        this.y = position.y;
    }

    private BallPosition calculateBallPosition(int number)
    {
        if (number == 0)
        {
            return calculateCueBallPosition();
        }
        return calculateRackBallPosition(number);
    }

    private BallPosition calculateCueBallPosition()
    {
        double x = GameSettings.SCREEN_MARGIN +
                (Table.WIDTH * RackConstants.CUE_BALL_X_FRACTION) -
                (r * RackConstants.BALL_SPACING);

        double y = GameSettings.SCREEN_MARGIN +
                (Table.HEIGHT * RackConstants.VERTICAL_CENTER_FRACTION) - r;

        return new BallPosition(x, y);
    }

    private BallPosition calculateRackBallPosition(int number)
    {
        int row = calculateRowNumber(number);

        // Calculate foot spot (apex of the rack) position
        double footSpotX = GameSettings.SCREEN_MARGIN +
                (Table.WIDTH * RackConstants.FOOT_SPOT_X_FRACTION);
        double footSpotY = GameSettings.SCREEN_MARGIN +
                (Table.HEIGHT * RackConstants.VERTICAL_CENTER_FRACTION);

        // Calculate position within the rack
        double x = calculateRackX(footSpotX, row);
        double y = calculateRackY(footSpotY, row, number);

        return new BallPosition(x, y);
    }

    private int calculateRowNumber(int ballNumber)
    {
        return (int) Math.ceil(
                (RackConstants.QUADRATIC_B_COEFFICIENT +
                        Math.sqrt(RackConstants.QUADRATIC_CONSTANT_TERM +
                                RackConstants.TRIANGLE_SEQUENCE_MULTIPLIER * ballNumber)) /
                        RackConstants.QUADRATIC_DENOMINATOR
        );
    }

    private double calculateRackX(double footSpotX, int row)
    {
        int rowOffset = row - RackConstants.FIRST_ROW_INDEX;

        return footSpotX +
                (r * RackConstants.BALL_SPACING * rowOffset) -
                (RackConstants.RACK_ROW_HORIZONTAL_OFFSET * rowOffset);

    }

    private double calculateRackY(double footSpotY, int row, int number)
    {
        // Calculate triangular number sequence for balls in previous rows
        double previousRowsBalls = (row * (row + RackConstants.POSITION_ADJUSTMENT)) / 2;

        // Adjust for zero-based indexing in ball count
        double adjustedBallCount = previousRowsBalls - RackConstants.POSITION_ADJUSTMENT;

        // Calculate vertical offset based on ball position
        double ballPositionOffset = -r * RackConstants.BALL_SPACING * (adjustedBallCount - number);

        // Calculate rack vertical adjustment
        double rackVerticalAdjustment = (row - RackConstants.VERTICAL_RACK_OFFSET) * r;

        // Apply final radius adjustment
        double finalRadiusAdjustment = -r;

        return footSpotY +
                ballPositionOffset +
                rackVerticalAdjustment +
                finalRadiusAdjustment;
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

            if (GameSettings.frictionEnabled)
            {
                handleBallFriction();
            }
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

        if (bound)
        {
            boundSound(dx, dy);
            dx *= 0.98;
            dy *= 0.98;
        }
    }

    private void boundSound(double dx, double dy)
    {

        int   xyVelo = (int) (Math.abs(dx) + Math.abs(dy));
        float vol    = -20.0f;

        if (xyVelo > 10)
        {
            vol = 1.0f;
        }
        else if (xyVelo > 6)
        {
            vol = -3.0f;
        }
        else if (xyVelo > 3)
        {
            vol = -6.0f;
        }
        else if (xyVelo > 1)
        {
            vol = -10.0f;
        }

        sound.play("bump.wav", vol);

    }

    public void startFriction()
    {
        double k = dx * dx + dy * dy;

        k = Math.sqrt(k) * GameSettings.TARGET_FPS / 2;

        if (k == 0)
        {
            return;
        }

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


