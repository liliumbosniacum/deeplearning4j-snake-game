import com.lilium.snake.game.helper.Direction;
import com.lilium.snake.game.helper.Position;
import com.lilium.snake.network.util.GameStateUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GameStateUtilTest {

    @Test
    public void testBuildStateObservation() {
        final Position[] snakePosition = new Position[]{
                new Position(50, 50),
                new Position(40, 50),
                new Position(30, 50),
                new Position(30, 40),
                new Position(40, 40),
                new Position(50, 40),
                new Position(60, 40)
        };
        final Position foodPosition = new Position(100, 50);

        assertThat(GameStateUtil.getStateForDirection(
                snakePosition,
                foodPosition,
                Direction.UP)
        ).isEqualTo(-1.0);

        assertThat(GameStateUtil.getStateForDirection(
                snakePosition,
                foodPosition,
                Direction.RIGHT)
        ).isEqualTo(1.0);

        assertThat(GameStateUtil.getStateForDirection(
                snakePosition,
                foodPosition,
                Direction.DOWN)
        ).isEqualTo(0.0);

        assertThat(GameStateUtil.getStateForDirection(
                snakePosition,
                foodPosition,
                Direction.LEFT)
        ).isEqualTo(-1.0);
    }
}
