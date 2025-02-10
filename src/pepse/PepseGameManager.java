package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;

import danogl.gui.rendering.Camera;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import pepse.world.*;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Flora;
import pepse.world.trees.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static pepse.util.Constants.*;

/**
 * PepseGameManager class initializes and manages the components of the game.
 * It defines game elements such as terrain, avatar, flora, and environmental effects.
 *
 * @author fanteo12
 */
public class PepseGameManager extends GameManager {

    // constants
    private static final float DAY_NIGHT_CYCLE = 30;
    //check this field
    private static final int START_SEED = 10;
    private static final int END_SEED = 20;
    private static final float ENERGY_X = 10;
    private static final float ENERGY_Y = 10;
    private static final float ENERGY_PANEL_SIZE = 30;
    private static final String RENDERABLE_STRING = "";
    private static final float AVATAR_SIZE_RATIO = 2;
    private static final float CAMERA_RATIO = 0.5f;
    private static final int CREATE_IN_RANGE = 1536/2;

    // fields
    private Vector2 windowDimensions;
    private float initialGroundHeight;
    private int minXCreated;
    private int maxXCreated;
    private int seed;
    private Terrain terrain;
    private Avatar avatar;
    private Flora flora;
    private ArrayList<Tree> trees;
    private Cloud cloud;

    /**
     * Initializes the game, setting up all game elements and their relationships.
     *
     * @param imageReader Reader for loading images.
     * @param soundReader Reader for loading sounds.
     * @param inputListener Listener for user input.
     * @param windowController Controller for managing the game window.
     */
        @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        // Initialize window dimensions and parameters
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        windowDimensions = windowController.getWindowDimensions();
        initialGroundHeight = windowDimensions.y() * GROUND_RATIO_NUMERATOR/GROUND_RATIO_DENOMINATOR;
        minXCreated = - CREATE_IN_RANGE;
        maxXCreated = (int)windowDimensions.x() + CREATE_IN_RANGE;
        seed = new Random().nextInt(START_SEED,END_SEED);

        // create sky
        createSky();

        // create terrain
        createTerrain();

        // night
        createNight();

        // sun and halo
        createSunAndHalo();

        // avatar
        createAvatar(inputListener,imageReader);

        // energy
        createEnergy();
        
        // flora
        createFlora(minXCreated,maxXCreated);

        // cloud
        createCloud();

