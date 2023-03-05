package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;

public class SudoCommand extends SimpleCommand {

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        return true;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender, @Nonnull String[] args) {
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Language.OFFLINE_PLAYER.replace("%player%", args[0]));
            return false;
        }
        StringBuilder command = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            command.append(" ").append(args[i]);
        }
        String cmd = command.toString();
        Bukkit.dispatchCommand(target, cmd);
        return true;
    }
    
}
