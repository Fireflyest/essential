package org.fireflyest.essential.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;

public class GodCommand extends SimpleCommand {

    @Override
    protected boolean execute(CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        player.setInvulnerable(!player.isInvulnerable());
        player.sendMessage(Language.TITLE + "无敌: §3" + player.isInvulnerable());
        return true;
    }
    
}
