package org.fireflyest.essential.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;

public class VanishCommand extends SimpleCommand {

    @Override
    protected boolean execute(CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        boolean invisible = player.isInvisible();
        if (invisible) {
            player.setInvisible(false);
            player.setPlayerListName(player.getName());
            player.sendMessage(Language.TITLE + "隐身: §3false");
        } else {
            player.setInvisible(true);
            player.setPlayerListName("隐藏玩家");
            player.sendMessage(Language.TITLE + "隐身: §3true");
        }
        return true;
    }
    
}