        // camera
        setCamera();

        }

    /**
     * Set up camera following the avatar
     */
    private void setCamera() {
        Vector2 initialAvatarLocation = new Vector2(windowDimensions.x()
                * SCREEN_RATIO - (AVATAR_SIZE / AVATAR_SIZE_RATIO),
                initialGroundHeight - AVATAR_SIZE);
        setCamera(new Camera(avatar,
                windowDimensions.mult(CAMERA_RATIO).subtract(initialAvatarLocation),
                windowDimensions,
                windowDimensions));
    }

    /**
     * Creates and adds clouds to the game environment.
     */
    private void createCloud() {
            cloud = new Cloud(windowDimensions,this::addGameObject,this::removeGameObjects);
            avatar.addObserver(cloud);
    }

    /**
     * creates flora, including trees and other vegetation, to the game.
     */
    private void createFlora(int minXCreated, int maxXCreated) {
        flora = new Flora(terrain::groundHeightAt,this::addGameObject,this::removeGameObjects,seed);
        createTrees(minXCreated,maxXCreated);
    }

    /**
     * Generates and adds trees to the game in the specified range.
     */
    private void createTrees(int minX, int maxX) {
        trees = flora.createInRange(minX,maxX);
        for (Tree tree : trees){
            gameObjects().addGameObject(tree.getStump(),Layer.STATIC_OBJECTS);
            ArrayList<GameObject> leaves = tree.getLeaves();
            for (GameObject leaf : leaves){
                gameObjects().addGameObject(leaf,Layer.STATIC_OBJECTS);
            }
            ArrayList<GameObject> fruits = tree.getFruits();
            for (GameObject fruit : fruits){
                gameObjects().addGameObject(fruit,Layer.DEFAULT);
            }
        }
    }

    /**
     * Creates the energy representation element to the game.
     */
    private void createEnergy() {

        EnergyPanel panel = new EnergyPanel(new Vector2(ENERGY_X, ENERGY_Y),
                new Vector2(ENERGY_PANEL_SIZE,ENERGY_PANEL_SIZE),
                new TextRenderable(RENDERABLE_STRING),avatar::getEnergy);
        panel.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects().addGameObject(panel,Layer.UI);
    }

    /**
     * Creates and adds the avatar to the game.
     */
    private void createAvatar(UserInputListener inputListener, ImageReader imageReader) {
        float xCoordinate = windowDimensions.x()*SCREEN_RATIO;
        avatar = new Avatar(new Vector2(xCoordinate,windowDimensions.y()/2),inputListener,imageReader);
        gameObjects().addGameObject(avatar);
    }

    /**
     * Creates the sun and its halo to the game.
     */
    private void createSunAndHalo() {
        GameObject sun = Sun.create(windowDimensions,DAY_NIGHT_CYCLE);
        GameObject sunHalo = SunHalo.create(sun);
        gameObjects().addGameObject(sunHalo,Layer.BACKGROUND);
        gameObjects().addGameObject(sun,Layer.BACKGROUND);
    }

    /**
     * Creates night cycle to the game.
     */
    private void createNight() {
        GameObject night = Night.create(windowDimensions,DAY_NIGHT_CYCLE/2);
        gameObjects().addGameObject(night,Layer.UI);
    }

    /**
     * Creates and initializes the terrain.
     */
    private void createTerrain() {
        terrain = new Terrain(windowDimensions,seed);
        List<Block> ground = terrain.createInRange(minXCreated,maxXCreated);
        for(Block block : ground){
            // add first layer as static objects
            if (block.getTag().equals(GROUND_TAG)){
                gameObjects().addGameObject(block,Layer.STATIC_OBJECTS);
            }
            // add the rest as a background objects
            else {gameObjects().addGameObject(block,Layer.FOREGROUND);}
        }
    }

    /**
     * Creates and adds the sky background to the game.
     */
    private void createSky() {
        GameObject sky = Sky.create(windowDimensions);
        gameObjects().addGameObject(sky, Layer.BACKGROUND);
    }

    /**
     * Updates the game state and manages terrain and flora creation dynamically.
     *
     * @param deltaTime Time elapsed since the last update.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if(avatar.getCenter().x() > maxXCreated - CREATE_IN_RANGE){
            extendTerrain(maxXCreated, maxXCreated + CREATE_IN_RANGE);
            createFlora(maxXCreated,maxXCreated + CREATE_IN_RANGE);
            maxXCreated += CREATE_IN_RANGE;
            minXCreated += CREATE_IN_RANGE;
            removeGameObjects(true);
        }

        // Check if the avatar is near the left boundary
        if(avatar.getCenter().x() < minXCreated + CREATE_IN_RANGE) {
            extendTerrain(minXCreated - CREATE_IN_RANGE, minXCreated);
            createFlora(minXCreated - CREATE_IN_RANGE, minXCreated);
            maxXCreated -= CREATE_IN_RANGE;
            minXCreated -= CREATE_IN_RANGE;
            removeGameObjects(false);
        }
    }

    /**
     * Removes game objects outside the specified boundary.
     *
     * @param right If true, removes objects from the right boundary; otherwise from the left boundary.
     */
    private void removeGameObjects(boolean right) {
        if (right) {
            for (GameObject obj : gameObjects()){
                if (obj.getCenter().x() < minXCreated){
                    gameObjects().removeGameObject(obj,Layer.STATIC_OBJECTS);
                    gameObjects().removeGameObject(obj);
                }
            }
        } else {
            for (GameObject obj : gameObjects()){
                if (obj.getCenter().x() > maxXCreated){
                    gameObjects().removeGameObject(obj,Layer.STATIC_OBJECTS);
                    gameObjects().removeGameObject(obj);
                }
            }
        }
    }

    /**
     * Generates terrain blocks in the specified range.
     */
    private void extendTerrain(int newMinX, int newMaxX) {

        List<Block> ground = terrain.createInRange(newMinX,newMaxX);
        for (Block block : ground){
            gameObjects().addGameObject(block,Layer.STATIC_OBJECTS);
        }
    }


    /**
     * Removes a game object from the game.
     *
     * @param gameObject The game object to remove.
     */
    public void removeGameObjects(GameObject gameObject) {
        gameObjects().removeGameObject(gameObject);
    }

    /**
     * Adds a game object to a specified layer.
     *
     * @param gameObject The game object to add.
     * @param layer The layer to add the game object to.
     */
    public void addGameObject(GameObject gameObject, int layer) {
        gameObjects().addGameObject(gameObject,layer);
    }

    /**
     * Entry point for running the game.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args){
        new PepseGameManager().run();
    }
}
