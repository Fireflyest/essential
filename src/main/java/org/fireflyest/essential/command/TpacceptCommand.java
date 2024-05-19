package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.data.StateCache;
import org.fireflyest.essential.util.TeleportUtils;
import org.fireflyest.util.SerializationUtil;

public class TpacceptCommand extends SimpleCommand {

    private StateCache cache;

    /**
     * 接受传送
     * @param cache 缓存
     */
    public TpacceptCommand(StateCache cache) {
        this.cache = cache;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }

        String tp = cache.get(player.getName() + ".base.tp");
        if (tp != null) {
            Player target = Bukkit.getPlayer(tp);
            if (target != null) {
                Location loc = target.getLocation();
                cache.set(target.getName() + ".base.back", SerializationUtil.serialize(loc));
                
                TeleportUtils.teleportTo(target, player.getLocation(), target.hasPermission("essential.vip"), player.getName() + "的身边");

                sender.sendMessage(Language.APPLY_ACCEPT);
            }
            return true;
        }

        String tphere = cache.get(player.getName() + ".base.tphere");
        if (tphere != null) {
            Player target = Bukkit.getPlayer(tphere);
            if (target != null) {
                Location loc = player.getLocation();
                cache.set(player.getName() + ".base.back", SerializationUtil.serialize(loc));
                
                TeleportUtils.teleportTo(player, target.getLocation(), player.hasPermission("essential.vip"), target.getName() + "的身边");

                sender.sendMessage(Language.APPLY_ACCEPT);
            }
            return true;
        }
        return true;
    }
    
}
