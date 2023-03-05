package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Config;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.data.StateCache;
import org.fireflyest.essential.util.TeleportUtils;
import org.fireflyest.util.SerializationUtil;

public class SpawnCommand extends SimpleCommand {

    private StateCache cache;

    /**
     * 出生点
     * @param cache 缓存
     */
    public SpawnCommand(StateCache cache) {
        this.cache = cache;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        
        World world = Bukkit.getWorld(Config.MAIN_WORLD);
        if (world != null) {
            cache.set(player.getName() + ".base.back", SerializationUtil.serialize(player.getLocation()));
            player.sendMessage(Language.SAVE_POINT);

            TeleportUtils.teleportTo(player, world.getSpawnLocation(), player.hasPermission("essential.vip"));
            player.sendMessage(Language.TELEPORT_POINT.replace("%point%", "spawn"));
        }
        
        return true;
    }
    
}
