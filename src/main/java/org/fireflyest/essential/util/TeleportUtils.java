package org.fireflyest.essential.util;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.data.Language;

public class TeleportUtils {
    
    private static final Set<String> waiting = new HashSet<>();

    private TeleportUtils() {
    }

    /**
     * 传送
     * @param player 玩家
     * @param loc 位置
     * @param vip 是否无延迟
     */
    public static void teleportTo(Player player, Location loc, boolean vip) {
        if (waiting.contains(player.getName())) {
            player.sendMessage(Language.TITLE + "waiting...");
            return;
        }
        if (loc == null) {
            player.sendMessage(Language.TITLE + "location error!");
            return;
        }
        if (!loc.getChunk().isLoaded()) {
            loc.getChunk().load();
        }
        long t = vip ? 1 : 3;
        player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_AMBIENT, 1, 1);
        Location firstLoc = player.getLocation();
        new BukkitRunnable() {
            @Override
            public void run() {
                Location nowLoc = player.getLocation();
                if (firstLoc.getWorld().equals(nowLoc.getWorld()) && nowLoc.distance(firstLoc) < 1.5) {
                    player.teleport(loc);
                } else {
                    player.sendMessage(Language.CANCEL_WAITING);
                }
            }
        }.runTaskLater(Essential.getPlugin(), t * 20);

        new BukkitRunnable() {
            final BossBar coolbar = Bukkit.getServer().createBossBar("", BarColor.WHITE, BarStyle.SOLID, BarFlag.PLAY_BOSS_MUSIC);
            double time = t;
            boolean apply = false;
            @Override
            public void run() {
                if (!apply) {
                    coolbar.addPlayer(player);
                    if (!coolbar.isVisible()) {
                        coolbar.setVisible(true);
                    }
                    apply = true;
                }
                // 判断时间是否到了
                if (time <= 0) {
                    waiting.remove(player.getName());
                    coolbar.setVisible(false);
                    coolbar.removeAll();
                    cancel();
                    return;
                } else {
                    coolbar.setProgress(time / t);
                }
                time -= 0.05;
            }
        }.runTaskTimerAsynchronously(Essential.getPlugin(), 0L, 1L);
    }

}
