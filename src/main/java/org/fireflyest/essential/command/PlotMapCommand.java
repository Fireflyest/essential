package org.fireflyest.essential.command;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.craftitem.interact.InteractAction;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.essential.world.Dimension;
import org.fireflyest.essential.world.DomainMapRenderer;

public class PlotMapCommand extends SubCommand {

    private final Map<String, Dimension> worldMap;

    private final Map<String, MapRenderer> rendererMap = new HashMap<>();

    public PlotMapCommand(EssentialService service, Map<String, Dimension> worldMap) {
        this.worldMap = worldMap;
        // this.service = service;
    }

    @Override
    protected boolean execute(CommandSender sender) {
        final Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!item.getType().equals(Material.FILLED_MAP)) {
            return true;
        }
        MapMeta meta = ((MapMeta)item.getItemMeta());
        MapView mapView = meta.getMapView();

        String worldName = player.getWorld().getName();
        // 判断该世界是否记录
        Dimension dimension = worldMap.get(worldName);
        if (dimension == null) {
            player.sendMessage(Language.PLOT_WORLD_UNKNOWN);
            return true;
        }

        mapView.setCenterX((int)player.getLocation().getX());
        mapView.setCenterZ((int)player.getLocation().getZ());

        MapRenderer mapRenderer = rendererMap.computeIfAbsent(worldName, k -> new DomainMapRenderer(dimension));
        mapView.addRenderer(mapRenderer);
        meta.setMapView(mapView);
        item.setItemMeta(meta);

        return true;
    }
    
}
