package fr.quatrevieux.araknemu.game.exploration.interaction;

import fr.quatrevieux.araknemu.game.exploration.interaction.action.Action;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionQueue;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.BlockingAction;

/**
 * Handle exploration player interactions
 */
final public class InteractionHandler {
    final private ActionQueue gameActions = new ActionQueue();

    private Interaction current;

    /**
     * Check if the player is interacting
     */
    public boolean interacting() {
        return current != null;
    }

    /**
     * Check if the player is busy
     */
    public boolean busy() {
        return interacting() || gameActions.isBusy();
    }

    /**
     * Stop all interactions
     */
    public void stop() {
        if (current != null) {
            current.stop();
            current = null;
        }

        gameActions.stop();
    }

    /**
     * Start the interaction
     */
    public void start(Interaction interaction) {
        if (busy()) {
            throw new IllegalStateException("Player is busy");
        }

        current = interaction.start();
    }

    /**
     * Get the current interaction
     *
     * @param interaction The interaction type
     */
    public <T extends Interaction> T get(Class<T> interaction) {
        if (current == null || !interaction.isInstance(current)) {
            throw new IllegalArgumentException("Invalid interaction type");
        }

        return (T) current;
    }

    /**
     * Remove the current interaction
     */
    public Interaction remove() {
        if (current == null) {
            throw new IllegalStateException("No interaction found");
        }

        Interaction interaction = current;
        current = null;

        return interaction;
    }

    /**
     * Push the action to the queue, and start it if not busy
     */
    public void push(Action action) throws Exception {
        if (interacting() && action instanceof BlockingAction) {
            throw new IllegalStateException("Cannot start blocking action when interacting");
        }

        gameActions.push(action);
    }

    /**
     * End an action which is successfully terminated
     *
     * @param actionId The action to end
     */
    public void end(int actionId) throws Exception {
        gameActions.end(actionId);
    }

    /**
     * Cancel an action in the queue
     *
     * @param actionId Action to cancel
     * @param argument The cancel argument
     *
     * @throws Exception
     */
    public void cancel(int actionId, String argument) throws Exception {
        gameActions.cancel(actionId, argument);
    }
}