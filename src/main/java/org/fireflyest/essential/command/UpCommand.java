package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.data.StateCache;
import org.fireflyest.essential.util.TeleportUtils;
import org.fireflyest.util.SerializationUtil;

public class UpCommand extends SimpleCommand {

    private StateCache cache;

    /**
     * 上升
     * @param cache 数据缓存
     */
    public UpCommand(StateCache cache) {
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

        cache.set(player.getName() + ".base.back", SerializationUtil.serialize(player.getLocation()));
        player.sendMessage(Language.SAVE_POINT);
        
        Location loc = player.getLocation().add(0, Double.parseDouble(arg1), 0);
        Location f = loc.clone().add(0, -1, 0);
        if (f.getBlock().getType().equals(Material.AIR)) {
            f.getBlock().setType(Material.GLASS);
        }
        TeleportUtils.teleportTo(player, loc, player.hasPermission("essential.vip"));
        return true;
    }
    
}
