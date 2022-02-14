package com.lilium.snake.game.util;

import com.lilium.snake.game.helper.Direction;
import com.lilium.snake.game.helper.Position;

/**
 * Util class containing helper methods for position calculations.
 *
 * @author mirza
 */
public final class PositionUtil {

    private PositionUtil() {}

    /**
     * Used to calculate next position based on current position and forwarded direction.
     *
     * @param currentPosition .
     * @param direction .
     * @return Returns calculated position.
     */
    public static Position getNextPosition(final Position currentPosition,
                                           final Direction direction) {
        if (direction == Direction.UP) {
            return buildPosition(
                    currentPosition.getX(),
                    currentPosition.getY() - GameUtil.PLAYER_SIZE
            );
        }

        if (direction == Direction.RIGHT) {
            return buildPosition(
                    currentPosition.getX() + GameUtil.PLAYER_SIZE,
                    currentPosition.getY()
            );
        }

        if (direction == Direction.DOWN) {
            return buildPosition(
                    currentPosition.getX(),
                    currentPosition.getY() + GameUtil.PLAYER_SIZE
            );
        }

        return buildPosition(
                currentPosition.getX() - GameUtil.PLAYER_SIZE,
                currentPosition.getY()
        );
    }

    /**
     * Used to verify if forwarded position is closer to the food.
     *
     * @param nextPosition .
     * @param foodPosition .
     * @param nextDirection .
     * @return .
     */
    public static boolean isPositionCloserToFoodPosition(final Position snakePosition,
    													 final Position nextPosition,
                                                         final Position foodPosition,
                                                         final Direction nextDirection) {
    	
        if(nextDirection == Direction.UP || nextDirection == Direction.DOWN) {
        	return Math.abs(snakePosition.getY() - foodPosition.getY()) > Math.abs(nextPosition.getY() - foodPosition.getY());
        }else {
        	return Math.abs(snakePosition.getX() - foodPosition.getX()) > Math.abs(nextPosition.getX() - foodPosition.getX()); 
        } 
    }

    private static Position buildPosition(final int x, final int y) {
        return new Position(x, y);
    }
}
