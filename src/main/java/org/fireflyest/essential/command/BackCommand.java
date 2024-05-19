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

public class BackCommand extends SimpleCommand {

    private StateCache cache;

    /**
     * 返回上一个记录点
     * @param cache 缓存
     */
    public BackCommand(StateCache cache) {
        this.cache = cache;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        
        String loc = cache.get(player.getName() + ".base.back");
        if (loc != null) {
            cache.set(player.getName() + ".base.back", SerializationUtil.serialize(player.getLocation()));

            Location location = SerializationUtil.deserialize(loc, Location.class);
            TeleportUtils.teleportTo(player, location, player.hasPermission("essential.vip"), "上个记录点");
        }
        return true;
    }
    
}
