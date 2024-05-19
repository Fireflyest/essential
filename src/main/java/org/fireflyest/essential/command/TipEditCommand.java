package org.fireflyest.essential.command;

import org.bukkit.command.CommandSender;
import org.fireflyest.craftcommand.command.SubCommand;

public class TipEditCommand extends SubCommand {

    @Override
    protected boolean execute(CommandSender sender) {
        return this.execute(sender, "##########");
    }
    
    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        TipCommand.editArmorStand(arg1.replace("&", "§"));
        return true;
    }

}
