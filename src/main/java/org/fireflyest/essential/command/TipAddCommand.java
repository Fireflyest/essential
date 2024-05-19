package org.fireflyest.essential.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SubCommand;

public class TipAddCommand extends SubCommand {

    @Override
    protected boolean execute(CommandSender sender) {
        return this.execute(sender, "");
    }
    
    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            return false;
        }

        TipCommand.lastArmorStand();

        ArmorStand stand = ((ArmorStand)player.getWorld().spawnEntity(TipCommand.getSelectLocation().add(0, -0.3, 0), EntityType.ARMOR_STAND));
        stand.setGravity(false);
        stand.setSmall(true);
        stand.setBasePlate(false);
        stand.setPersistent(true);
        stand.setCollidable(false);
        stand.setVisible(false);
        stand.setCustomNameVisible(true);
        stand.setCustomName(arg1.replace("&", "§"));
        
        TipCommand.selectArmorStand(stand);
        return true;
    }

}
