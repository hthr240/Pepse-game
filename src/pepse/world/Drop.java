package pepse.world;

import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The Drop class represents a falling droplet object in the game world.
 * Drops are small, circular game objects that fall with a set velocity and fade away over time.
 *
 * @author fanteo12
 */
public class Drop {

    // constants
    private static final int DROP_COLOR_DELTA = 80;
    private static final Vector2 DROP_SIZE = new Vector2(10,10);
    private static final float DROP_VELOCITY = 100;
    private static final float TRANSITION_TIME = 3;

    /**
     * Creates a drop object at a specified position, adds it to the game, and manages its lifecycle.
     *
     * @param topLeftCorner The starting position of the drop.
     * @param removeGameObject A function to remove the drop from the game once it fades out.
     * @param addGameObject A function to add the drop to the game at a specific layer.
     */
    public static void create(Vector2 topLeftCorner, BiConsumer<GameObject,Integer> addGameObject,
                              Consumer<GameObject> removeGameObject){
        Renderable dropImage = new OvalRenderable(ColorSupplier.approximateColor(Color.BLUE,
                DROP_COLOR_DELTA));
        GameObject drop = new GameObject(topLeftCorner,DROP_SIZE,dropImage);
        drop.setVelocity(new Vector2(0,DROP_VELOCITY));
        drop.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        addGameObject.accept(drop, Layer.BACKGROUND);

        new Transition<>(drop,drop.renderer()::setOpaqueness,1f,0f,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                TRANSITION_TIME, Transition.TransitionType.TRANSITION_ONCE,()->removeGameObject.accept(drop));
    }
}
