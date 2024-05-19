package org.fireflyest.essential.command;

import org.bukkit.command.CommandSender;
import org.fireflyest.craftcommand.command.SubCommand;

public class TipRemoveCommand extends SubCommand {

    @Override
    protected boolean execute(CommandSender sender) {
        TipCommand.removeArmorStand();
        return true;
    }
    
}
