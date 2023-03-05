package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;

public class MessageCommand extends SimpleCommand {

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        return true;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender, @Nonnull String[] args) {
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(Language.OFFLINE_PLAYER.replace("%player%", args[0]));
            return false;
        }
        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            message.append(" ").append(args[i]);
        }
        String msg = message.toString();
        sender.sendMessage(Language.TITLE + "悄悄对§3" + args[0] + "§f说 §7»§f" + msg);
        target.sendMessage(Language.TITLE + "§3" + sender.getName() + "§f悄悄对你说 §7»§f" + msg);
        return true;
    }
    
}
