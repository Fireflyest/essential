package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;

public class SuicideCommand extends SimpleCommand {

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }

        if (!sender.hasPermission("essential.suicide"))  {
            sender.sendMessage(Language.NOT_PERMISSION.replace("%permission%", "essential.suicide"));
            return true;
        }
        player.setHealth(0);
        return true;
    }
    
}
