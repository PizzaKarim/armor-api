package net.hmsvr.bukkit.armor_api.listener;

import net.hmsvr.bukkit.armor_api.ArmorAPI;
import net.hmsvr.bukkit.armor_api.SemanticVersion;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

/**
 * An abstract class inherited by listeners handling version-specific armor-related behaviour.
 */
public abstract class ArmorListener implements Listener {

    protected final ArmorAPI plugin;

    protected ArmorListener(ArmorAPI plugin) {
        this.plugin = plugin;
    }

    /**
     * Registers the {@link Listener} appropriate for this server based on its version.
     * @param plugin the {@link org.bukkit.plugin.Plugin} instance to register the listener for.
     */
    public static void register(ArmorAPI plugin) {
        PluginManager pm = Bukkit.getPluginManager();
        SemanticVersion version = ArmorAPI.serverVersion();
        if (version.above("1.13.1")) {
            //plugin.getLogger().info("Registered listener: ArmorListener_1_13_1"); // DEBUG
            pm.registerEvents(new ArmorListener_1_13_1(plugin), plugin);
        } else if (version.above("1.9.0")) {
            //plugin.getLogger().info("Registered listener: ArmorListener_1_9"); // DEBUG
            pm.registerEvents(new ArmorListener_1_9(plugin), plugin);
        } else if (version.above("1.8.0")) {
            //plugin.getLogger().info("Registered listener: ArmorListener_1_8"); // DEBUG
            pm.registerEvents(new ArmorListener_1_8(plugin), plugin);
        } else {
            throw new IllegalStateException("Unsupported server version: " + version);
        }
    }
}
