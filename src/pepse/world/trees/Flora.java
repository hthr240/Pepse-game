package pepse.world.trees;

import danogl.GameObject;
import pepse.world.Block;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The Flora class is responsible for generating and managing trees within a specified range.
 * It uses randomness to determine where trees are placed.
 *
 * @author fanteo12
 */
public class Flora {

    // constants
    private static final int TREES_RANDOMNESS = 10;
    private static final float TREE_PERCENT = 2;

    // fields
    private final BiConsumer<GameObject, Integer> addGameObject;
    private final Consumer<GameObject> removeGameObject;
    private final int seed;
    private final ArrayList<Tree> trees;
    private final Function<Float, Float> getHeight;

    /**
     * Constructs a Flora instance for managing tree generation.
     *
     * @param getHeight Function to calculate the ground height at a given x-coordinate.
     * @param addGameObject Function to add game objects to the game world.
     * @param removeGameObject Function to remove game objects from the game world.
     * @param seed A seed for generating consistent randomness.
     */
    public Flora(Function<Float,Float> getHeight, BiConsumer<GameObject,Integer> addGameObject,
                 Consumer<GameObject> removeGameObject, int seed) {
        this.addGameObject = addGameObject;
        this.removeGameObject = removeGameObject;
        this.seed = seed;
        this.trees = new ArrayList<>();
        this.getHeight = getHeight;
    }

    /**
     * Creates trees within a specified horizontal range.
     *
     * @param minX The minimum x-coordinate of the range.
     * @param maxX The maximum x-coordinate of the range.
     * @return A list of Tree objects generated within the range.
     */
    public ArrayList<Tree> createInRange(int minX, int maxX) {
        for (int i = (minX / Block.getBlockSize()) * Block.getBlockSize(); i < maxX ; i += Block.getBlockSize()) {
            Random random = new Random(Objects.hash(i,seed));
            float rand = random.nextInt(TREES_RANDOMNESS);

            // probability of 0.2 for a tree to be in a given column
            if (rand <= TREE_PERCENT){
                Tree tree = new Tree(i, getHeight,addGameObject,removeGameObject,random);
                i += Block.getBlockSize() * 2;
                trees.add(tree);
            }
        }
        return trees;
    }
}
