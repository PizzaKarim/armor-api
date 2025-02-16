package net.hmsvr.bukkit.armor_api.listener;

import net.hmsvr.bukkit.armor_api.ArmorAPI;
import net.hmsvr.bukkit.armor_api.ArmorSlot;
import net.hmsvr.bukkit.armor_api.event.ArmorEquipEvent;
import net.hmsvr.bukkit.armor_api.event.ArmorRemoveEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.Dispenser;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * The {@link ArmorListener} for Minecraft versions 1.8 and above.
 */
public class ArmorListener_1_8 extends ArmorListener {

    protected static Constructor<?> BB_CONSTRUCTOR;
    protected static Field ENTITY_HANDLE;
    protected static Field ENTITY_BB;
    protected static Method BB_OVERLAPS;

    static {
        try {
            Class<?> ENTITY = Class.forName("net.minecraft.server." + ArmorAPI.packageVersion() + ".Entity");
            Class<?> AXIS_ALIGNED_BB = Class.forName("net.minecraft.server." + ArmorAPI.packageVersion() + ".AxisAlignedBB");
            BB_CONSTRUCTOR = AXIS_ALIGNED_BB.getDeclaredConstructor(double.class, double.class, double.class, double.class, double.class, double.class);
            for (Method method : AXIS_ALIGNED_BB.getDeclaredMethods()) {
                if (method.getReturnType() != boolean.class) continue;
                if (!Arrays.equals(method.getParameterTypes(), new Class[] { AXIS_ALIGNED_BB })) continue;
                method.setAccessible(true);
                BB_OVERLAPS = method;
                break;
            }
            Class<?> CRAFT_ENTITY = Class.forName("org.bukkit.craftbukkit." + ArmorAPI.packageVersion() + ".entity.CraftEntity");
            for (Field field : CRAFT_ENTITY.getDeclaredFields()) {
                if (field.getType() != ENTITY) continue;
                field.setAccessible(true);
                ENTITY_HANDLE = field;
                break;
            }
            for (Field field : ENTITY.getDeclaredFields()) {
                if (field.getType() != AXIS_ALIGNED_BB) continue;
                if (Modifier.isStatic(field.getModifiers())) continue;
                field.setAccessible(true);
                ENTITY_BB = field;
                break;
            }
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {}
    }

    protected ArmorListener_1_8(ArmorAPI plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    protected void onClick(InventoryClickEvent event) {
        if (event.getClick() == ClickType.CREATIVE) {
            // Not supported
            return;
        }
        Player player = (Player) event.getWhoClicked();
        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();
        PluginManager pm = Bukkit.getPluginManager();

        boolean hasCurrent = current != null && current.getType() != Material.AIR;
        boolean hasCursor = cursor != null && cursor.getType() != Material.AIR;

        ItemStack removed = null;
        ItemStack equipped = null;
        ArmorRemoveEvent.Cause removeCause = null;
        ArmorEquipEvent.Cause equipCause = null;

        if (event.getAction() == InventoryAction.HOTBAR_SWAP) {
            if (event.getSlotType() != InventoryType.SlotType.ARMOR) return;
            if (hasCurrent) {
                removed = current;
                removeCause = ArmorRemoveEvent.Cause.HOTBAR_SWAP;
            }
            equipped = player.getInventory().getItem(event.getHotbarButton());
            equipCause = ArmorEquipEvent.Cause.HOTBAR_SWAP;
        } else if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
            if (event.isShiftClick() && hasCurrent) {
                removed = current;
                removeCause = ArmorRemoveEvent.Cause.SHIFT_CLICK;
            } else {
                if (hasCurrent) {
                    removed = current;
                    removeCause = ArmorRemoveEvent.Cause.CURSOR_PICKUP;
                }
                if (hasCursor) {
                    equipped = cursor;
                    equipCause = ArmorEquipEvent.Cause.CURSOR_PUT;
                }
            }
        } else if (event.isShiftClick() && event.getInventory().getType() == InventoryType.CRAFTING) {
            if (current == null) return; // Shouldn't ever be null; check to avoid warnings
            ArmorSlot slot = ArmorSlot.getSlot(current.getType());
            if (slot == null || slot.getItem(player) != null) return;
            equipped = current;
            equipCause = ArmorEquipEvent.Cause.SHIFT_CLICK;
        }

        if (removed != null) {
            ArmorSlot slot = ArmorSlot.getSlot(removed.getType());
            if (slot == null) return; // Shouldn't ever be null; check to avoid warnings
            ArmorRemoveEvent remove = new ArmorRemoveEvent(player, slot, removed, removeCause, event);
            pm.callEvent(remove);
            if (remove.isCancelled()) {
                event.setCancelled(true);
                return;
            }
            // TODO: Set item (cursor or current; I need to figure this out)
        }
        if (equipped != null) {
            ArmorSlot slot = ArmorSlot.getSlot(equipped.getType());
            if (slot == null) return; // Shouldn't ever be null; check to avoid warnings
            ArmorEquipEvent equip = new ArmorEquipEvent(player, slot, equipped, equipCause, event);
            pm.callEvent(equip);
            if (equip.isCancelled()) {
                event.setCancelled(true);
                return;
            }
            // TODO: Set item (cursor or current; I need to figure this out)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    protected void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getType() != InventoryType.CRAFTING) return;
        Player player = (Player) event.getWhoClicked();
        Map<Integer, ItemStack> items = event.getNewItems();
        List<ItemStack> armor = Arrays.asList(items.get(5), items.get(6), items.get(7), items.get(8));
        ArmorSlot[] slots = ArmorSlot.values();
        PluginManager pm = Bukkit.getPluginManager();
        for (int i = 0; i < armor.size(); i++) {
            ItemStack item = armor.get(i);
            if (item == null) continue;
            ArmorEquipEvent equip = new ArmorEquipEvent(player, slots[i], item, ArmorEquipEvent.Cause.DRAG_CURSOR, event);
            pm.callEvent(equip);
            if (equip.isCancelled()) {
                event.setCancelled(true);
                break; // TODO: Call other equips and set items manually, so you can cancel specific items
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    protected void onBreak(PlayerItemBreakEvent event) {
        ArmorSlot slot = ArmorSlot.getSlot(event.getBrokenItem().getType());
        if (slot == null) return;
        Player player = event.getPlayer();
        ArmorRemoveEvent remove = new ArmorRemoveEvent(player, slot, event.getBrokenItem(), ArmorRemoveEvent.Cause.BREAK, event);
        Bukkit.getPluginManager().callEvent(remove);
        if (!remove.isCancelled()) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                PlayerInventory inventory = player.getInventory();
                ItemStack item = event.getBrokenItem();
                item.setAmount(1);
                item.setDurability(item.getType().getMaxDurability());
                switch (slot) {
                    case HEAD:
                        inventory.setHelmet(item);
                        break;
                    case CHEST:
                        inventory.setChestplate(item);
                        break;
                    case LEGS:
                        inventory.setLeggings(item);
                        break;
                    case FEET:
                        inventory.setBoots(item);
                        break;
                }
            }
        }.runTask(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    protected void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Boolean keepInventory = player.getWorld().getGameRuleValue(GameRule.KEEP_INVENTORY);
        if (event.getKeepInventory() || Boolean.TRUE.equals(keepInventory)) return;
        PlayerInventory inventory = player.getInventory();
        PluginManager pm = Bukkit.getPluginManager();
        ItemStack item;
        if ((item = inventory.getHelmet()) != null) {
            ArmorRemoveEvent remove = new ArmorRemoveEvent(player, ArmorSlot.HEAD, item, ArmorRemoveEvent.Cause.DEATH, event);
            pm.callEvent(remove);
        }
        if ((item = inventory.getChestplate()) != null) {
            ArmorRemoveEvent remove = new ArmorRemoveEvent(player, ArmorSlot.CHEST, item, ArmorRemoveEvent.Cause.DEATH, event);
            pm.callEvent(remove);
        }
        if ((item = inventory.getLeggings()) != null) {
            ArmorRemoveEvent remove = new ArmorRemoveEvent(player, ArmorSlot.LEGS, item, ArmorRemoveEvent.Cause.DEATH, event);
            pm.callEvent(remove);
        }
        if ((item = inventory.getBoots()) != null) {
            ArmorRemoveEvent remove = new ArmorRemoveEvent(player, ArmorSlot.FEET, item, ArmorRemoveEvent.Cause.DEATH, event);
            pm.callEvent(remove);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    protected void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!event.hasItem() || event.getItem() == null) return; // Redundant check to avoid warnings
        if (event.useInteractedBlock() == Event.Result.ALLOW && !event.getPlayer().isSneaking()) {
            if (event.getClickedBlock() != null && event.getClickedBlock().getType().isInteractable()) return;
        }
        ItemStack item = event.getItem();
        ArmorSlot slot = ArmorSlot.getSlot(item.getType());
        if (slot == null || !slot.hasInteract(item.getType())) return;
        PlayerInventory inventory = event.getPlayer().getInventory();
        ItemStack occupied = null;
        switch (slot) {
            case HEAD:
                occupied = inventory.getHelmet();
                break;
            case CHEST:
                occupied = inventory.getChestplate();
                break;
            case LEGS:
                occupied = inventory.getLeggings();
                break;
            case FEET:
                occupied = inventory.getBoots();
                break;
        }
        ArmorEquipEvent.Cause cause = ArmorEquipEvent.Cause.INTERACT;
        if (occupied != null) {
            if (!ArmorAPI.serverVersion().above("1.19.4")) return;
            ArmorRemoveEvent remove = new ArmorRemoveEvent(event.getPlayer(), slot, occupied, ArmorRemoveEvent.Cause.INTERACT_SWAP, event);
            Bukkit.getPluginManager().callEvent(remove);
            if (remove.isCancelled()) {
                event.setUseItemInHand(Event.Result.DENY);
                return;
            }
            cause = ArmorEquipEvent.Cause.INTERACT_SWAP;
        }
        ArmorEquipEvent equip = new ArmorEquipEvent(event.getPlayer(), slot, item, cause, event);
        Bukkit.getPluginManager().callEvent(equip);
        if (equip.isCancelled()) {
            event.setUseItemInHand(Event.Result.DENY);
            return;
        }
        // TODO: onInteract(PlayerInteractEvent) | Set item
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    protected void onDispense(BlockDispenseEvent event) {
        Dispenser dispenser = (Dispenser) event.getBlock().getState().getData();
        Block block = event.getBlock().getRelative(dispenser.getFacing());
        List<LivingEntity> nearby = getNearbyEntities(block, event.getItem());
        LivingEntity entity = nearby.get(0);
        // TODO: Works on entities besides the player; could make custom BlockDispenseArmorEvent that works before 1.13.1
        if (!(entity instanceof Player)) return;
        ArmorSlot slot = ArmorSlot.getSlot(event.getItem().getType());
        if (slot == null) return; // Shouldn't ever be null; check to avoid warnings
        ArmorEquipEvent equip = new ArmorEquipEvent((Player) entity, slot, event.getItem(), ArmorEquipEvent.Cause.DISPENSER, event);
        Bukkit.getPluginManager().callEvent(equip);
        if (equip.isCancelled()) {
            event.setCancelled(true);
            return;
        }
        event.setItem(equip.getItem());
    }

    protected List<LivingEntity> getNearbyEntities(Block block, ItemStack item) {
        List<LivingEntity> nearby = new ArrayList<>();
        World world = block.getWorld();
        Object bb;
        try {
            bb = BB_CONSTRUCTOR.newInstance((double) block.getX(), (double) block.getY(), (double) block.getZ(), (double) block.getX() + 1, (double) block.getY() + 1, (double) block.getZ() + 1);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        EntitySelectorEquipable equipable = new EntitySelectorEquipable(item);
        int minX = floor((block.getX() - 2.0d) / 16.0d);
        int maxX = f(((block.getX() + 1) - 2.0d) / 16.0d);
        int minZ = floor((block.getZ() - 2.0d) / 16.0d);
        int maxZ = f(((block.getZ() + 1) - 2.0d) / 16.0d);
        for (int i = minX; i < maxX; i++) {
            for (int j = minZ; j < maxZ; j++) {
                if (!world.isChunkLoaded(i, j)) continue;
                Chunk chunk = world.getChunkAt(i, j);
                Entity[] entities = chunk.getEntities();
                int minY = floor((block.getY() - 2.0d) / 16.0d);
                int maxY = floor(((block.getY() + 1) + 2.0d) / 16.0d);
                minY = minY < 0 ? 0 : Math.min(minY, 15);
                maxY = maxY < 0 ? 0 : Math.min(maxY, 15);
                for (int k = minY; k <= maxY; k++) {
                    for (Entity entity : entities) {
                        int ek = floor(entity.getLocation().getY() / 16.0d);
                        if (ek < 0) ek = 0;
                        else if (ek > 15) ek = 15;
                        if (ek != k) continue;
                        try {
                            Object ebb;
                            Object e = ENTITY_HANDLE.get(entity);
                            ebb = ENTITY_BB.get(e);
                            boolean overlaps = (boolean) BB_OVERLAPS.invoke(ebb, bb);
                            if (!overlaps) continue;
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                        if (!equipable.test(entity)) continue;
                        nearby.add((LivingEntity) entity);
                    }
                }
            }
        }
        return nearby;
    }

    // TODO: Simplify; int cast of double value is never higher
    //       than the original value as the fractional part is truncated
    //       Could be replaced with a simple right shift by 4 (x >> 4)
    protected final int floor(double value) {
        // 5.3 < 5.0 ? 4 : 5
        // 0.3 < 0.0 ? -1 : 0
        int i = (int) value;
        return value < (double) i ? i - 1 : i;
    }

    // TODO: Refactor (& rename)
    protected final int f(double value) {
        // 5.3 > 5.0 ? 6 : 5
        // 0.3 > 0.0 ? 1 : 0
        int i = (int) value;
        return value > (double) i ? i + 1 : i;
    }

    protected static class EntitySelectorEquipable implements Predicate<Entity> {

        private final ItemStack item;

        protected EntitySelectorEquipable(ItemStack item) {
            this.item = item;
        }

        @Override
        public boolean test(Entity entity) {
            if (entity.isDead()) return false;
            if (!(entity instanceof LivingEntity)) return false;
            // TODO: Only supports armor for now; could add other slots in the future (EquipmentAPI?)
            ArmorSlot slot = ArmorSlot.getSlot(item.getType());
            if (slot == null) return false;
            ItemStack item = slot.getItem((LivingEntity) entity);
            return item == null || item.getType().name().equals("AIR") || item.getType().name().endsWith("_AIR");
        }
    }
}
