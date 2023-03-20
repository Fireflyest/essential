package org.fireflyest.essential.command;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.essential.data.Language;

public class WorldCreateCommand extends SubCommand {

    @Override
    protected boolean execute(CommandSender sender) {
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        WorldCreator worldCreator = new WorldCreator(arg1);
        Bukkit.createWorld(worldCreator);
        sender.sendMessage(Language.WORLD_CREATE + arg1);
         return true;
    }
    
}
