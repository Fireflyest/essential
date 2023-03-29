package org.fireflyest.essential.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;

public class RideCommand extends SimpleCommand {

    @Override
    protected boolean execute(CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        
        Entity targetEntity = null;
        Location eyeLoc = player.getEyeLocation().add(player.getEyeLocation().getDirection().normalize());
        double minDistance = 20;
        for (Entity nearbyEntity : player.getNearbyEntities(3, 3, 3)) {
            double d;
            if ((d = eyeLoc.distance(nearbyEntity.getLocation())) < minDistance) {
                targetEntity = nearbyEntity;
                minDistance = d;
            }
        }
        if (targetEntity != null) {
            targetEntity.addPassenger(player);
        }
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }

        Player target = Bukkit.getPlayerExact(arg1);
        if (target != null && target.getLocation().distance(player.getLocation()) < 3) {
            target.addPassenger(player);
        }

        return true;
    }
    
}
