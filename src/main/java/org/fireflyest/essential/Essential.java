package org.fireflyest.essential;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * essential
 */
public class Essential extends JavaPlugin {

    private static JavaPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;

        plugin.getLogger().info("");
    }

    @Override
    public void onDisable() {

    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }


}
