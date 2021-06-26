package com.lilium.snake.game;

import com.lilium.snake.game.helper.Direction;
import com.lilium.snake.game.helper.Position;
import com.lilium.snake.game.util.GameUtil;
import com.lilium.snake.game.util.PositionUtil;
import com.lilium.snake.game.util.RewardUtil;
import com.lilium.snake.network.Action;
import com.lilium.snake.network.GameState;
import com.lilium.snake.network.util.GameStateUtil;
import com.lilium.snake.network.util.NetworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Objects;

/**
 * Implementation of a simple snake game with some extra methods needed for the network.
 * Original implementation can be found here https://github.com/janbodnar/Java-Snake-Game
 *
 * @author mirza
 */
public class Game extends JPanel implements ActionListener {
    // region Member
    private static final Logger LOG = LoggerFactory.getLogger(Game.class);

    private static final Image FOOD_IMAGE = GameUtil.getFoodImage();
    private static final Image TAIL_IMAGE = GameUtil.getTailImage();
    private static final Image HEAD_IMAGE = GameUtil.getHeadImage();
    private static final Image OBSERVATION_IMAGE = GameUtil.getObservationImage();

    // Used to keep track of all snake parts (positions of the tail and head)
    private transient Position[] snakePosition = new Position[900];
    private Direction currentDirection = Direction.RIGHT;
    private boolean inGame = true;

    private transient Position foodPosition;
    private int snakeLength;
    // endregion

    // region Setup
    public Game() {
        setBackground(Color.WHITE);
        setFocusable(true);
        setPreferredSize(new Dimension(GameUtil.GAME_DIMENSIONS, GameUtil.GAME_DIMENSIONS));

        initializeGame();
    }
    // endregion

    // region Implementation
    @Override
    public void actionPerformed(ActionEvent e) {
        if (isOngoing()) {
            if (isFoodEaten()) {
                // Increase player length
                snakeLength++;

                // Set food on a new position
                setFoodPosition();
            } else {
                final Position headPosition = getHeadPosition();
                inGame = !headPosition.isOutsideTheGameBounds();

                if (inGame) { // We only need to check for body part collision if we are still in the game
                    checkIfPlayerHeadIsCollidingWithOtherBodyParts(headPosition);
                }
            }
        }

        if (!inGame) {
            LOG.debug("Game is over :(");
        }

        repaint();
    }

    @Override
    protected void paintComponent(final Graphics graphics) {
        super.paintComponent(graphics);

        draw(graphics);
    }

    /**
     * Get current game score.
     *
     * @return .
     */
    public int getScore() {
        return snakeLength - 3;
    }

    /**
     * Move the player and check for collisions
     */
    public void move() {
        // Copy positions (e.g. head position is not moved to the top of the body, body - 1 is not body - 2 and so on)
        if (snakeLength - 1 >= 0) System.arraycopy(snakePosition, 0, snakePosition, 1, snakeLength - 1);

        // Previous head position is currently at index 1
        final Position previousHeadPosition = snakePosition[1];

        // Set new head position
        snakePosition[0] = PositionUtil.getNextPosition(previousHeadPosition, currentDirection);

        // As we do not use any key pressed events to move our player we need "manually" notify about performed action
        actionPerformed(null);
    }

    /**
     * Change direction based on forwarded action.
     *
     * @param action Action based on which direction is changed.
     */
    public void changeDirection(final Action action) {
        LOG.debug("Current direction {}, changing to {}", currentDirection, action);

        switch (action) {
            case MOVE_UP:
                if (currentDirection == Direction.DOWN) break;
                currentDirection = Direction.UP;
                break;
            case MOVE_RIGHT:
                if (currentDirection == Direction.LEFT) break;
                currentDirection = Direction.RIGHT;
                break;
            case MOVE_DOWN:
                if (currentDirection == Direction.UP) break;
                currentDirection = Direction.DOWN;
                break;
            case MOVE_LEFT:
                if (currentDirection == Direction.RIGHT) break;
                currentDirection = Direction.LEFT;
                break;
        }
    }

    /**
     * Initializes game world and places the food and player on starting position
     */
    public GameState initializeGame() {
        snakeLength = 3;
        snakePosition = new Position[900];

        // Set snake on it's default position
        for (int i = 0; i < snakeLength; i++) {
            snakePosition[i] = new Position(50 - i * GameUtil.PLAYER_SIZE, 50);
        }

        // Set food position
        setFoodPosition();

        // Mark that player is in game
        inGame = true;

        // Return observation of the current game state
        return buildStateObservation();
    }

