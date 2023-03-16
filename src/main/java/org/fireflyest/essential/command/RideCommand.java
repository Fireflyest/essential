package org.fireflyest.essential.command;

import javax.swing.text.html.parser.Entity;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;

public class RideCommand extends SimpleCommand {

    @Override
    protected boolean execute(CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        
        
        return true;
    }
    
}
