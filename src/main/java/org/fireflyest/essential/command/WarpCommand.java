package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.bean.Point;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.data.StateCache;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.essential.util.TeleportUtils;
import org.fireflyest.util.SerializationUtil;

public class WarpCommand extends SimpleCommand {

    private EssentialService service;
    private StateCache cache;

    /**
     * 传送点
     * @param service 数据服务
     * @param cache 缓存
     */
    public WarpCommand(EssentialService service, StateCache cache) {
        this.service = service;
        this.cache = cache;
    }

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
        
        if (!player.hasPermission("essential.warp." + arg1))  {
            sender.sendMessage(Language.NOT_PERMISSION.replace("%permission%", "essential.warp." + arg1));
            return true;
        }

        Point point = service.selectPoint(arg1);
        if (point != null) {
            cache.set(player.getName() + ".base.back", SerializationUtil.serialize(player.getLocation()));

            Location loc = SerializationUtil.deserialize(point.getLoc(), Location.class);
            TeleportUtils.teleportTo(player, loc, player.hasPermission("essential.vip"), arg1);
        } else {
            sender.sendMessage(Language.HAVE_NOT_SET_POI.replace("%point%", arg1));
        }

        return true;
    }
    
}
