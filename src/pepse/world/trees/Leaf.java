package pepse.world.trees;

import danogl.GameObject;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.Constants;

/**
 * The Leaf class represents a leaf object in the game world.
 * Leaves oscillate in angle and width over time to create a natural swaying effect.
 *
 * @author fanteo12
 */
public class Leaf extends GameObject {

    // constants
    private static final String LEAF_TAG = "leaf";
    private static final Float START_ANGLE = 0f;
    private static final Float END_ANGLE = 100f;
    private static final Vector2 MAX_LEAF_WIDTH = new Vector2(45,45);

    // fields
    private final float leafCycle;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public Leaf(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,float leafCycle, float randTime) {
        super(topLeftCorner, dimensions, renderable);

        this.setTag(LEAF_TAG);
        this.leafCycle = leafCycle;
        new ScheduledTask(this,randTime,true,()->{changeAngle();changeWidth();});
    }

    /**
     * transition for moving the leaf back and forth
     */
    private void changeWidth() {
        new Transition<>(this,
                (Float angle) -> this.renderer().setRenderableAngle(angle),
                START_ANGLE,
                END_ANGLE,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                leafCycle,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
    }

    /**
     * transition for changing the leaf width
     */
    private void changeAngle() {
        new Transition<>(this,
                this::setDimensions,
                new Vector2(Constants.LEAF_SIZE,Constants.LEAF_SIZE),
                MAX_LEAF_WIDTH,
                Transition.LINEAR_INTERPOLATOR_VECTOR, leafCycle,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
    }

}
