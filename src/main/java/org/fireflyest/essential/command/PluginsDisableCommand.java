package org.fireflyest.essential.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.essential.data.Language;

public class PluginsDisableCommand extends SubCommand {

    @Override
    protected boolean execute(CommandSender sender) {
        return true;
    }
    
    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        Plugin plugin = pluginManager.getPlugin(arg1);
        pluginManager.disablePlugin(plugin);
        sender.sendMessage(Language.PLUGIN_DISABLE);
        return true;
    }

}
