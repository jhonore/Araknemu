package fr.quatrevieux.araknemu.game.item.inventory;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectQuantityChanged;

import java.util.List;

/**
 * Base implementation for inventory item entry
 */
abstract public class AbstractItemEntry implements ItemEntry {
    final private ItemStorage storage;
    final private fr.quatrevieux.araknemu.data.living.entity.Item entity;
    final private Item item;
    final private Dispatcher dispatcher;

    public AbstractItemEntry(ItemStorage<? extends AbstractItemEntry> storage, fr.quatrevieux.araknemu.data.living.entity.Item entity, Item item, Dispatcher dispatcher) {
        this.storage = storage;
        this.entity = entity;
        this.item = item;
        this.dispatcher = dispatcher;
    }

    @Override
    final public int id() {
        return entity.entryId();
    }

    @Override
    final public Item item() {
        return item;
    }

    @Override
    final public int quantity() {
        return entity.quantity();
    }

    @Override
    final public void add(int quantity) {
        if (quantity <= 0) {
            throw new InventoryException("Invalid quantity given");
        }

        changeQuantity(quantity() + quantity);
    }

    @SuppressWarnings("unchecked")
    @Override
    final public void remove(int quantity) throws InventoryException {
        if (quantity > quantity() || quantity <= 0) {
            throw new InventoryException("Invalid quantity given");
        }

        if (quantity == quantity()) {
            entity.setQuantity(0);
            storage.delete(this);
            return;
        }

        changeQuantity(quantity() - quantity);
    }

    @Override
    final public List<ItemTemplateEffectEntry> effects() {
        return entity.effects();
    }

    @Override
    final public int templateId() {
        return entity.itemTemplateId();
    }

    private void changeQuantity(int quantity) {
        entity.setQuantity(quantity);
        dispatcher.dispatch(new ObjectQuantityChanged(this));
    }
}
