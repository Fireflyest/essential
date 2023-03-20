package org.fireflyest.essential.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.essential.data.Language;

public class WorldUnloadCommand extends SubCommand {

    @Override
    protected boolean execute(CommandSender sender) {
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        Bukkit.unloadWorld(arg1, true);
        sender.sendMessage(Language.WORLD_UNLOAD + arg1);
         return true;
    }
    
}
