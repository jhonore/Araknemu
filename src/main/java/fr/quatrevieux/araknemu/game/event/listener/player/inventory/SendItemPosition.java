package fr.quatrevieux.araknemu.game.event.listener.player.inventory;

import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.inventory.ObjectMoved;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.object.ItemPosition;

/**
 * Send the new object position
 */
final public class SendItemPosition implements Listener<ObjectMoved> {
    final private GamePlayer player;

    public SendItemPosition(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(ObjectMoved event) {
        player.send(
            new ItemPosition(event.entry())
        );
    }

    @Override
    public Class<ObjectMoved> event() {
        return ObjectMoved.class;
    }
}