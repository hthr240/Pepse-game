package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.util.Vector2;
import pepse.util.Constants;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * The Avatar class represents the player-controlled character in the game.
 * It supports movement, jumping, energy management, and interactions with other game objects.
 * It implements the Observer design pattern.
 *
 * @author fanteo12
 */
public class Avatar extends GameObject{

    // constants
    private static final String IDLE0_PATH = "assets/idle_0.png";
    private static final String IDLE1_PATH = "assets/idle_1.png";
    private static final String IDLE2_PATH = "assets/idle_2.png";
    private static final String IDLE3_PATH = "assets/idle_3.png";
    private static final String[] idlePaths = {IDLE0_PATH, IDLE1_PATH, IDLE2_PATH, IDLE3_PATH};
    private static final String RUN0_PATH = "assets/run_0.png";
    private static final String RUN1_PATH = "assets/run_1.png";
    private static final String RUN2_PATH = "assets/run_2.png";
    private static final String RUN3_PATH = "assets/run_3.png";
    private static final String RUN4_PATH = "assets/run_4.png";
    private static final String RUN5_PATH = "assets/run_5.png";
    private static final String[] runPaths = {RUN0_PATH, RUN1_PATH, RUN2_PATH, RUN3_PATH, RUN4_PATH, RUN5_PATH};
    private static final String JUMP0_PATH = "assets/jump_0.png";
    private static final String JUMP1_PATH = "assets/jump_1.png";
    private static final String JUMP2_PATH = "assets/jump_2.png";
    private static final String JUMP3_PATH = "assets/jump_3.png";
    private static final String[] jumpPaths = {JUMP0_PATH, JUMP1_PATH, JUMP2_PATH, JUMP3_PATH};
    private static final float GRAVITY = 800;
    private static final float VELOCITY_X = 350;
    private static final float VELOCITY_Y = -650;
    private static final float MAX_ENERGY = 100f;
    private static final float TIME_BETWEEN_CLIPS = 0.05f;
    private static final float ENERGY_RUN_REDUCE = 0.5f;
    private static final float ENERGY_JUMP_REDUCE = 10f;
    private static final float ENERGY_FRUIT_INCREASE = 10f;

    // fields
    private final UserInputListener inputListener;
    private final AnimationRenderable idleAnimation;
    private final AnimationRenderable runAnimation;
    private final AnimationRenderable jumpAnimation;
    private float energy;
    private static ArrayList<Observer> observerList;

    /**
     * Constructs an Avatar object with specified position, input listener, and image reader.
     *
     * @param topLeftCorner The initial position of the avatar.
     * @param inputListener Listener for user input.
     * @param imageReader Reader for loading animation images.
     */
    public Avatar(Vector2 topLeftCorner,
                  UserInputListener inputListener,
                  ImageReader imageReader){
        super(topLeftCorner,new Vector2(Constants.AVATAR_SIZE,Constants.AVATAR_SIZE), new AnimationRenderable(idlePaths,imageReader,true,0.07));

        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);

        this.inputListener = inputListener;
        this.idleAnimation = new AnimationRenderable(idlePaths,imageReader,true,TIME_BETWEEN_CLIPS);
        this.runAnimation = new AnimationRenderable(runPaths,imageReader,true,TIME_BETWEEN_CLIPS);
        this.jumpAnimation = new AnimationRenderable(jumpPaths,imageReader,true,TIME_BETWEEN_CLIPS);
        energy = MAX_ENERGY;
        observerList = new ArrayList<>();
        this.setTag(Constants.AVATAR_TAG);
    }

    /**
     * Gets the current energy level of the avatar.
     *
     * @return Current energy level.
     */
    public float getEnergy() {
        return energy;
    }

    /**
     * Adds energy to the avatar, ensuring it does not exceed the maximum allowed value.
     *
     * @param addedEnergy Amount of energy to add.
     */
    public void addEnergy(float addedEnergy){
        if (energy + addedEnergy > MAX_ENERGY){
            energy = MAX_ENERGY;
        }
        else {
            energy += addedEnergy;
        }
    }

    /**
     * Updates the avatar's state and handles input-driven actions.
     *
     * @param deltaTime Time elapsed since the last update.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xVel = 0;

        if(inputListener.isKeyPressed(KeyEvent.VK_LEFT) && energy >= ENERGY_RUN_REDUCE) {
            xVel -= VELOCITY_X;
            runLeft();
        }
        if(inputListener.isKeyPressed(KeyEvent.VK_RIGHT) && energy >= ENERGY_RUN_REDUCE) {
            xVel += VELOCITY_X;
            runRight();
        }
        transform().setVelocityX(xVel);

        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE) &&
                getVelocity().y() == 0 && energy >= ENERGY_JUMP_REDUCE) {
            jump();
        }
        else if (getVelocity().y() == 0 && getVelocity().x() == 0 && energy < MAX_ENERGY){
            rest();
        }
    }

    /**
     * Handles the avatar's resting behavior, regenerating energy.
     */
    private void rest() {
        energy ++;
        renderer().setRenderable(idleAnimation);
    }

    /**
     * Handles the avatar's jumping behavior.
     */
    private void jump() {
        transform().setVelocityY(VELOCITY_Y);
        energy -= ENERGY_JUMP_REDUCE;
        renderer().setRenderable(jumpAnimation);
        updateObservers();
    }

    /**
     * Handles the avatar's running behavior to the right.
     */
    private void runRight() {
        energy -= ENERGY_RUN_REDUCE;
        renderer().setRenderable(runAnimation);
        if (renderer().isFlippedHorizontally()){
            renderer().setIsFlippedHorizontally(false);
        }
    }

    /**
     * Handles the avatar's running behavior to the left.
     */
    private void runLeft() {
        energy -= ENERGY_RUN_REDUCE;
        renderer().setRenderable(runAnimation);
        renderer().setIsFlippedHorizontally(true);
    }

    /**
     * add an observer to monitor avatar events.
     *
     * @param observer The observer to register.
     */
    public void addObserver(Observer observer){
        observerList.add(observer);
    }

    /**
     * Notifies all registered observers about an update.
     */
    private void updateObservers(){
        for (Observer observer : observerList){
            observer.update();
        }
    }

    /**
     * Handles collision events involving the avatar.
     *
     * @param other The other GameObject involved in the collision.
     * @param collision Details about the collision.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if(other.getTag().equals(Constants.GROUND_TAG)){
            this.transform().setVelocityY(0);
        }

        if (other.getTag().equals(Constants.FRUIT_TAG)){
            addEnergy(ENERGY_FRUIT_INCREASE);
        }
    }
}
