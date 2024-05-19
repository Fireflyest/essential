package org.fireflyest.essential.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.craftmsg.MessageService;
import org.fireflyest.essential.data.Language;

public class VanishCommand extends SimpleCommand {

    private final MessageService message;

    public VanishCommand(MessageService message) {
        this.message = message;
    }

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
            message.popMessage(player, "影身模式开启");
        } else {
            player.setInvisible(true);
            player.setPlayerListName("隐藏玩家");
            message.popMessage(player, "影身模式关闭");
        }
        return true;
    }
    
}
