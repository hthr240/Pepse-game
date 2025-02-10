package pepse.world.trees;

import danogl.GameObject;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static pepse.util.Constants.LEAF_SIZE;

/**
 * Represents a tree object in the game world. A tree consists of a trunk, leaves, and fruits.
 * The tree is generated procedurally based on various parameters such as location and height.
 *
 * @author fanteo12
 */
public class Tree {

    // constants
    private static final Color TRUNK_COLOR = new Color(100, 50, 20);
    private static final int TRUNK_MIN_HEIGHT = 120;
    private static final int TRUNK_MAX_HEIGHT = 200;
    private static final int TRUNK_WIDTH = 20;
    private static final Color LEAF_COLOR = new Color(50,200,30);
    private static final int TOP_WIDTH = 70;
    private static final int LEAVES_FRUITS_RAND = 100;
    private static final float LEAF_CYCLE = 10f;
    private static final float RAND_TIME_FACTOR = 3;
    private static final int LEAVES_PERCENT = 75;
    private static final int FRUIT_PERCENT = 85;
    private static final float FRUIT_SIZE = 25;
    private static final Color RED_FRUIT = new Color(255,102,102);
    private static final Color BLUE_FRUIT = new Color(102,102,255);
    private static final int COLOR_DELTA = 50;

    // fields
    private final Function<Float, Float> getHeight;
    private final Random random;
    private final BiConsumer<GameObject, Integer> addGameObject;
    private final Consumer<GameObject> removeGameObject;
    private Trunk trunk;
    private final ArrayList<GameObject> leaves;
    private final ArrayList<GameObject> fruits;

    /**
     * Constructs a new Tree object.
     *
     * @param location The x-coordinate of the tree's base.
     * @param getHeight Function to determine the ground height at a specific location.
     * @param addGameObject Function to add objects to the game world.
     * @param removeGameObject Function to remove objects from the game world.
     * @param random Random object for procedural generation.
     */
    public Tree (float location, Function<Float,Float> getHeight, BiConsumer<GameObject,
            Integer> addGameObject, Consumer<GameObject> removeGameObject, Random random){
        this.getHeight = getHeight;
        this.addGameObject = addGameObject;
        this.removeGameObject = removeGameObject;
        this.random = random;
        this.leaves = new ArrayList<>();
        this.fruits = new ArrayList<>();
        createTrunk(location,getHeight);
        createTreeTop();
    }

    /**
     * creates the leafs and the fruits of the tree
     */
    public void createTreeTop(){
        for (float r =  trunk.getCenter().x() - TOP_WIDTH; r <= trunk.getCenter().x() + TOP_WIDTH; r += LEAF_SIZE){
            for (float c = trunk.getCenter().y() - Block.getBlockSize(); c >= trunk.getTopLeftCorner().y() - TOP_WIDTH; c -= LEAF_SIZE){
                int rand = random.nextInt(LEAVES_FRUITS_RAND);
                if (rand <= LEAVES_PERCENT && getHeight.apply(r) >= c){
                    createLeaf(r,c);
                }
                else if(rand <= FRUIT_PERCENT && getHeight.apply(r) >= c){
                    createFruit(r,c,rand);
                }
            }
        }


    }

    /**
     * Creates fruits for the tree based on leaf placement.
     */
    private void createFruit(float r, float c, int rand) {
        Color fruitColor;
        if (rand%2 == 0){
             fruitColor = RED_FRUIT;
        } else {
            fruitColor = BLUE_FRUIT;
        }
        GameObject fruit = new Fruit(new Vector2(r,c),
                            new Vector2(FRUIT_SIZE,FRUIT_SIZE),
                            new OvalRenderable(ColorSupplier.approximateColor(fruitColor,COLOR_DELTA)),
                            this.removeGameObject,
                            this.addGameObject);
        fruits.add(fruit);
    }

    /**
     * Creates a leaf at the specified coordinates.
     */
    private void createLeaf(float r, float c) {
        float randTime = random.nextFloat() * RAND_TIME_FACTOR;
        GameObject leaf = new Leaf(new Vector2(r,c),
                                new Vector2(LEAF_SIZE,LEAF_SIZE),
                                new RectangleRenderable(ColorSupplier.approximateColor(LEAF_COLOR,COLOR_DELTA)),
                                LEAF_CYCLE,
                                randTime);
        leaves.add(leaf);
    }

    /**
     * Creates the trunk of the tree.
     */
    private void createTrunk(float location, Function<Float,Float> getHeight){
        Renderable trunkImage = new RectangleRenderable(ColorSupplier.approximateColor(TRUNK_COLOR,COLOR_DELTA/2));
        float trunkHeight = random.nextInt(TRUNK_MIN_HEIGHT,TRUNK_MAX_HEIGHT);
        Vector2 trunkDimensions = new Vector2(TRUNK_WIDTH,trunkHeight);
        this.trunk = new Trunk(Vector2.ZERO,trunkDimensions,trunkImage);
        trunk.setTopLeftCorner(new Vector2(location,getHeight.apply(location) - trunkHeight));
    }

    /**
     * Returns the tree's stump.
     */
    public Trunk getStump() {
        return trunk;
    }

    /**
     * Returns the list of leaves.
     */
    public ArrayList<GameObject> getLeaves() {
        return leaves;
    }

    /**
     * Returns the list of fruits.
     */
    public ArrayList<GameObject> getFruits() {
        return fruits;
    }
}
