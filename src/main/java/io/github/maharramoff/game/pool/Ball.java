package io.github.maharramoff.game.pool;

import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

public final class Ball
{
    public static final Map<BallType, Color[]> BALL_COLORS = new EnumMap<>(BallType.class);

    static
    {
        for (BallType type : BallType.values())
        {
            BALL_COLORS.put(type, type.getColors());
        }
    }

    double x, y;
    int radius, number;
    double velocityX, velocityY;
    private double accelerationY, accelerationX;
    private final Sound sound = new Sound();

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
                (radius * RackConstants.BALL_SPACING);

        double y = GameSettings.SCREEN_MARGIN +
                (Table.HEIGHT * RackConstants.VERTICAL_CENTER_FRACTION) - radius;

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
                (radius * RackConstants.BALL_SPACING * rowOffset) -
                (RackConstants.RACK_ROW_HORIZONTAL_OFFSET * rowOffset);

    }

    private double calculateRackY(double footSpotY, int row, int number)
    {
        // Calculate triangular number sequence for balls in previous rows
        double previousRowsBalls = (row * (row + RackConstants.POSITION_ADJUSTMENT)) / 2;

        // Adjust for zero-based indexing in ball count
        double adjustedBallCount = previousRowsBalls - RackConstants.POSITION_ADJUSTMENT;

        // Calculate vertical offset based on ball position
        double ballPositionOffset = -radius * RackConstants.BALL_SPACING * (adjustedBallCount - number);

        // Calculate rack vertical adjustment
        double rackVerticalAdjustment = (row - RackConstants.VERTICAL_RACK_OFFSET) * radius;

        // Apply final radius adjustment
        double finalRadiusAdjustment = -radius;

        return footSpotY +
                ballPositionOffset +
                rackVerticalAdjustment +
                finalRadiusAdjustment;
    }

    Ball(int number)
    {
        radius = 10;
        this.number = number;
        setXY(number);
    }


    public void update()
    {
        if (velocityY != 0 || velocityX != 0)
        {
            y += velocityY;
            x += velocityX;

            if (GameSettings.frictionEnabled)
            {
                handleBallFriction();
            }
        }
    }

    void handleBounds()
    {
        boolean bound = false;

        if (x > GameSettings.SCREEN_MARGIN + Table.WIDTH - (Table.RAIL_WIDTH + 2 * radius))
        {
            velocityX = -Math.abs(velocityX);
            accelerationX = Math.abs(accelerationX);
            bound = true;
        }
        else if (x < GameSettings.SCREEN_MARGIN + Table.RAIL_WIDTH)
        {
            velocityX = Math.abs(velocityX);
            accelerationX = -Math.abs(accelerationX);
            bound = true;
        }

        if (y < GameSettings.SCREEN_MARGIN + Table.RAIL_WIDTH)
        {
            velocityY = Math.abs(velocityY);
            accelerationY = -Math.abs(accelerationY);
            bound = true;
        }
        else if (y > GameSettings.SCREEN_MARGIN + Table.HEIGHT - (Table.RAIL_WIDTH + 2 * radius))
        {
            velocityY = -Math.abs(velocityY);
            accelerationY = Math.abs(accelerationY);
            bound = true;
        }

        if (bound)
        {
            boundSound(velocityX, velocityY);
            velocityX *= 0.98;
            velocityY *= 0.98;
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
        double k = velocityX * velocityX + velocityY * velocityY;

        k = Math.sqrt(k) * GameSettings.TARGET_FPS / 2;

        if (k == 0)
        {
            return;
        }

        accelerationY = -velocityY / k;
        accelerationX = -velocityX / k;
    }

    private void handleBallFriction()
    {
        velocityY += accelerationY;
        velocityX += accelerationX;

        if (velocityX > 0 == accelerationX > 0)
        {
            velocityX = 0;
        }

        if (velocityY > 0 == accelerationY > 0)
        {
            velocityY = 0;
        }
    }

    public void draw(Graphics2D graphics2D)
    {
        graphics2D.setColor(BALL_COLORS.get(BallType.values()[number])[0]);
        graphics2D.fillOval((int) x, (int) y, 2 * radius, 2 * radius);
        graphics2D.setColor(BALL_COLORS.get(BallType.values()[number])[1]);

        if (number > 8)
        {
            graphics2D.fillRoundRect((int) (x), (int) (y + radius / 2), 20, 10, 7, 7);
            graphics2D.setColor(BALL_COLORS.get(BallType.values()[number])[2]);
            graphics2D.fillOval((int) (x + radius / 2), (int) (y + radius / 2), radius, radius);
            graphics2D.setColor(BALL_COLORS.get(BallType.values()[number])[3]);
        }
        else
        {
            graphics2D.fillOval((int) (x + radius / 2), (int) (y + radius / 2), radius, radius);
            graphics2D.setColor(BALL_COLORS.get(BallType.values()[number])[2]);
        }

        graphics2D.setFont(new Font("Arial Bold", Font.BOLD, 8));
        graphics2D.drawString(String.valueOf(number), (float) (x + (number >= 10 ? radius - 4.6 : radius - 2.6)), (float) (y + radius + 2.6));
    }

    public double getCenterX()
    {
        return this.x + radius;
    }

    public double getCenterY()
    {
        return this.y + radius;
    }
}


