package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;

public class SkullCommand extends SimpleCommand {

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        return execute(sender, sender.getName());
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender, @Nonnull String arg1) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        Player target = Bukkit.getPlayer(arg1);
        if (target == null) {
            sender.sendMessage(Language.OFFLINE_PLAYER.replace("%player%", arg1));
            return false;
        }
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = ((SkullMeta)item.getItemMeta());
        if (meta != null) {
            meta.setOwningPlayer(target);
        }
        player.getInventory().addItem(item);
        return true;
    }

    
}
