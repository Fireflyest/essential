package org.fireflyest.essential.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;

public class RepairCommand extends SimpleCommand {

    @Override
    protected boolean execute(CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getItemMeta() instanceof Damageable) {
            Damageable damageable = ((Damageable)item.getItemMeta());
            damageable.setDamage(0);
            item.setItemMeta(((ItemMeta)damageable));
            player.sendMessage(Language.ITEM_REPAIR);
        }

        return true;
    }
    
}
