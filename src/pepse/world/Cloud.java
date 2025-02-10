package pepse.world;

import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The Cloud class is responsible for generating and managing cloud objects in the game world.
 * Clouds consist of blocks that move horizontally in a loop and interact with environmental changes.
 * Implements AvatarObserver to respond to updates from the Avatar.
 *
 * @author fanteo12
 */
public class Cloud implements Observer{

    // constants
    private static final Color BASE_CLOUD_COLOR = new Color(255, 255, 255);
    private static final int HEIGHT = 3;
    private static final int WIDTH = 10;
    private static final float START_WIDTH = -330;
    private static final float START_HEIGHT = 200;
    private static final Vector2 CLOUD_BLOCK_SIZE = new Vector2(30,30);
    private static final float TRANSITION_TIME = 20;
    private static final int CLOUD_BLOCK_RAND = 100;
    private static final int CLOUD_BLOCK_PERCENT = 70;

    // fields
    private final Vector2 windowDimensions;
    private final BiConsumer<GameObject, Integer> addGameObject;
    private final Consumer<GameObject> removeGameObject;
    private final Renderable cloudImage;
    private final ArrayList<ArrayList<Block>> blocksArray;

    /**
     * Constructs a Cloud instance, initializing its blocks and appearance.
     *
     * @param windowDimensions The dimensions of the game window.
     * @param addGameObject Function to add game objects to the game.
     * @param removeGameObject Function to remove game objects from the game.
     */
    public Cloud(Vector2 windowDimensions, BiConsumer<GameObject,Integer> addGameObject,
                 Consumer<GameObject> removeGameObject){
        this.windowDimensions = windowDimensions;
        this.addGameObject = addGameObject;
        this.removeGameObject = removeGameObject;
        cloudImage = new RectangleRenderable(ColorSupplier.approximateMonoColor(BASE_CLOUD_COLOR));
        blocksArray = new ArrayList<>();

        createCloudBlockArray();
    }

    /**
     * Creates a grid of blocks to represent the cloud structure.
     */
    private void createCloudBlockArray() {
        Random rand = new Random();
        for (int r = 0;  r < HEIGHT; r++){
            blocksArray.add(new ArrayList<>());
            for (int c = 0; c < WIDTH; c++){
                int block = rand.nextInt(CLOUD_BLOCK_RAND);
                if (block <= CLOUD_BLOCK_PERCENT){
                    createCloudBlock(r,c);
                }
            }
        }
    }

    /**
     * Creates a single cloud block at a specific grid position.
     */
    private void createCloudBlock(int r, int c) {
        Block block = new Block(new Vector2(START_WIDTH + c,START_HEIGHT + r),
                CLOUD_BLOCK_SIZE,cloudImage);
        block.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        blocksArray.get(r).add(block);
        addGameObject.accept(block, Layer.BACKGROUND);
        createBlockTransition(r,c,block);
    }

    /**
     * Sets a horizontal looping transition for a cloud block.
     */
    private void createBlockTransition(int r, int c, Block block) {
        new Transition<>(block,
                (Vector2 topLeftCorner)->block.setTopLeftCorner(
                        new Vector2(topLeftCorner.x() + Block.getBlockSize(),topLeftCorner.y())),
                new Vector2(START_WIDTH + c * Block.getBlockSize(),
                        START_HEIGHT + r * Block.getBlockSize()),
                new Vector2(windowDimensions.x() + c * Block.getBlockSize(),
                        START_HEIGHT + r * Block.getBlockSize()),
                Transition.LINEAR_INTERPOLATOR_VECTOR,
                TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_LOOP,
                null);
    }

    /**
     * Responds to updates triggered by the Avatar.
     * drops Droplets from the cloud blocks.
     */
    @Override
    public void update() {
        Random rand = new Random();
        for (ArrayList<Block> blocks : blocksArray) {
            for (Block block : blocks) {
                if (rand.nextBoolean()){
                    Drop.create(block.getCenter(),addGameObject,removeGameObject);
                }
            }
        }
    }
}
