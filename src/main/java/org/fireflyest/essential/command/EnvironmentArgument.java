package org.fireflyest.essential.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.fireflyest.craftcommand.argument.Argument;

public class EnvironmentArgument implements Argument {

    @Override
    public List<String> tab(CommandSender sender, String arg) {
        List<String> ret = new ArrayList<>();
        for (Environment environment : Environment.values()) {
            if (environment.name().startsWith(arg)) {
                ret.add(environment.name());
            }
        }
        return ret;
    }
    
}
