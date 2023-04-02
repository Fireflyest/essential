package org.fireflyest.essential.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.fireflyest.craftcommand.argument.Argument;

public class StructureTypeArgument implements Argument {

    private Set<String> stringSet = new HashSet<>();

    public StructureTypeArgument() {
        stringSet.add("road");
        stringSet.add("cottage");
        stringSet.add("tree");
        stringSet.add("ornament");
        stringSet.add("bridge");
        stringSet.add("tower");
        stringSet.add("wall");
        stringSet.add("machine");
        stringSet.add("statue");
    }

    @Override
    public List<String> tab(CommandSender sender, String arg) {
        List<String> ret = new ArrayList<>();
        for (String string : stringSet) {
            if (string.startsWith(arg)) {
                ret.add(string);
            }
        }
        return ret;
    }
    
}
