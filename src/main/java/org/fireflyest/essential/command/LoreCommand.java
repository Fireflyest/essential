package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;
import org.fireflyest.util.ItemUtils;

public class LoreCommand extends SimpleCommand {

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        return true;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender, @Nonnull String arg1, @Nonnull String arg2) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        if (player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
            return true;
        }
        int line = Integer.parseInt(arg2);
        ItemUtils.setLore(player.getInventory().getItemInMainHand(), arg1.replace("&", "§"), line);
        return true;
    }
    
}
