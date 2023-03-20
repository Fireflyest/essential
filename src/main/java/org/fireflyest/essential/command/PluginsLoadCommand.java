package org.fireflyest.essential.command;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.UnknownDependencyException;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.data.Language;

public class PluginsLoadCommand extends SubCommand {

    @Override
    protected boolean execute(CommandSender sender) {
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        File file = new File(Essential.getPlugin().getDataFolder().getParentFile(), arg1);
        if (file.exists()) {
            try {
                pluginManager.loadPlugin(file);
                sender.sendMessage(Language.PLUGIN_LOAD + file.getName());
            } catch (UnknownDependencyException | InvalidPluginException | InvalidDescriptionException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
    
}
