package org.fireflyest.essential.listener;

import java.time.Instant;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.fireflyest.craftgui.api.ViewGuide;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.bean.Steve;
import org.fireflyest.essential.data.Config;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.data.StateCache;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.essential.util.DeathMsgUtils;
import org.fireflyest.util.SerializationUtil;

public class PlayerEventListener implements Listener {
    
    private EssentialService service;
    private StateCache cache;
    private ViewGuide guide;

    private final String motdHeader = 
            "   .·*ᄽ´¯`§7*·.¸¸ᄿ*·.  §e§l◎  §6§lEdgeCraft§r  §e§l◎ §7 .·*ᄽ¸¸.·*§f´¯`ᄿ*·.   \n"
            + "\n"
            + "§f[ §amc.craftedge.cn §f]\n"
            + "";

    /**
     * 玩家事件监听
     * @param service 数据服务
     * @param cache 缓存
     */
    public PlayerEventListener(EssentialService service, StateCache cache, ViewGuide guide) {
        this.service = service;
        this.cache = cache;
        this.guide = guide;
    }

    /**
     * 玩家登入
     * @param event 事件
     */
    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setPlayerListHeader(motdHeader);
    }

    /**
     * 玩家加入
     * @param event 加入事件
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String uid = player.getUniqueId().toString();
        
        // 传送到主世界
        World mainWorld = Bukkit.getWorld(Config.MAIN_WORLD);
        if (mainWorld != null) {
            player.teleport(mainWorld.getSpawnLocation());
        }

        // 进入提示
        event.setJoinMessage("§6[§a+§6]§f" + player.getName());
        
        
        // 玩家数据是否为空
        Steve steve = service.selectSteveByUid(uid);
        if (steve == null) {
            Bukkit.broadcastMessage(Language.NEW_PLAYER.replace("%player%", player.getName()));

            service.insertSteve(player.getName(), uid, Instant.now().toEpochMilli());
            steve = service.selectSteveByUid(uid);
        }

        // 是否已经注册
        if ("".equals(steve.getPassword())) {
            cache.set(player.getName() + ".account.state", StateCache.UN_REGISTER);
            player.sendMessage(Language.REGISTER);
        } else {
            cache.set(player.getName() + ".account.state", StateCache.UN_LOGIN);
            player.sendMessage(Language.DON_LOGIN);
        }

        // 观察者模式
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setGameMode(GameMode.SPECTATOR);
            }
        }.runTaskLater(Essential.getPlugin(), 2);

        
        if (cache.exist(player.getName() + ".account.auto")) {
            // 自动登录
            new BukkitRunnable() {
                @Override
                public void run() {
                    cache.set(player.getName() + ".account.state", StateCache.LOGIN);
                    Location quit = service.selectQuit(uid);
                    player.setGameMode(GameMode.SURVIVAL);
                    player.sendMessage(Language.AUTO_LOGIN);
                    if (quit != null) {
                        player.teleport(quit);
                    }
                }
            }.runTaskLater(Essential.getPlugin(), 2);
        } else {
            // 手动登录
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (StateCache.LOGIN.equals(cache.get(player.getName() + ".account.state"))
                            || !player.isOnline()) {
                        cancel();
                        return;
                    }
                    if (guide.unUsed(player.getName())) {
                        guide.openView(player, Essential.VIEW_ACCOUNT, player.getName());
                    }
                }
            }.runTaskTimer(Essential.getPlugin(), 3, 60);
        }
    }

    /**
     * 玩家登入，登入在加入之前
     * @param event 登入事件
     */
    @EventHandler
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        // 保存地址自动登录
        String key = event.getName() + ".account.address";
        // 当前登录ip
        String ip = event.getAddress().toString();
        // 最后登录ip
        String lastIp = cache.get(key);
        // 是否保留自动登录
        if (lastIp == null || !ip.equals(lastIp)) {
            cache.del(event.getName() + ".account.auto");
        }
        // 保存新的ip
        cache.set(key, event.getAddress().toString());
        //名称是否合法
        if (!event.getName().matches("[0-9a-zA-Z_]+") || event.getName().length() > 30) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Language.ILLEGAL_NAME);
        }
    }

    /**
     * 玩家离开
     * @param event 离开事件
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        event.setQuitMessage("§6[§c-§6]§f" + name);

        // 是否已登录
        if (!StateCache.LOGIN.equals(cache.get(name + ".account.state"))) {
            return;
        }
        // 记录最后在线，五分钟内自动登录
        cache.setex(name + ".account.auto", 300, name);
        // 保存下线位置
        new BukkitRunnable() {
            @Override
            public void run() {
                service.updateQuit(player.getLocation(), player.getUniqueId().toString());
            }
        }.runTaskAsynchronously(Essential.getPlugin());
    }

    /**
     * 指令输入判断，未登录不能输入其他指令
     * @param event 指令输入事件
     */
    @EventHandler
    public void onSendCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        if (StateCache.LOGIN.equals(cache.get(name + ".account.state"))) {
            return;
        }
        String str = event.getMessage();
        if (str.length() >= 3 && str.startsWith("l ", 1)) {
            return;
        }
        if (str.length() >= 5 && str.startsWith("reg ", 1)) {
            return;
        }
        if (str.length() >= 7 && str.startsWith("login ", 1)) {
            return;
        }
        if (str.length() >= 10 && str.startsWith("register ", 1)) {
            return;
        }
        if (str.length() >= 15 && str.startsWith("account losepw", 1)) {
            return;
        }

        if (StateCache.UN_REGISTER.equals(cache.get(name + ".account.state"))) {
            player.sendMessage(Language.REGISTER);
        } else {
            player.sendMessage(Language.DON_LOGIN);
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location loc = player.getLocation();
        cache.set(player.getName() + ".base.back", SerializationUtil.serialize(loc));
        player.sendMessage(Language.SAVE_POINT);

        String name = event.getEntity().getName();
        String msg = event.getDeathMessage();
        EntityDamageEvent entityDamageEvent = event.getEntity().getLastDamageCause();
        if (entityDamageEvent != null) {
            msg = DeathMsgUtils.convertDeathMsg(name, msg, entityDamageEvent.getCause());
            if (msg != null) {
                event.setDeathMessage(msg);
            }
        }
        event.getEntity().setBedSpawnLocation(Objects.requireNonNull(Bukkit.getWorld(Config.MAIN_WORLD)).getSpawnLocation(), true);

        new BukkitRunnable() {
            @Override
            public void run() {
                event.getEntity().spigot().respawn();
            }
        }.runTask(Essential.getPlugin());
    }

}
