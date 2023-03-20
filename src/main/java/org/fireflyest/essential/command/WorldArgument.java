package org.fireflyest.essential.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.fireflyest.craftcommand.argument.Argument;

public class WorldArgument implements Argument {

    @Override
    public List<String> tab(CommandSender sender, String arg) {
        List<String> ret = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            if (world.getName().startsWith(arg)) {
                ret.add(world.getName());
            }
        }
        return ret;
    }
    
}
