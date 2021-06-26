package com.lilium.snake.network.util;

import com.lilium.snake.game.helper.Direction;
import com.lilium.snake.game.helper.Position;
import com.lilium.snake.game.util.PositionUtil;

import java.util.Arrays;

/**
 * Class containing some helper methods for {@link com.lilium.snake.network.GameState}.
 *
 * @author mirza
 */
public final class GameStateUtil {

    private GameStateUtil() {}

    /**
     * Calculate the state for given direction. Based on forwarded direction we check if head (or snake) can move in
     * that direction, and is food in that direction also. -1.0 indicates that snake cant go that way, 1.0 indicates
     * that food is in that direction, and 0.0 indicates that we can go that way but there is not food.
     *
     * @param snakePosition Current snake position.
     * @param foodPosition Current food position.
     * @param directionToCheck Direction in which we are checking.
     * @return Returns calculated state value.
     */
    public static double getStateForDirection(final Position[] snakePosition,
                                              final Position foodPosition,
                                              final Direction directionToCheck) {
        // Position of snakes head
        final Position head = snakePosition[0];
        final Position nextPosition = PositionUtil.getNextPosition(head, directionToCheck);

        if (isHeadUnableToMoveToNextPosition(nextPosition, snakePosition)) {
            return -1.0;
        }

        if (directionToCheck == Direction.UP) {
            if (head.getY() > foodPosition.getY()) {
                return 1.0;
            }

            return 0.0;
        }

        if (directionToCheck == Direction.RIGHT) {
            if (head.getX() < foodPosition.getX()) {
                return 1.0;
            }

            return 0.0;
        }

        if (directionToCheck == Direction.DOWN) {
            if (head.getY() < foodPosition.getY()) {
                return 1.0;
            }

            return 0.0;
        }

        if (directionToCheck == Direction.LEFT) {
            if (head.getX() < foodPosition.getX()) {
                return 1.0;
            }

            return 0.0;
        }

        return 0.0;
    }

    /**
     * Get the index of max value in the array.
     *
     * @param values Array of values.
     * @return Returns found index.
     */
    public static int getMaxValueIndex(final double[] values) {
        int maxAt = 0;

        for (int i = 0; i < values.length; i++) {
            maxAt = values[i] > values[maxAt] ? i : maxAt;
        }

        return maxAt;
    }

    private static boolean isHeadUnableToMoveToNextPosition(final Position nextPosition,
                                                            final Position[] snakePosition) {
        // Snake cant move to that position if it is outside the game bounds or if its body is in the way
        return nextPosition.isOutsideTheGameBounds() || Arrays.asList(snakePosition).contains(nextPosition);
    }
}
