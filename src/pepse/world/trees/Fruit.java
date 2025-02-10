package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.Layer;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.Constants;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The Fruit class represents a fruit object in the game world.
 * Fruits can interact with the Avatar, disappear on collision, and reappear after a day cycle.
 *
 * @author fanteo12
 */
public class Fruit extends GameObject {

    // constants
    private static final float FRUIT_CYCLE = 30f;

    // fields
    private final Consumer<GameObject> removeGameObject;
    private final BiConsumer<GameObject, Integer> addGameObject;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public Fruit(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                 Consumer<GameObject> removeGameObject, BiConsumer<GameObject, Integer> addGameObject) {
        super(topLeftCorner, dimensions, renderable);
        
        this.setTag(Constants.FRUIT_TAG);
        this.removeGameObject = removeGameObject;
        this.addGameObject = addGameObject;
    }

    /**
     * Handles collision events involving the fruit.
     * If the fruit collides with the avatar, it is removed and scheduled to reappear after a delay.
     *
     * @param other The other GameObject involved in the collision.
     * @param collision Details about the collision.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);

        if (other.getTag().equals(Constants.AVATAR_TAG)){
            removeGameObject.accept(this);
        }

        new ScheduledTask(other,
                FRUIT_CYCLE,
                false,
                ()->addGameObject.accept(this, Layer.DEFAULT));
    }
}
