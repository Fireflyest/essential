package org.fireflyest.essential.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.fireflyest.craftcommand.argument.Argument;
import org.fireflyest.essential.Essential;

public class PluginsLoadArgument implements Argument {

    @Override
    public List<String> tab(CommandSender sender, String arg) {
        List<String> ret = new ArrayList<>();
        for (String file : Essential.getPlugin().getDataFolder().getParentFile().list()) {
            if (file.endsWith(".jar") && file.startsWith(arg)) {
                ret.add(file);
            }
        }
        return ret;
    }
    
}
