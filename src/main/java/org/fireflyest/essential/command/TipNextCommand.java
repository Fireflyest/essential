package org.fireflyest.essential.command;

import org.bukkit.command.CommandSender;
import org.fireflyest.craftcommand.command.SubCommand;

public class TipNextCommand extends SubCommand {

    @Override
    protected boolean execute(CommandSender sender) {
        TipCommand.nextArmorStand();
        return true;
    }
    
}
