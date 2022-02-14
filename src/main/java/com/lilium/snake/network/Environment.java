package com.lilium.snake.network;

import com.lilium.snake.game.Game;
import com.lilium.snake.network.util.NetworkUtil;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;

/**
 * Game environment that is used to train the network.
 *
 * @author mirza
 */
public class Environment implements MDP<GameState, Integer, DiscreteSpace> {
    // Size is 4 as we have 4 actions
    private final DiscreteSpace actionSpace = new DiscreteSpace(4);
    private final Game game;

    public Environment(final Game game) {
        this.game = game;
    }

    @Override
    public ObservationSpace<GameState> getObservationSpace() {
        return new GameObservationSpace();
    }

    @Override
    public DiscreteSpace getActionSpace() {
        return actionSpace;
    }

    @Override
    public GameState reset() {
        return game.initializeGame();
    }

    @Override
    public void close() {}

    @Override
    public StepReply<GameState> step(final Integer actionIndex) {
        // Find action based on action index
        final Action actionToTake = Action.getActionByIndex(actionIndex);

        // Change direction based on action, calculate the reward for the next step and move the snake in that direction
        game.changeDirection(actionToTake);
        double reward = game.calculateRewardForActionToTake(actionToTake);
        game.move();

        // If you want to see what is the snake doing while training increase this value
        NetworkUtil.waitMs(0);

        // Get current state
        final GameState observation = game.buildStateObservation();

        return new StepReply<>(
                observation,
                reward,
                isDone(),
                "SnakeDl4j"
        );
    }

    @Override
    public boolean isDone() {
        return !game.isOngoing();
    }

    @Override
    public MDP<GameState, Integer, DiscreteSpace> newInstance() {
        game.initializeGame();
        return new Environment(game);
    }
}
