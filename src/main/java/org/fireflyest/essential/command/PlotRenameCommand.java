package org.fireflyest.essential.command;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.essential.bean.Domain;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.essential.world.Dimension;
import org.fireflyest.essential.world.Plot;

public class PlotRenameCommand extends SubCommand {

    private Map<String, Dimension> worldMap;
    private EssentialService service;

    public PlotRenameCommand(EssentialService service, Map<String, Dimension> worldMap) {
        this.service = service;
        this.worldMap = worldMap;
    }

    @Override
    protected boolean execute(CommandSender sender) {
        sender.sendMessage(Language.COMMAND_ARGUMENT);
        return true;
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
        String loc = player.getLocation().getChunk().getX() + ":" + player.getLocation().getChunk().getZ();

        Plot plot = dimension.getPlot(loc);
        if (plot == null) {
            sender.sendMessage(Language.PLOT_OUTSIDE);
            return true;
        }

        Domain domain = plot.getDomain();
        if (domain == null) {
            sender.sendMessage(Language.PLOT_NON_EXISTENT);
            return true;
        }
        if (!domain.getOwner().equals(player.getUniqueId().toString())) {
            sender.sendMessage(Language.PLOT_FORBID);
            return true;
        }

        String name = domain.getName();
        domain.setName(arg1);
        service.updateDomainName(domain.getName(), name);
        sender.sendMessage(Language.PLOT_RENAME);

        return true;
    }
    
}
