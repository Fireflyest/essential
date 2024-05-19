package org.fireflyest.essential.command;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SubCommand;

public class TipSelectCommand extends SubCommand {

    @Override
    protected boolean execute(CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            return false;
        }
        // 找出最近的
        Location eyeLocation = player.getEyeLocation().add(player.getEyeLocation().getDirection().normalize().multiply(2));
        double distance = 10;
        ArmorStand nearArmorStand = null;
        for (Entity entity : player.getNearbyEntities(2.5, 2.5, 2.5)) {
            if (!(entity instanceof ArmorStand)) {
                continue;
            }
            ArmorStand armorStand = ((ArmorStand)entity);
            double tempDistance;
            if ((tempDistance = eyeLocation.distance(armorStand.getEyeLocation())) < distance) {
                distance = tempDistance;
                nearArmorStand = armorStand;
            }
        }
        // 选择
        if (nearArmorStand != null) {
            TipCommand.selectArmorStand(nearArmorStand);
        }
        return true;
    }


}
