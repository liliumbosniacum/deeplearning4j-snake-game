package com.lilium.snake.game.util;

import javax.swing.*;
import java.awt.*;

/**
 * Util class containing methods used to ease our handling of the game.
 *
 * @author mirza
 */
public final class GameUtil {
    // region Member
    /**
     * Dimensions of game world 300x300.
     */
    public static final int GAME_DIMENSIONS = 300;
    /**
     * Size of a player body part (head or tail).
     */
    public static final int PLAYER_SIZE = 10;
    // endregion

    // region Constructor
    private GameUtil() {}
    // endregion

    // region Implementation
    public static Image getFoodImage() {
        return new ImageIcon("src/main/resources/images/food.png").getImage();
    }

    public static Image getHeadImage() {
        return new ImageIcon("src/main/resources/images/head.png").getImage();
    }

    public static Image getObservationImage() {
        return new ImageIcon("src/main/resources/images/observation.png").getImage();
    }

    public static Image getTailImage() {
        return new ImageIcon("src/main/resources/images/tail.png").getImage();
    }
    // endregion
}
