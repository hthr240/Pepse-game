package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * The sun halo object that is created in the world
 *
 * @author fanteo12
 */
public class SunHalo {

    // constants
    private static final float HALO_SIZE = 250;
    private static final String HALO_TAG = "halo";

    /**
     * create a sun halo
     * @param sun the sun object, the sunHalo will follow the sun movement behind it
     * @return the sunHalo object
     */
    public static GameObject create(GameObject sun){

        OvalRenderable haloImage = new OvalRenderable(new Color(255,255,0,20));
        Vector2 haloSize = new Vector2(HALO_SIZE,HALO_SIZE);
        GameObject sunHalo = new GameObject(sun.getCenter(),haloSize,haloImage);
        sunHalo.setTag(HALO_TAG);

        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sunHalo.addComponent((float deltaTime) -> sunHalo.setCenter(sun.getCenter()));

        return sunHalo;
    }
}
