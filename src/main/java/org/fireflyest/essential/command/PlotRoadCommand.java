package org.fireflyest.essential.command;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.NumberConversions;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.bean.Domain;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.data.StateCache;
import org.fireflyest.essential.world.Dimension;
import org.fireflyest.essential.world.Plot;

public class PlotRoadCommand extends SubCommand {

    private Map<String, Dimension> worldMap;
    private StateCache cache;

    public PlotRoadCommand(StateCache cache, Map<String, Dimension> worldMap) {
        this.cache = cache;
        this.worldMap = worldMap;
    }

    @Override
    protected boolean execute(CommandSender sender) {
        return execute(sender, "64");
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        return execute(sender, arg1, "none");
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

        // 再次确认
        String key = player.getName() + ".command.confirm";
        if (!cache.exist(key) || !"road".equals(cache.get(key))) {
            cache.setex(key, 30, "road");
            sender.sendMessage(Language.COMMAND_CONFIRM);
            return true;
        }

        // 平整
        int height = NumberConversions.toInt(arg1);
        int num = 0;
        for (String nearRoad : dimension.nearRoads(domain)) {
            String[] xz = nearRoad.split(":");
            Chunk chunk = player.getWorld().getChunkAt(NumberConversions.toInt(xz[0]), NumberConversions.toInt(xz[1]));
            this.flat(chunk, height, num++);
        }

        // 道路

        return true;
    }
    
    private void flat(Chunk chunk, int height, int delay) {
        int worldX = chunk.getX() * 16;
        int worldZ = chunk.getZ() * 16;
        new BukkitRunnable() {
            public void run() {
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < 16; j++) {
                        int highest = chunk.getWorld().getHighestBlockYAt(worldX + i, worldZ + j) + 1;
                        if (highest < height + 1) {
                            highest = height + 1;
                        }
                        for (int k = height; k < highest; k++) {
                            Block block = chunk.getBlock(i, k, j);
                            block.setType(k == height ? Material.GRASS_BLOCK : Material.AIR);
                        }
                    }
                }
            }
        }.runTaskLater(Essential.getPlugin(), delay * 20L);
    }

}
