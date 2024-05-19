package org.fireflyest.essential.command;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.craftitem.builder.ItemBuilder;
import org.fireflyest.essential.data.EssentialYaml;
import org.fireflyest.essential.data.Language;

public class KitCommand extends SimpleCommand {

    private final EssentialYaml yaml;

    public KitCommand(EssentialYaml yaml) {
        this.yaml = yaml;
    }

    @Override
    protected boolean execute(CommandSender sender) {
        return this.execute(sender, "default");
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        return this.execute(sender, arg1, sender.getName());
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1, String arg2) {
        if (!sender.hasPermission("essential.kit." + arg1))  {
            sender.sendMessage(Language.NOT_PERMISSION.replace("%permission%", "essential.kit." + arg1));
            return true;
        }
        if (!sender.getName().equals(arg2) && !sender.hasPermission("essential.kit")) {
            sender.sendMessage(Language.NOT_PERMISSION.replace("%permission%", "essential.kit"));
            return true;
        }
        Player target = Bukkit.getPlayer(arg2);
        if (target == null) {
            sender.sendMessage(Language.OFFLINE_PLAYER.replace("%player%", arg1));
            return false;
        }

        Set<ItemBuilder> itemBuilders = yaml.getItemBuilders("kit_" + arg1);
        if (itemBuilders.isEmpty()) {
            sender.sendMessage(Language.HAVE_NOT_SET_KIT.replace("%kit%", arg1));
        } else {
            // TODO: cooldown
            for (ItemBuilder builder : itemBuilders) {
                target.getInventory().addItem(builder.build());
            }
            sender.sendMessage(Language.GIVE_KIT.replace("%kit%", arg1));
        }
        
        return true;
    }
    
}
