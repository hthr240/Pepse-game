package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import pepse.util.Constants;

import java.awt.*;

/**
 * The sun object that is created in the world
 *
 * @author fanteo12
 */
public class Sun {

    // constants
    private static final String SUN_TAG = "sun";
    private static final float SUN_SIZE = 100;
    private static final Float INIT_SUN_DEGREE = 0f;
    private static final Float FINAL_SUN_DEGREE = 360f;
    private static final int GROUND_RATIO_NUMERATOR = 2;
    private static final int GROUND_RATIO_DENOMINATOR = 3;

    /**
     * Creates a sun object
     * @param windowDimensions the dimensions of the window
     * @param cycleLength the length of the cycle
     * @return the sun object
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength){

        OvalRenderable sunImage = new OvalRenderable(Color.YELLOW);
        GameObject sun = new GameObject(Vector2.ZERO,new Vector2(SUN_SIZE,SUN_SIZE), sunImage);
        sun.setCenter(new Vector2(windowDimensions.mult(Constants.SCREEN_RATIO)));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(SUN_TAG);

        Vector2 initialSunCenter = sun.getCenter();
        Vector2 cycleCenter = new Vector2(windowDimensions.x() * Constants.SCREEN_RATIO,
                windowDimensions.y() * GROUND_RATIO_NUMERATOR/GROUND_RATIO_DENOMINATOR);

        new Transition<>(
                sun,
                (Float angle) -> sun.setCenter
                        (initialSunCenter.subtract(cycleCenter)
                                .rotated(angle)
                                .add(cycleCenter)),
                INIT_SUN_DEGREE,
                FINAL_SUN_DEGREE,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,
                null);
        return sun;
    }
}