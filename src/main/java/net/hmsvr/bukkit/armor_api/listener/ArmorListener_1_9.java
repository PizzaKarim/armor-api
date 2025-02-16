package net.hmsvr.bukkit.armor_api.listener;

import net.hmsvr.bukkit.armor_api.ArmorAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;

/**
 * The {@link ArmorListener} for Minecraft versions 1.9 and above.
 */
public class ArmorListener_1_9 extends ArmorListener_1_8 {

    protected ArmorListener_1_9(ArmorAPI plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    protected void onClick(InventoryClickEvent event) {
        super.onClick(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    protected void onDrag(InventoryDragEvent event) {
        super.onDrag(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    protected void onBreak(PlayerItemBreakEvent event) {
        super.onBreak(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    protected void onDeath(PlayerDeathEvent event) {
        super.onDeath(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    protected void onInteract(PlayerInteractEvent event) {
        super.onInteract(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    protected void onDispense(BlockDispenseEvent event) {
        super.onDispense(event);
    }
}
