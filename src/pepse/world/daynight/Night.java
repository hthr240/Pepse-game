package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * The night object that is created in the world
 *
 * @author fanteo12
 */
public class Night {

    // constants
    private static final float OPACITY_MIDNIGHT = 0.5f;
    private static final float OPACITY_DAY = 0f;
    private static final String NIGHT_TAG = "night";

    /**
     * Creates a night object
     * @param windowDimensions the dimensions of the window
     * @param cycleLength the length of the cycle
     * @return the night object
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength){

        RectangleRenderable nightImage = new RectangleRenderable(Color.BLACK);
        GameObject night = new GameObject(Vector2.ZERO,windowDimensions,nightImage);
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.setTag(NIGHT_TAG);

        new Transition<>(
                night,
                night.renderer()::setOpaqueness,
                OPACITY_DAY,
                OPACITY_MIDNIGHT,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);

        return night;
    }
}