package org.fireflyest.essential.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.structure.StructureRotation;
import org.bukkit.command.CommandSender;
import org.fireflyest.craftcommand.argument.Argument;

public class StructureRotationArgument implements Argument {

    @Override
    public List<String> tab(CommandSender sender, String arg) {
        List<String> ret = new ArrayList<>();
        for (StructureRotation rotation : StructureRotation.values()) {
            if (rotation.name().startsWith(arg)) {
                ret.add(rotation.name());
            }
        }
        return ret;
    }
    
}
