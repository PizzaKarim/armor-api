package net.hmsvr.bukkit.armor_api;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An enum for the different armor slots in a player inventory.
 */
public enum ArmorSlot {

    /**
     * The head slot, primarily used for helmets.
     */
    HEAD,

    /**
     * The chest slot, primarily used for chestplates.
     */
    CHEST,

    /**
     * The legs slot, primarily used for leggings.
     */
    LEGS,

    /**
     * The feet slot, primarily used for boots.
     */
    FEET;

    /**
     * Gets the currently equipped item in this slot for the specified entity.
     * @param entity the entity
     * @return the currently equipped item, or null if none is equipped.
     */
    public @Nullable ItemStack getItem(@NotNull LivingEntity entity) {
        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) return null;
        switch (this) {
            case HEAD: return equipment.getHelmet();
            case CHEST: return equipment.getChestplate();
            case LEGS: return equipment.getLeggings();
            case FEET: return equipment.getBoots();
        }
        return null;
    }

    /**
     * Tests if the specified material can be equipped in this slot using the interact button (right-click by default).
     * @param material the material
     * @return true if this item can be equipped using the interact button, otherwise false.
     */
    public boolean hasInteract(@NotNull Material material) {
        String name = material.name();
        switch (this) {
            case HEAD: return name.endsWith("_HELMET");
            case CHEST: return name.endsWith("_CHESTPLATE");
            case LEGS: return name.endsWith("_LEGGINGS");
            case FEET: return name.endsWith("_BOOTS");
        }
        return false;
    }

    /**
     * Gets the armor slot this item material can be equipped into.
     * @param material the material
     * @return the armor slot, or null if no slot suits this item material.
     */
    public static @Nullable ArmorSlot getSlot(@NotNull Material material) {
        String name = material.name();
        if (name.endsWith("_HELMET") || name.endsWith("_SKULL") || name.endsWith("CARVED_PUMPKIN") || name.endsWith("SKULL_ITEM")) return HEAD;
        if (name.endsWith("_CHESTPLATE") || name.endsWith("ELYTRA")) return CHEST;
        if (name.endsWith("_LEGGINGS")) return LEGS;
        if (name.endsWith("_BOOTS")) return FEET;
        return null;
    }
}
