package org.fireflyest.essential.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.fireflyest.craftcommand.argument.Argument;

public class StructureArgument implements Argument {

    private Set<String> stringSet = new HashSet<>();

    public StructureArgument() {
        //
    }

    @Override
    public List<String> tab(CommandSender sender, String arg) {
        stringSet.clear();
        for (NamespacedKey key : Bukkit.getStructureManager().getStructures().keySet()) {
            stringSet.add(key.getKey());
        }
        List<String> ret = new ArrayList<>();
        for (String string : stringSet) {
            if (string.startsWith(arg)) {
                ret.add(string);
            }
        }
        return ret;
    }
    
}
