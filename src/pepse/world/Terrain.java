package pepse.world;

import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.Constants;
import pepse.util.NoiseGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The Terrain class generates and manages the ground in the game world.
 * It uses Perlin noise to create natural-looking variations in the ground height.
 *
 * @author fanteo12
 */
public class Terrain {

    // constants
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 25;
    private static final int NOISE_FACTOR = 270;


    // fields
    private static float groundHeightAtX0;
    private final NoiseGenerator noiseGenerator;

    /**
     * Constructs a Terrain instance for generating ground blocks.
     *
     * @param windowDimensions The dimensions of the game window.
     * @param seed A seed value for generating consistent noise patterns.
     */
    public Terrain(Vector2 windowDimensions, int seed){
        groundHeightAtX0 = windowDimensions.y() * Constants.GROUND_RATIO_NUMERATOR/Constants.GROUND_RATIO_DENOMINATOR;
        noiseGenerator = new NoiseGenerator(seed, (int)groundHeightAtX0);
    }

    /**
     * Calculates the ground height at a specific x-coordinate.
     *
     * @param x The x-coordinate to calculate the ground height for.
     * @return The height of the ground at the specified x-coordinate.
     */
    public float groundHeightAt(float x) { return (float)(groundHeightAtX0 + noiseGenerator.noise(x, NOISE_FACTOR)); }

    /**
     * Generates ground blocks within a specified horizontal range.
     *
     * @param minX The minimum x-coordinate of the range.
     * @param maxX The maximum x-coordinate of the range.
     * @return A list of Block objects representing the ground in the range.
     */
    public List<Block> createInRange(int minX, int maxX){

        List<Block> blockList = new ArrayList<>();

        // Align range boundaries to block size
        int newMaxX = (int)Math.floor((float)maxX / Block.getBlockSize()) * Block.getBlockSize();
        int newMinX = (int)Math.floor((float)minX / Block.getBlockSize()) * Block.getBlockSize();

        // Generate blocks for the specified range
        for (int i = newMinX; i <= newMaxX; i += Block.getBlockSize()) {
            int height = (int) (Math.floor(groundHeightAt(i) / Block.getBlockSize()) * Block.getBlockSize());
            for (int j = 0; j < TERRAIN_DEPTH; j++){
                Vector2 pos = new Vector2(i,height + j * Block.getBlockSize());
                Renderable blockImage = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
                Block block = new Block(pos, new Vector2(Block.getBlockSize(),Block.getBlockSize()), blockImage);
                if (j == 0) {block.setTag(Constants.GROUND_TAG);}
                blockList.add(block);
            }
        }
        return blockList;
    }
}
