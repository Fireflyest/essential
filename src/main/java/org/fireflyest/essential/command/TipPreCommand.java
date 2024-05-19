package org.fireflyest.essential.command;

import org.bukkit.command.CommandSender;
import org.fireflyest.craftcommand.command.SubCommand;

public class TipPreCommand extends SubCommand {

    @Override
    protected boolean execute(CommandSender sender) {
        TipCommand.preArmorStand();
        return true;
    }
    
}
