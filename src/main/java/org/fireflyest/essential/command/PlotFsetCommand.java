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

public class PlotFsetCommand extends SubCommand {

    private Map<String, Dimension> worldMap;
    private EssentialService service;

    public PlotFsetCommand(EssentialService service, Map<String, Dimension> worldMap) {
        this.service = service;
        this.worldMap = worldMap;
    }

    @Override
    protected boolean execute(CommandSender sender) {
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

        long permit = 0;
        switch (arg1) {
            case "use":
                permit = Dimension.PERMISSION_USE;
                break;
            case "destroy":
                permit = Dimension.PERMISSION_DESTROY;
                break;
            case "place":
                permit = Dimension.PERMISSION_PLACE;
                break;
            case "bucket":
                permit = Dimension.PERMISSION_BUCKET;
                break;
            case "ignite":
                permit = Dimension.PERMISSION_IGNITE;
                break;
            case "build":
                permit = Dimension.PERMISSION_BUILD;
                break;
            case "pve":
                permit = Dimension.PERMISSION_PVE;
                break;
            case "open":
                permit = Dimension.PERMISSION_OPEN;
                break;
            case "tp":
                permit = Dimension.PERMISSION_TP;
                break;
            case "armor":
                permit = Dimension.PERMISSION_ARMOR;
                break;
            case "pvp":
                permit = Dimension.FLAG_PVP;
                break;
            case "monster":
                permit = Dimension.FLAG_MONSTER;
                break;
            case "explode":
                permit = Dimension.FLAG_EXPLODE;
                break;
            case "piston":
                permit = Dimension.FLAG_PISTON;
                break;
            case "water":
                permit = Dimension.FLAG_FLOW_WATER;
                break;
            case "lava":
                permit = Dimension.FLAG_FLOW_LAVA;
                break;
            case "flow":
                permit = Dimension.FLAG_FLOW;
                break;
            default:
                break;
        }
        domain.setFriend(dimension.switchPermit(domain.getFriend(), permit));
        service.updateDomainFriend(domain.getFriend(), domain.getName());
        player.sendMessage(Language.PLOT_FLAG_SWITCH
            .replace("%domain%", domain.getName())
            .replace("%flag%", arg1) 
            .replace("%scope%", "好友") 
            + (dimension.isPermit(domain.getFriend(), permit) ? "§a开启" : "§c关闭"));
        return true;
    }
    
}
