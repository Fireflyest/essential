package org.fireflyest.essential.command;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.essential.bean.Domain;
import org.fireflyest.essential.data.Config;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.data.StateCache;
import org.fireflyest.essential.service.EssentialEconomy;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.essential.world.Dimension;
import org.fireflyest.essential.world.Plot;

public class PlotRemoveCommand extends SubCommand {

    private Map<String, Dimension> worldMap;
    private EssentialService service;
    private EssentialEconomy economy;
    private StateCache cache;

    public PlotRemoveCommand(EssentialService service, EssentialEconomy economy, StateCache cache, Map<String, Dimension> worldMap) {
        this.service = service;
        this.economy = economy;
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
        String worldName = player.getWorld().getName();
        // 判断该世界是否记录
        Dimension dimension = worldMap.get(worldName);
        if (dimension == null) {
            sender.sendMessage(Language.PLOT_WORLD_UNKNOWN);
            return true;
        }
        String loc = player.getLocation().getChunk().getX() + ":" + player.getLocation().getChunk().getZ();

        Plot plot = dimension.getPlot(loc);
        if (plot == null) {
            sender.sendMessage(Language.PLOT_OUTSIDE);
            return true;
        }

        return this.execute(sender, plot.getDomain().getName());
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1) {
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

        String key = player.getName() + ".command.confirm";
        if (cache.exist(key) && "remove".equals(cache.get(key))) {
            // 退钱
            economy.depositPlayer(player, Config.BASE_CHUNK_PRICE * domain.getLevel() * .0);

            dimension.removeDomain(domain);
            sender.sendMessage(Language.PLOT_REMOVE);
            player.performCommand("plot map");
        } else {
            cache.setex(key, 30, "remove");
            sender.sendMessage(Language.COMMAND_CONFIRM);
        }

        return true;
    }
    
}
