package org.fireflyest.essential.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.fireflyest.craftcommand.argument.Argument;

public class PluginsArgument implements Argument {

    @Override
    public List<String> tab(CommandSender sender, String arg) {
        List<String> ret = new ArrayList<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getName().startsWith(arg)) {
                ret.add(plugin.getName());
            }
        }
        return ret;
    }
    
}