    /**
     * Used to check if the game is still ongoing.
     *
     * @return Returns true if player is still alive and in the game.
     */
    public boolean isOngoing() {
        return inGame;
    }

    /**
     * Used to end game.
     */
    public void endGame() {
        this.inGame = false;
    }

    /**
     * Get current game state observation. Snake can observe 4 states. They are positions in 4 directions around the
     * head. For example if head is at pos(50 50) => pos(x y), snake can observe position UP pos(50 40), DOWN pos(50 60),
     * RIGHT pos(60 50) and LEFT pos(40 50).
     *
     * @return Returns an object representing current game state observation.
     */
    public GameState buildStateObservation() {
        return new GameState(new double[] {
                GameStateUtil.getStateForDirection(snakePosition, foodPosition, Direction.UP),
                GameStateUtil.getStateForDirection(snakePosition, foodPosition, Direction.RIGHT),
                GameStateUtil.getStateForDirection(snakePosition, foodPosition, Direction.DOWN),
                GameStateUtil.getStateForDirection(snakePosition, foodPosition, Direction.LEFT),
        });
    }

    /**
     * Used to calculate the reward for action that was taken.
     *
     * @param action Taken action.
     * @return Returns calculated reward.
     */
    public double calculateRewardForActionToTake(final Action action) {
        return RewardUtil.calculateRewardForActionToTake(
                action,
                snakePosition,
                foodPosition
        );
    }
    // endregion

    // region Helper
    private void draw(final Graphics graphics) {
        if (!isOngoing()) {
            return;
        }

        // Draw food
        graphics.drawImage(FOOD_IMAGE, foodPosition.getX(), foodPosition.getY(), this);

        // Draw snake
        for (int i = 0; i < snakeLength; i++) {
            // Position of one of the snake parts (head or tail)
            final Position pos = snakePosition[i];
            if (pos == null) {
                continue;
            }

            // First item is always head
            graphics.drawImage(i == 0 ? HEAD_IMAGE : TAIL_IMAGE, pos.getX(), pos.getY(), this);
        }

        final Position headPosition = getHeadPosition();
        final Position[] observations = new Position[NetworkUtil.NUMBER_OF_INPUTS];
        // If we decide to have more inputs we need to modify the code to get more then just next position
        observations[0] = PositionUtil.getNextPosition(headPosition, Direction.UP);
        observations[1] = PositionUtil.getNextPosition(headPosition, Direction.RIGHT);
        observations[2] = PositionUtil.getNextPosition(headPosition, Direction.DOWN);
        observations[3] = PositionUtil.getNextPosition(headPosition, Direction.LEFT);
        for (int i = 0; i < observations.length; i++) {
            final Position pos = observations[i];
            if (pos == null) {
                continue;
            }

            // Draw what snake can see
            graphics.drawImage(OBSERVATION_IMAGE, pos.getX(), pos.getY(), this);
        }

        // Synchronize graphics state
        Toolkit.getDefaultToolkit().sync();
    }

    private void setFoodPosition() {
        foodPosition = new Position(
                (int) (Math.random() * 29) * GameUtil.PLAYER_SIZE,
                (int) (Math.random() * 29)  * GameUtil.PLAYER_SIZE
        );

        // Do not set food onto snake
        if (Arrays.asList(snakePosition).contains(foodPosition)) {
            setFoodPosition();
        }
    }

    private boolean isFoodEaten() {
        // Return true if snakes head is on the food position (snake if having a snack)
        return foodPosition.equals(getHeadPosition());
    }

    private void checkIfPlayerHeadIsCollidingWithOtherBodyParts(final Position headPosition) {
        // Count how many times is snake head contained in the snake position array. We expect only to find it once.
        // If there is more then one that means that the head is overlapping the body and we can end the game.
        final long matches = Arrays.stream(snakePosition)
                .filter(Objects::nonNull)
                .filter(pos -> pos.equals(headPosition))
                .count();

        if (matches > 1) {
            endGame();
        }
    }

    private Position getHeadPosition() {
        return snakePosition[0];
    }
    // endregion
}
