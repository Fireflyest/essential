package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.craftmsg.MessageService;
import org.fireflyest.essential.data.Language;

public class FlyCommand extends SimpleCommand {

    private final MessageService message;

    public FlyCommand(MessageService message) {
        this.message = message;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        player.setAllowFlight(!player.getAllowFlight());
        message.popMessage(player, player.getAllowFlight() ? "飞行模式开启" : "飞行模式关闭");
        return true;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender, @Nonnull String arg1) {
        if (!sender.hasPermission("essential.givefly"))  {
            sender.sendMessage(Language.NOT_PERMISSION.replace("%permission%", "essential.givefly"));
            return true;
        }
        Player target = Bukkit.getPlayer(arg1);
        if (target == null) {
            sender.sendMessage(Language.OFFLINE_PLAYER.replace("%player%", arg1));
            return false;
        }
        target.setAllowFlight(!target.getAllowFlight());
        sender.sendMessage(Language.SUCCEED_SWITCH + "§3" + arg1);
        message.popMessage(target, target.getAllowFlight() ? "飞行模式开启" : "飞行模式关闭");
        return true;
    }
    
}
