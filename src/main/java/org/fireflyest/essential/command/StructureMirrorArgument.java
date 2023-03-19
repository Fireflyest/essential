package org.fireflyest.essential.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.structure.Mirror;
import org.bukkit.command.CommandSender;
import org.fireflyest.craftcommand.argument.Argument;

public class StructureMirrorArgument implements Argument {

    @Override
    public List<String> tab(CommandSender sender, String arg) {
        List<String> ret = new ArrayList<>();
        for (Mirror mirror : Mirror.values()) {
            if (mirror.name().startsWith(arg)) {
                ret.add(mirror.name());
            }
        }
        return ret;
    }
    
}
