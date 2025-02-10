package pepse.world;

/**
 * The Observer interface defines a contract for objects that wish to observe
 * and respond to updates from the Avatar.
 * Implementing classes must provide the behavior for the update method, which
 * will be called whenever the Avatar performs an action that triggers observer notifications.
 *
 * @author fanteo12
 */
public interface Observer {

    /**
     * This method is called when the observed Avatar triggers an update event.
     */
    void update();
}
