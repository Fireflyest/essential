package org.fireflyest.essential.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.craftmsg.MessageService;
import org.fireflyest.essential.data.Language;

public class GodCommand extends SimpleCommand {

    private final MessageService message;

    public GodCommand(MessageService message) {
        this.message = message;
    }

    @Override
    protected boolean execute(CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        player.setInvulnerable(!player.isInvulnerable());
        message.popMessage(player, player.isInvulnerable() ? "无敌模式开启" : "无敌模式关闭");
        return true;
    }
    
}
