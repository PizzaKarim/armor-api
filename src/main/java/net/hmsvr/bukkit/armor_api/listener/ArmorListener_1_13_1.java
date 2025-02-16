package net.hmsvr.bukkit.armor_api.listener;

import net.hmsvr.bukkit.armor_api.ArmorAPI;
import net.hmsvr.bukkit.armor_api.event.ArmorEquipEvent;
import net.hmsvr.bukkit.armor_api.ArmorSlot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;

/**
 * The {@link ArmorListener} for Minecraft versions 1.13.1 and above.
 */
public class ArmorListener_1_13_1 extends ArmorListener_1_9 {

    protected ArmorListener_1_13_1(ArmorAPI plugin) {
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
        return;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    protected void onDispense(BlockDispenseArmorEvent event) {
        if (!(event.getTargetEntity() instanceof Player)) return;
        ArmorSlot slot = ArmorSlot.getSlot(event.getItem().getType());
        if (slot == null) return;
        ArmorEquipEvent equip = new ArmorEquipEvent((Player) event.getTargetEntity(), slot, event.getItem(), ArmorEquipEvent.Cause.DISPENSER, event);
        Bukkit.getPluginManager().callEvent(equip);
        if (equip.isCancelled()) {
            event.setCancelled(true);
            return;
        }
        event.setItem(equip.getItem());
    }
}
