package org.fireflyest.essential.command;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.essential.bean.Domain;
import org.fireflyest.essential.data.Config;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.service.EssentialEconomy;
import org.fireflyest.essential.world.Dimension;
import org.fireflyest.essential.world.Plot;

public class PlotAbandonCommand extends SubCommand {

    private EssentialEconomy economy;
    private Map<String, Dimension> worldMap;

    public PlotAbandonCommand(EssentialEconomy economy, Map<String, Dimension> worldMap) {
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

        // 未在地皮内输入
        Plot plot = dimension.getPlot(arg1);
        if (plot == null) {
            sender.sendMessage(Language.PLOT_OUTSIDE);
            return true;
        }

        Domain domain = plot.getDomain();
        if (!domain.getOwner().equals(player.getUniqueId().toString())) {
            sender.sendMessage(Language.PLOT_FORBID);
            return true;
        }

        // 初始地皮无法删除
        if (!domain.getPlots().contains(" " + arg1)) {
            sender.sendMessage(Language.PLOT_ABANDON_CENTER);
            return true;
        }

        // 归还基本金额
        economy.depositPlayer(player, Config.BASE_CHUNK_PRICE);

        dimension.abandonPlot(domain, plot);
        player.performCommand("plot map");
        sender.sendMessage(Language.PLOT_ABANDON);

        return true;
    }
    
}
