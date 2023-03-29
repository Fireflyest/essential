package org.fireflyest.essential.command;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.essential.bean.Domain;
import org.fireflyest.essential.data.Config;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.service.EssentialEconomy;
import org.fireflyest.essential.world.Dimension;

public class PlotExpandCommand extends SubCommand {
    
    private EssentialEconomy economy;
    private Map<String, Dimension> worldMap;

    public PlotExpandCommand(EssentialEconomy economy, Map<String, Dimension> worldMap) {
        this.economy = economy;
        this.worldMap = worldMap;
    }

    @Override
    protected boolean execute(CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        String loc = player.getLocation().getChunk().getX() + ":" + player.getLocation().getChunk().getZ();
        return this.execute(sender, loc);
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1) {
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

        List<Domain> roadBelong = dimension.getRoadBelong(arg1);
        if (roadBelong == null || roadBelong.isEmpty()) {
            sender.sendMessage(Language.PLOT_EXPAND_NULL);
            return true;
        }

        if (roadBelong.size() > 1) {
            sender.sendMessage(Language.PLOT_EXPAND_FAIL);
            return true;
        }

        Domain domain = roadBelong.get(0);
        if (!domain.getOwner().equals(player.getUniqueId().toString())) {
            sender.sendMessage(Language.PLOT_FORBID);
            return true;
        }
        if (domain.getLevel() >= Config.MAX_CHUNK) {
            sender.sendMessage(Language.PLOT_UPPER_LIMIT);
            return true;
        }

        // 费用
        double cost = Config.BASE_CHUNK_PRICE + (domain.getLevel() * domain.getLevel() * (Config.BASE_CHUNK_PRICE / 5.0));
        if (!economy.has(player, cost)) {
            sender.sendMessage(Language.PLOT_EXPAND_COST + cost + economy.currencyNameSingular());
            return true;
        }
        
        economy.withdrawPlayer(player, cost);

        dimension.expandDomain(domain, arg1);
        player.performCommand("plot map");

        return true;
    }
    
}
