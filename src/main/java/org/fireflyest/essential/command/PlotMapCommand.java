package org.fireflyest.essential.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.essential.bean.Domain;
import org.fireflyest.essential.data.Config;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.service.EssentialEconomy;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.essential.world.Dimension;
import org.fireflyest.essential.world.Plot;
import org.fireflyest.essential.world.Dimension.EventResult;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class PlotMapCommand extends SubCommand {

    private final Map<String, String> biomeMap = new HashMap<>();
    private Map<String, Dimension> worldMap;
    private EssentialService service;
    private EssentialEconomy economy;


    public PlotMapCommand(EssentialService service, EssentialEconomy economy, Map<String, Dimension> worldMap) {
        this.service = service;
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
        Location point = player.getLocation();

        Dimension dimension = worldMap.get(point.getWorld().getName());
        if (dimension == null) {
            sender.sendMessage(Language.PLOT_WORLD_UNKNOWN);
            return true;
        }

        int x = point.getChunk().getX();
        int z = point.getChunk().getZ();
        sender.sendMessage("");
        ComponentBuilder buttonBuilder = new ComponentBuilder()
            .append("§f使用§eF3+Q§f可开启区块范围显示 点击地图可创建或扩张地皮  ").reset()
            .append("🔄")
            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§f点击刷新")))
            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/plot map"))
            .append(" ").reset()
            .append("🚩")
            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§f占领脚下地皮")))
            .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/plot create " + player.getName() + "的地皮"));
        player.spigot().sendMessage(buttonBuilder.create());
        sender.sendMessage("§7\uf600§f\uf601\uf601\uf601§7\uf601§f\uf601\uf601\uf601§7\uf601§f\uf601\uf601\uf601§7\uf601§f\uf601\uf601\uf601§7\uf601§f\uf601\uf601\uf601§7\uf601§f\uf601\uf601\uf601§7\uf602");
        for (int i = z - 7; i <= z + 7; i++) {
            ComponentBuilder componentBuilder = new ComponentBuilder((z == i || z + 4 == i || z - 4 == i) ? "§7\uf603" : "§f\uf603").reset();
            for (int j = x - 11; j <= x + 11; j++) {
                String loc = j + ":" + i;
                // 地图显示方块
                String biomeKey = player.getWorld().getName() + loc;
                String block = biomeMap.get(biomeKey);
                if (block == null) {
                    Chunk chunk = player.getWorld().getChunkAt(j, i);
                    ChunkSnapshot snapshot = chunk.getChunkSnapshot(false, true, false);
                    Biome biome = snapshot.getBiome(8, player.getWorld().getHighestBlockYAt(j * 16 + 8, i * 16 + 8) + 10, 8);
                    switch (biome) {
                        default:
                        case PLAINS:
                        case SAVANNA:
                        case SAVANNA_PLATEAU:
                        case WINDSWEPT_HILLS:
                        case SUNFLOWER_PLAINS:
                        case FLOWER_FOREST:
                            block = "\uf0fb";
                            break;
                        case OCEAN:
                        case DEEP_OCEAN:
                        case RIVER:
                        case WARM_OCEAN:
                        case LUKEWARM_OCEAN:
                        case COLD_OCEAN:
                        case DEEP_LUKEWARM_OCEAN:
                        case DEEP_COLD_OCEAN:
                            block = "\uf055";
                            break;
                        case DESERT:
                        case BEACH:
                            block = "\uf09e";
                            break;
                        case FOREST:
                        case TAIGA:
                        case JUNGLE:
                        case SPARSE_JUNGLE:
                        case BIRCH_FOREST:
                        case DARK_FOREST:
                            block = "\uf4a9";
                            break;
                        case NETHER_WASTES:
                            block = "\uf0db";
                            break;
                        case THE_END:
                            block = "\uf0a4";
                            break;
                        case FROZEN_OCEAN:
                        case FROZEN_RIVER:
                        case DEEP_FROZEN_OCEAN:
                        case ICE_SPIKES:
                            block = "\uf0c2";
                            break;
                        case SNOWY_PLAINS:
                        case SNOWY_BEACH:
                        case SNOWY_TAIGA:
                        case FROZEN_PEAKS:
                        case JAGGED_PEAKS:
                        case STONY_PEAKS:
                            block = "\uf0ef";
                            break;
                        case MUSHROOM_FIELDS:
                            block = "\uf0fc";
                            break;
                        case STONY_SHORE:
                            block = "\uf06e";
                            break;
                        case THE_VOID:
                            block = "\uf58f";
                            break;
                    }
                    biomeMap.put(biomeKey, block);
                }

                // 是否在地皮内
                Plot plot = dimension.getPlot(loc);
                if (plot != null) {
                    Domain domain = plot.getDomain();
                    String ownerName = service.selectSteveName(domain.getOwner());
                    switch (plot.getDomain().getType()) {
                        case EventResult.IN_DOMAIN:
                            if (domain.getOwner().equals(player.getUniqueId().toString())) {
                                block = "⬜";
                                Text textDomain = new Text("§e§l" + loc + "\n§7我的地皮" + "\n§3名称§3: §f" + domain.getName());
                                componentBuilder.append("§2" + block).reset()
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, textDomain))
                                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/plot tp " + domain.getName()));   
                            } else {
                                block = "⬜";
                                Text textDomain = new Text("§e§l" + loc + "\n§7私人地皮" + "\n§3名称§3: §f" + domain.getName() + "\n§3归属§3: §f" + ownerName);
                                componentBuilder.append("§b" + block).reset()
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, textDomain))
                                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/plot tp " + domain.getName()));   
                            }
                            break;
                        case EventResult.IN_LEAGUE:
                            block = "⬜";
                            Text textLeague = new Text("§e§l" + loc + "\n§7联盟地皮" + "\n§3联盟§3: §f" + domain.getName() + "\n§3归属§3: §f" + ownerName);
                            componentBuilder.append("§6" + block).reset()
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, textLeague))
                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/plot tp " + domain.getName()));              
                            break;
                        case EventResult.SERVER_PROTECT:
                            block = "⬜";
                            Text textServer = new Text("§e§l" + loc + "\n§7服务器占有" + "\n§3名称§3: §f" + domain.getName());
                            componentBuilder.append("§c" + block).reset()
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, textServer))
                                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ""));              
                            break;
                        default:
                            componentBuilder.append("§f" + block).reset()
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§e§l" + loc + "\n§7无主之地\n§7点击获取在此创建地皮的指令")));
                            break;
                    }
                } else {
                    // 是否在道路上
                    boolean canExpand = false;
                    double cost = 0;
                    String expandDomain = "";
                    List<Domain> roadBelong = dimension.getRoadBelong(loc);
                    if (roadBelong != null) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (Domain domain : roadBelong) {
                            if (!canExpand && domain.getOwner().equals(player.getUniqueId().toString())) {
                                canExpand = true;
                                cost = Config.BASE_CHUNK_PRICE + (domain.getLevel() * domain.getLevel() * (Config.BASE_CHUNK_PRICE / 5.0));
                                expandDomain = domain.getName();
                            }
                            String color = "§f";
                            if (domain.getType() == EventResult.IN_DOMAIN) {
                                color = "§e";
                            } else if (domain.getType() == EventResult.IN_LEAGUE) {
                                color = "§6";
                            } else if (domain.getType() == EventResult.SERVER_PROTECT) {
                                color = "§c";
                            }
                            stringBuilder.append("\n§f• ").append(color).append(domain.getName());
                        }
                        if (roadBelong.size() > 1) {
                            componentBuilder.append("§8" + block).reset()
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§e§l" + loc + "\n§7共享道路§3" + stringBuilder.toString())))
                                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ""));
                        } else {
                            Text text = new Text("§e§l" + loc + "\n§7独占道路" + (canExpand ? "\n§7点击扩张地皮§f" + expandDomain + "\n§7花费§f" + cost + economy.currencyNameSingular() : "") + "§3" + stringBuilder.toString());
                            componentBuilder.append("§7" + block).reset()
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, text))
                                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/plot expand " + j + ":" + i));
                        }
                    } else {
                        // 无人占领区
                        componentBuilder.append("§f" + block).reset()
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§e§l" + loc + "\n§7点击获取建地皮指令\n§7地皮默认名称为§f" + player.getName() + "的地皮")))
                            .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/plot create " + player.getName() + "的地皮 " + j + ":" + i));
                    }
                } 
            }
            componentBuilder.append((z == i || z + 4 == i || z - 4 == i) ? "§7\uf604" : "§f\uf604").reset();
            player.spigot().sendMessage(componentBuilder.create());
        }
        sender.sendMessage("§7\uf605§f\uf606\uf606\uf606§7\uf606§f\uf606\uf606\uf606§7\uf606§f\uf606\uf606\uf606§7\uf606§f\uf606\uf606\uf606§7\uf606§f\uf606\uf606\uf606§7\uf606§f\uf606\uf606\uf606§7\uf607");
        sender.sendMessage("§2⬜私人地皮 §6⬜联盟地皮 §c⬜服务器占有 §7⬜独占道路 §8⬜共享道路");
        return true;
    }
    
}
