package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.data.StateCache;
import org.fireflyest.essential.util.TeleportUtils;
import org.fireflyest.util.SerializationUtil;

public class TopCommand extends SimpleCommand {

    private StateCache cache;

    /**
     * 到顶端
     * @param cache 缓存
     */
    public TopCommand(StateCache cache) {
        this.cache = cache;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }

        Location loc = player.getLocation();
        cache.set(player.getName() + ".base.back", SerializationUtil.serialize(loc));
        player.sendMessage(Language.SAVE_POINT);
        
        loc.setY(loc.getChunk().getChunkSnapshot().getHighestBlockYAt(Math.abs((int)loc.getX() % 16), Math.abs((int)loc.getZ() % 16)) + 2.5);
        TeleportUtils.teleportTo(player, loc, player.hasPermission("essential.vip"));
        return true;
    }
    
}
