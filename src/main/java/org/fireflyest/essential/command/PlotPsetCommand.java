package org.fireflyest.essential.command;

import java.util.Map;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.essential.bean.Domain;
import org.fireflyest.essential.bean.Ship;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.essential.world.Dimension;
import org.fireflyest.essential.world.Plot;

public class PlotPsetCommand extends SubCommand {

    private Map<String, Dimension> worldMap;
    private EssentialService service;

    public PlotPsetCommand(EssentialService service, Map<String, Dimension> worldMap) {
        this.service = service;
        this.worldMap = worldMap;
    }

    @Override
    protected boolean execute(CommandSender sender) {
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1, String arg2) {
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

        String targetUid = service.selectSteveUid(arg2);

        long sharePermit;
        long switchPermit;
        if (domain.getShareMap().containsKey(targetUid)) {
            // 如果已经存在 修改替换
            sharePermit = domain.getShareMap().get(targetUid);
            switchPermit = dimension.switchPermit(sharePermit, permit);
            domain.setShare(domain.getShare().replace(targetUid + ":" + sharePermit, targetUid + ":" + switchPermit));
            domain.getShareMap().put(targetUid, switchPermit);
        } else {
            // 不存在 先获取关系基础权限 再修改加入
            Ship ship = service.selectShip(UUID.fromString(domain.getOwner()), UUID.fromString(targetUid));
            if (ship == null) {
                sharePermit = domain.getGlobe();
            } else if (ship.isIntimate()) {
                sharePermit = domain.getIntimate();
            } else {
                sharePermit = domain.getFriend();
            }
            switchPermit = dimension.switchPermit(sharePermit, permit);
            domain.setShare(domain.getShare()+ " " + targetUid + ":" + switchPermit);
            domain.getShareMap().put(targetUid, switchPermit);
        }
        service.updateDomainShare(domain.getShare(), domain.getName());
        player.sendMessage(Language.PLOT_FLAG_SWITCH
            .replace("%domain%", domain.getName())
            .replace("%flag%", arg1) 
            .replace("%scope%", "玩家" + arg2) 
            + (dimension.isPermit(domain.getIntimate(), permit) ? "§a开启" : "§c关闭"));
        return true;
    }
    
}
