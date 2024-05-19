package org.fireflyest.essential.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.fireflyest.craftcommand.argument.Argument;

public class WorldTypeArgument implements Argument {

    @Override
    public List<String> tab(CommandSender sender, String arg) {
        List<String> ret = new ArrayList<>();
        for (WorldType type : WorldType.values()) {
            if (type.name().startsWith(arg)) {
                ret.add(type.name());
            }
        }
        return ret;
    }
    
}
