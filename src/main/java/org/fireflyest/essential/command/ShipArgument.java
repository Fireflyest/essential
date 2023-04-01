package org.fireflyest.essential.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.fireflyest.craftcommand.argument.Argument;
import org.fireflyest.essential.data.EssentialYaml;

public class ShipArgument implements Argument {

    private EssentialYaml yaml;

    public ShipArgument(EssentialYaml yaml) {
        this.yaml = yaml;
    }

    @Override
    public List<String> tab(CommandSender sender, String arg) {
        List<String> ret = new ArrayList<>();
        Set<String> ships = new HashSet<>(yaml.getShip().getKeys(false));
        ships.add("master");
        ships.add("apprentice");
        ships.add("lover");
        ships.add("couple");
        for (String ship : ships) {
            if (ship.startsWith(arg)) {
                ret.add(ship);
            }
        }
        return ret;
    }
    
}
