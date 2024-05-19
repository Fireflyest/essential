package org.fireflyest.essential.command;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.essential.bean.Domain;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.data.StateCache;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.essential.util.TeleportUtils;
import org.fireflyest.essential.world.Dimension;
import org.fireflyest.util.SerializationUtil;

public class PlotTpCommand extends SubCommand {
    
    private final Map<String, Dimension> worldMap;
    private final EssentialService service;
    private final StateCache cache;

    public PlotTpCommand(EssentialService service, StateCache cache, Map<String, Dimension> worldMap) {
        this.service = service;
        this.cache = cache;
        this.worldMap = worldMap;
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
        
        Domain domain = service.selectDomainByName(arg1);
        if (domain == null) {
            sender.sendMessage(Language.PLOT_NON_EXISTENT);
            player.closeInventory();
            return true;
        }

        Dimension dimension = worldMap.get(domain.getWorld());
        if (dimension == null) {
            sender.sendMessage(Language.PLOT_WORLD_UNKNOWN);
            player.closeInventory();
            return true;
        }

        if (!domain.getOwner().equals(player.getUniqueId().toString())) {
            Dimension.EventResult result = dimension.triggerPermit(domain.getPlots().split(" ")[0], player.getUniqueId().toString(), Dimension.PERMISSION_TP, !dimension.isProtect());
            if (!result.isAllow()) {
                this.sendPermitMessage(player, result.getDomain(), "tp");
                return true;
            }
        }

        Location point = SerializationUtil.deserialize(domain.getCenter(), Location.class);
        
        cache.set(player.getName() + ".base.back", SerializationUtil.serialize(player.getLocation()));

        TeleportUtils.teleportTo(player, point, player.hasPermission("essential.vip"), "地皮" + arg1);

        player.closeInventory();

        return true;
    }

    /**
     * 权限提示
     * @param player 玩家
     * @param domain 领地
     * @param permission 权限
     */
    private void sendPermitMessage(Player player, String domain, String permission) {
        player.sendMessage(Language.PLOT_FLAG
            .replace("%domain%", domain)
            .replace("%flag%", permission));
    }
    
}
