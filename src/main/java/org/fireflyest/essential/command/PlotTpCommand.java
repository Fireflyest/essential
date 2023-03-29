package org.fireflyest.essential.command;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.essential.bean.Domain;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.data.StateCache;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.essential.util.TeleportUtils;
import org.fireflyest.util.SerializationUtil;

public class PlotTpCommand extends SubCommand {

    private EssentialService service;
    private StateCache cache;

    public PlotTpCommand(EssentialService service, StateCache cache) {
        this.service = service;
        this.cache = cache;
    }

    @Override
    protected boolean execute(CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }

        String[] domains = service.selectDomainsNameByPlayer(player.getUniqueId());
        if (domains.length > 1) {
            this.execute(sender, domains[0]);
        } else {
            sender.sendMessage(Language.PLOT_NULL);
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
        
        Domain domain = service.selectDomainsByName(arg1);
        if (domain == null) {
            sender.sendMessage(Language.PLOT_NON_EXISTENT);
            return true;
        }

        // TODO: 权限判断
        if (!domain.getOwner().equals(player.getUniqueId().toString())) {

        }

        Location point = SerializationUtil.deserialize(domain.getCenter(), Location.class);
        
        cache.set(player.getName() + ".base.back", SerializationUtil.serialize(player.getLocation()));
        player.sendMessage(Language.SAVE_POINT);

        TeleportUtils.teleportTo(player, point, player.hasPermission("essential.vip"));
        player.sendMessage(Language.TELEPORT_POINT.replace("%point%", arg1));

        return true;
    }
    
}
