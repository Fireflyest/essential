package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.bean.Home;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.data.StateCache;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.essential.util.TeleportUtils;
import org.fireflyest.util.SerializationUtil;

public class HomeCommand extends SimpleCommand {

    private EssentialService service;
    private StateCache cache;

    /**
     * 传送到家
     * @param service 数据服务
     */
    public HomeCommand(EssentialService service, StateCache cache) {
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
        Home home = service.selectHome(player.getUniqueId(), arg1);
        if (home == null) {
            player.sendMessage(Language.HAVE_NOT_SETTLE.replace("%home%", arg1));
            return true;
        }
        cache.set(player.getName() + ".base.back", SerializationUtil.serialize(player.getLocation()));

        Location loc = SerializationUtil.deserialize(home.getLoc(), Location.class);
        TeleportUtils.teleportTo(player, loc, player.hasPermission("essential.vip"), "家" + arg1);

        player.closeInventory();

        return true;
    }

}
