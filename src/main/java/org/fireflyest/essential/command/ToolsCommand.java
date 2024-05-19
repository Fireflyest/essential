package org.fireflyest.essential.command;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.craftitem.builder.InteractItemBuilder;

public class ToolsCommand extends SimpleCommand {

    @Override
    protected boolean execute(CommandSender sender) {
        return false;
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            return false;
        }


        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1, String arg2) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            return false;
        }

        Material material = Material.STICK;
        if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
            material = player.getInventory().getItemInMainHand().getType();
        }

        arg2 = arg2.replace("_", " ");
        ItemStack item = new InteractItemBuilder(material)
            .triggerUse(arg1, arg2)
            .cooldown(10)
            .name("§f快捷工具")
            .lore("")
            .lore("§f[§b" + arg1 + "§f]")
            .lore("§f" + arg2)
            .build();

        player.getInventory().addItem(item);

        return true;
    }
    
}
