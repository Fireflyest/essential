package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;
import org.fireflyest.util.SerializationUtil;

public class TestCommand extends SimpleCommand {

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        return true;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender, @Nonnull String arg1) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        switch (arg1) {
            case "entity":
                Location eyeLocation = player.getEyeLocation().add(player.getEyeLocation().getDirection().normalize().multiply(2));
                double distance = 10;
                Entity nearEntity = null;
                for (Entity entity : player.getNearbyEntities(2.5, 2.5, 2.5)) {
                    double tempDistance;
                    if ((tempDistance = eyeLocation.distance(entity.getLocation())) < distance) {
                        distance = tempDistance;
                        nearEntity = entity;
                    }
                }
                if (nearEntity != null) {
                    if (nearEntity instanceof ConfigurationSerializable) {
                        player.sendMessage(SerializationUtil.serialize((ConfigurationSerializable)nearEntity));
                    } else {
                        player.sendMessage("Entity type = " + nearEntity.getType());
                    }
                }
                break;
            case "sign":
                Block block = player.getTargetBlockExact(5);
                break;
            default:
                break;
        }
        return true;
    }
    
}
