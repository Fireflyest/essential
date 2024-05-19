package org.fireflyest.essential.listener;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.data.Config;
import org.fireflyest.essential.data.StateCache;
import org.fireflyest.essential.util.LocateUtils;

public class ServerEventListener implements Listener {
    
    private final Map<String, BufferedImage> imgMap = new HashMap<>();

    private final StateCache cache;

    public ServerEventListener(StateCache cache) {
        this.cache = cache;

        File folder = new File(Essential.getPlugin().getDataFolder(), "logo");
        folder.mkdirs();
        for (File file : folder.listFiles()) {
            if (!file.getName().endsWith("png")) {
                continue;
            }
            String name = file.getName().replace(".png", "");
            try {
                BufferedImage image = ImageIO.read(new  FileInputStream(file));
                imgMap.put(name, image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        // 第一行
        String tip;
        String icon;
        switch (LocalDate.now().getDayOfWeek()) {
            case FRIDAY:
                icon = "battle";
                tip = "§7“§e§l战斗之夜！§7“";
                break;
            case SATURDAY:
            case SUNDAY:
                icon = "game";
                tip = "§7“§e§l狂欢周末！§7“";
                break;
            case MONDAY:
            case TUESDAY:
            case WEDNESDAY:
            case THURSDAY:
            default:
                icon = "default";
                tip = "§7“§e§l你好，" + LocateUtils.locate(event.getAddress().toString().substring(1)) + "！§7“";
                break;
        }
        if (cache.get(StateCache.SERVER_MAINTAIN) != null) {
            icon = "maintain";
            tip = "§7“§c§l正在维护！§7“";
        }

        BufferedImage compressedImage = imgMap.get(icon);
        if (compressedImage != null) {
            try {
                event.setServerIcon(Bukkit.loadServerIcon(compressedImage));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 人数
        event.setMaxPlayers(LocalDateTime.now().getYear());
        // 第二行
        event.setMotd("§b⌈§7§lC§8§lE§b⌉               " +tip + "§6§l\n§b⌊§8§lD§7§lG§b⌋                §6§l" + Config.WEBSITE);

    }

    

}
