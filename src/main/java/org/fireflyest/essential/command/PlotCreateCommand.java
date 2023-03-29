package org.fireflyest.essential.command;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.essential.data.Config;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.essential.world.Dimension;
import org.fireflyest.essential.world.Dimension.EventResult;

public class PlotCreateCommand extends SubCommand {

    private EssentialService service;
    private Map<String, Dimension> worldMap;

    public PlotCreateCommand(EssentialService service, Map<String, Dimension> worldMap) {
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
        String loc = player.getLocation().getChunk().getX() + ":" + player.getLocation().getChunk().getZ();
        return this.execute(sender, arg1, loc);
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1, String arg2) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        // 重名
        if (!"".equals(service.selectDomainOwner(arg1))) {
            sender.sendMessage(Language.PLOT_EXIST);
            return true;
        }
        // 上限
        if (service.selectDomainsByPlayer(player.getUniqueId()).length > Config.MAX_DOMAIN) {
            sender.sendMessage(Language.PLOT_UPPER_LIMIT);
            return true;
        }
        // 是否有创建领地的权限
        String worldName = player.getWorld().getName();
        String permission = "essential.domain." + worldName;
        if (!sender.hasPermission(permission))  {
            sender.sendMessage(Language.NOT_PERMISSION.replace("%permission%", permission));
            return true;
        }
        // 判断该世界是否记录
        Dimension dimension = worldMap.get(worldName);
        if (dimension == null) {
            sender.sendMessage(Language.PLOT_WORLD_UNKNOWN);
            return true;
        }
        // 是否空区块
        EventResult result = dimension.canCreate(arg2);
        if (!result.isAllow()) {
            switch (result.getType()) {
                case EventResult.IN_DOMAIN:
                    sender.sendMessage(Language.PLOT_IN_DOMAIN);
                    return true;
                case EventResult.IN_LEAGUE:
                    sender.sendMessage(Language.PLOT_IN_LEAGUE);
                    return true;
                case EventResult.SERVER_PROTECT:
                    sender.sendMessage(Language.PLOT_SERVER_PROTECT);
                    return true;
                case EventResult.IN_ROAD:
                case EventResult.IN_SHARE_ROAD:
                    sender.sendMessage(Language.PLOT_IN_ROAD);
                    return true;
                default:
                    break;
            }
        }
        // 插入领地
        int x = player.getLocation().getChunk().getX();
        int z = player.getLocation().getChunk().getZ();
        String loc = x + ":" + z;
        if (loc.equals(arg2)) {
            dimension.createDomain(arg1, player.getUniqueId(), player.getLocation());
        } else {
            String[] xz = arg2.split(":");
            x = NumberConversions.toInt(xz[0]);
            z = NumberConversions.toInt(xz[1]);
            Location point = new Location(player.getWorld(), x * 16.0 + 8, player.getWorld().getHighestBlockYAt(x * 16 + 8, z * 16 + 8), z * 16.0 + 8);
            dimension.createDomain(arg1, player.getUniqueId(), point);
        }

        sender.sendMessage(Language.PLOT_CREATE + arg1);
        player.performCommand("plot map");
        return true;
    }
    
}
