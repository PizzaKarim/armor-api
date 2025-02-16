package net.hmsvr.bukkit.armor_api;

import net.hmsvr.bukkit.armor_api.listener.ArmorListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * An API for handling armor-related events.
 * The primary purpose of this API is to provide events for easy state management.
 * @see net.hmsvr.bukkit.armor_api.event.ArmorEquipEvent ArmorEquipEvent
 * @see net.hmsvr.bukkit.armor_api.event.ArmorRemoveEvent ArmorRemoveEvent
 */
public final class ArmorAPI extends JavaPlugin implements Listener {

    private static final String PACKAGE_VERSION = Bukkit.getServer().getClass().getName().split("\\.")[3];
    private static final SemanticVersion SERVER_VERSION = new SemanticVersion(Bukkit.getBukkitVersion().split("-")[0]);

    @Override
    public void onEnable() {
        //getLogger().info("Server version : " + serverVersion()); // DEBUG
        //getLogger().info("Package version: " + packageVersion()); // DEBUG
        ArmorListener.register(this);
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    /**
     * Gets the server software version.
     * @return the server version (e.g., <code>1.13.2</code>)
     */
    public static @NotNull SemanticVersion serverVersion() {
        return SERVER_VERSION;
    }

    /**
     * Gets the package name for the version-specific implementation.
     * @return the package name (e.g., <code>v1_13_R2</code>)
     */
    public static @NotNull String packageVersion() {
        return PACKAGE_VERSION;
    }
}
