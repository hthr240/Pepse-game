package pepse.world;

import danogl.GameObject;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.util.function.Supplier;

/**
 * The EnergyRepresentation class represents a UI element that displays the avatar's current energy level.
 * It dynamically updates the displayed energy percentage based on a supplied energy value.
 *
 * @author fanteo12
 */
public class EnergyPanel extends GameObject {

    // constants
    private static final String PERCENT_SIGN = "%";

    // fields
    private final TextRenderable textImage;
    private final Supplier<Float> getEnergy;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param textRenderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public EnergyPanel(Vector2 topLeftCorner, Vector2 dimensions, TextRenderable textRenderable,
                                Supplier<Float> getEnergy) {
        super(topLeftCorner, dimensions, textRenderable);
        textImage = textRenderable;
        this.getEnergy = getEnergy;
    }

    /**
     * Updates the energy display by fetching the current energy level and formatting it as a percentage.
     *
     * @param deltaTime Time elapsed since the last update.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        textImage.setString(((int) Math.floor(getEnergy.get())) + PERCENT_SIGN);
    }
}
