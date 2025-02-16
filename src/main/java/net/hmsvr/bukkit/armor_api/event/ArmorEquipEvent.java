package net.hmsvr.bukkit.armor_api.event;

import net.hmsvr.bukkit.armor_api.ArmorSlot;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * This {@link Event} is called whenever a {@link Player} equips an item in an armor slot.
 */
public class ArmorEquipEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    protected final ArmorSlot slot;
    protected final Cause cause;
    protected final Event causeEvent;

    protected ItemStack item;
    protected boolean cancelled;

    /**
     * Initializes a new ArmorEquipEvent object to represent a player equipping a piece of armor.
     * @param who the player
     * @param slot the armor slot
     * @param item the equipped item
     * @param cause the cause for this event
     * @param causeEvent the {@link Event} that caused the item to be equipped
     */
    public ArmorEquipEvent(@NotNull Player who, @NotNull ArmorSlot slot, @NotNull ItemStack item, @NotNull Cause cause, @NotNull Event causeEvent) {
        super(who);
        this.slot = slot;
        this.item = item;
        this.cause = cause;
        this.causeEvent = causeEvent;
    }

    /**
     * Gets the equipped item.
     * @return the equipped item
     */
    public final @NotNull ItemStack getItem() {
        return item;
    }

    /**
     * Sets the equipped item.
     * @param item the new item
     */
    public final void setItem(@NotNull ItemStack item) {
        this.item = item;
    }

    /**
     * Gets the armor slot for which the item was equipped into.
     * @return the armor slot
     */
    public final @NotNull ArmorSlot getSlot() {
        return slot;
    }

    /**
     * Gets the cause for this item to be equipped.
     * @return the cause
     */
    public final @NotNull Cause getCause() {
        return cause;
    }

    /**
     * Gets the event that caused this item to be equipped.
     * @return the cause event
     */
    public final @NotNull Event getCauseEvent() {
        return causeEvent;
    }

    /**
     * Gets the cancellation state of this event.
     * @return true if this event has been cancelled, otherwise false.
     */
    @Override
    public final boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancellation state of this event.
     * @param cancel whether to cancel this event or not.
     */
    @Override
    public final void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public final @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * An enum for causes that could equip an item.
     */
    public enum Cause {

        /**
         * The item was equipped by interaction (right-click by default).
         */
        INTERACT,

        /**
         * The item was equipped by a nearby dispenser.
         */
        DISPENSER,

        /**
         * The item was put in the armor slot using the cursor in the player inventory.
         */
        CURSOR_PUT,

        /**
         * The item was swapped into the armor slot using the hotbar buttons (1-9) in the player inventory.
         */
        HOTBAR_SWAP,

        /**
         * The item was shift-clicked into the armor slot in the player inventory.
         */
        SHIFT_CLICK,

        /**
         * The item was dragged into the armor slot by holding down the mouse button in the player inventory.
         */
        DRAG_CURSOR,

        /**
         * The item was swapped into the armor slot using interaction from Minecraft versions 1.19.4 and above.
         */
        INTERACT_SWAP
    }
}
