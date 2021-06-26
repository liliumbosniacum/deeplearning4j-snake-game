package com.lilium.snake.game.util;

import com.lilium.snake.game.helper.Direction;
import com.lilium.snake.game.helper.Position;
import com.lilium.snake.network.Action;

import java.util.Arrays;

/**
 * Util class containing helper methods for reward calculation.
 *
 * @author mirza
 */
public final class RewardUtil {

    private RewardUtil() {}

    /**
     * Used to calculate reward for taken action.
     *
     * @param action Action that was taken.
     * @param snakePosition Current snake position.
     * @param foodPosition Current food position.
     * @return Returns calculated reward value.
     */
    public static double calculateRewardForActionToTake(final Action action,
                                                        final Position[] snakePosition,
                                                        final Position foodPosition) {
        Direction nextDirection = Direction.UP;
        switch (action) {
            case MOVE_UP -> {}
            case MOVE_RIGHT -> nextDirection = Direction.RIGHT;
            case MOVE_DOWN -> nextDirection = Direction.DOWN;
            case MOVE_LEFT -> nextDirection = Direction.LEFT;
        }

        final Position position = PositionUtil.getNextPosition(
                snakePosition[0],
                nextDirection
        );

        return getRewardForPosition(nextDirection, position, snakePosition, foodPosition);
    }

    private static double getRewardForPosition(final Direction nextDirection,
                                               final Position nextPosition,
                                               final Position[] snakePosition,
                                               final Position foodPosition) {
        if (nextPosition.isOutsideTheGameBounds() || Arrays.asList(snakePosition).contains(nextPosition)) {
            return -100.0;
        }

        if (nextPosition.equals(foodPosition)) {
            return 100.0;
        }

        if (PositionUtil.isPositionCloserToFoodPosition(nextPosition, foodPosition, nextDirection)) {
            return 1.0;
        }

        return -1.0;
    }
}
