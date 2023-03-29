package org.fireflyest.essential.command;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.essential.bean.Domain;
import org.fireflyest.essential.data.Config;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.data.StateCache;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.essential.world.Dimension;

public class PlotGiveCommand extends SubCommand {

    private Map<String, Dimension> worldMap;
    private EssentialService service;
    private StateCache cache;

    public PlotGiveCommand(EssentialService service, StateCache cache, Map<String, Dimension> worldMap) {
        this.service = service;
        this.cache = cache;
        this.worldMap = worldMap;
    }

    @Override
    protected boolean execute(CommandSender sender) {
        sender.sendMessage(Language.COMMAND_ARGUMENT);
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        sender.sendMessage(Language.COMMAND_ARGUMENT);
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1, String arg2) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        String worldName = service.selectDomainWorld(arg1);
        // 判断该世界是否记录
        Dimension dimension = worldMap.get(worldName);
        if (dimension == null) {
            sender.sendMessage(Language.PLOT_WORLD_UNKNOWN);
            return true;
        }

        Domain domain = dimension.getDomain(arg1);
        if (domain == null) {
            sender.sendMessage(Language.PLOT_NON_EXISTENT);
            return true;
        }
        if (!domain.getOwner().equals(player.getUniqueId().toString())) {
            sender.sendMessage(Language.PLOT_FORBID);
            return true;
        }

        // 获取对方uid
        String targetUid = service.selectSteveUid(arg2);
        // 上限
        if (service.selectDomainsByPlayer(UUID.fromString(targetUid)).length > Config.MAX_DOMAIN) {
            sender.sendMessage(Language.PLOT_UPPER_LIMIT);
            return true;
        }

        String key = player.getName() + ".command.confirm";
        if (cache.exist(key) && "give".equals(cache.get(key))) {
            dimension.giveDomain(domain, targetUid);
            sender.sendMessage(Language.PLOT_GIVE);
            player.performCommand("plot map");
            // 告诉对方
            Player target = Bukkit.getPlayerExact(arg2);
            if (target != null) {
                sender.sendMessage(Language.PLOT_BE_GIVEN + arg1);
            }
        } else {
            cache.setex(key, 30, "give");
            sender.sendMessage(Language.COMMAND_CONFIRM);
        }

        return true;
    }
    
}
