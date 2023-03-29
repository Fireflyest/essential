package org.fireflyest.essential.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;

public class HoldCommand extends SimpleCommand {

    @Override
    protected boolean execute(CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }

        if (player.getPassengers().isEmpty()) {
            boolean holdAnything = sender.hasPermission("essential.hold.anything");
            Entity targetEntity = null;
            Location eyeLoc = player.getEyeLocation().add(player.getEyeLocation().getDirection().normalize());
            double minDistance = 20;
            for (Entity nearbyEntity : player.getNearbyEntities(3, 3, 3)) {
                // 没权限只能举起玩家
                if (!holdAnything && !(nearbyEntity instanceof Player)) {
                    continue;
                }
                double d;
                if ((d = eyeLoc.distance(nearbyEntity.getLocation())) < minDistance) {
                    targetEntity = nearbyEntity;
                    minDistance = d;
                }
            }
            if (targetEntity != null) {
                player.addPassenger(targetEntity);
                player.swingMainHand();
            }
        } else {
            Entity entity = player.getPassengers().get(0);
            player.removePassenger(entity);
            entity.setVelocity(player.getLocation().getDirection().normalize());
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
            player.addPassenger(target);
            player.swingMainHand();
        }

        return true;
    }
    
}
