package org.fireflyest.essential.listener;

import java.time.Instant;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.fireflyest.craftgui.api.ViewGuide;
import org.fireflyest.craftgui.util.TranslateUtils;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.bean.Steve;
import org.fireflyest.essential.data.Config;
import org.fireflyest.essential.data.EssentialYaml;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.data.StateCache;
import org.fireflyest.essential.service.EssentialPermission;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.essential.util.DeathMsgUtils;
import org.fireflyest.util.ItemUtils;
import org.fireflyest.util.SerializationUtil;

public class PlayerEventListener implements Listener {
    
    private EssentialService service;
    private EssentialYaml yaml;
    private EssentialPermission permission;
    private StateCache cache;
    private ViewGuide guide;

    private final Pattern aitePattern = Pattern.compile("@[a-z0-9A-Z][^ ]+");
    private final Pattern varPattern = Pattern.compile("%([^%]*)%");

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
    public PlayerEventListener(EssentialService service, EssentialYaml yaml, EssentialPermission permission, StateCache cache, ViewGuide guide) {
        this.service = service;
        this.yaml = yaml;
        this.permission = permission;
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
        UUID uid = player.getUniqueId();
        
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
            service.insertSteve(player.getName(), uid, Instant.now().toEpochMilli(), yaml.getGroup().getString("default.prefix"));
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
                    // 观察者模式
                    player.setGameMode(GameMode.SPECTATOR);
                    if (guide.unUsed(player.getName())) {
                        guide.openView(player, Essential.VIEW_ACCOUNT, player.getName());
                    }
                }
            }.runTaskTimer(Essential.getPlugin(), 30, 60);
        }

        // 刷新权限
        permission.refreshPlayerPermission(player);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (cache.get(player.getName() + ".base.mute") != null) {
            player.sendMessage(Language.TITLE + "禁言还剩§3" + cache.ttl(player.getName() + ".base.mute") + "秒");
            event.setCancelled(true);
        }

        World world = player.getWorld();
        String chatRangeName;
        String chatRangeColor;
        // 聊天范围
        String range = cache.get(player.getName() + ".chat.range");
        if ("globe".equals(range) || message.contains("%room%")) { // 全部可见
            chatRangeName = yaml.getWorld().getString(world.getName() + ".display");
            chatRangeColor = yaml.getWorld().getString(world.getName() + ".color");
        } else if (range == null) { // 附近可见
            chatRangeName = "附近";
            chatRangeColor = "c=#747d8c";
            // 附近人可接收
            Iterator<Player> iterator = event.getRecipients().iterator();
            while (iterator.hasNext()) {
                Player p = iterator.next();
                if (!p.getWorld().equals(world) || p.getLocation().distance(player.getLocation()) > 180) {
                    iterator.remove();
                }
            }
        } else { // 群聊
            Set<String> smembers = cache.smembers(range);
            if (smembers == null) {
                cache.del(player.getName() + ".chat.range");
                chatRangeName = yaml.getWorld().getString(world.getName() + ".display");
                chatRangeColor = yaml.getWorld().getString(world.getName() + ".color");
            } else {
                chatRangeName = range.substring(range.lastIndexOf(".") + 1);
                chatRangeColor = "c=#9b59b6";
                event.getRecipients().clear();
                cache.expire(range, 60 * 60 * 3);
                for (String smember : smembers) {
                    event.getRecipients().add(Bukkit.getPlayerExact(smember));
                }
            }
        }

        // 处理聊天格式
        String prefix = service.selectStevePrefix(player.getUniqueId());
        prefix = prefix.replace("<", "<he=show_text•点击切换头衔|ce=run_command•/prefix|");
        event.setFormat("§f[$<he=show_text•点击切换聊天范围|ce=run_command•/chat|"
                 + chatRangeColor + ">" + chatRangeName + "§f]§f[" // 聊天范围显示
                 + prefix + "§f]$<he=show_text•点击交互|ce=run_command•/interact " // 点击头衔切换
                 + player.getName() + "|c=#b8e994>%1$s §7▷§r %2$s"); // 点击名称交互

        // 处理聊天内容
        if (message.contains("@")) {
            Matcher matcher = aitePattern.matcher(event.getMessage());
            while (matcher.find()) {
                String aite = matcher.group();
                Player target = Bukkit.getPlayer(aite.substring(1));
                if (target != null) {
                    message = message.replace(aite, "$<c=#f8c291>@" + target.getName() + "§r");
                    target.sendTitle("", "在聊天中提到你", 10, 40, 20);
                    target.playSound(target.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 5, 5);
                }
            }
            event.setMessage(message);
        }

        // 聊天变量
        if (message.contains("%")) {
            Matcher matcher = varPattern.matcher(event.getMessage());
            if (matcher.find()) {
                String value = matcher.group();
                switch (value) {
                    case "%item%":
                        ItemStack itemStack = player.getInventory().getItemInMainHand();
                        message = message.replace(value, "§7($<he=show_item•minecraft:" + itemStack.getType().toString().toLowerCase() 
                                + "•" + ItemUtils.toNbtString(itemStack) 
                                + "|c=#f8c291>物品§7)§r");
                        break;
                    case "%room%":
                        String room;
                        if (range != null && range.contains(".")) {
                            room = range.substring(range.lastIndexOf(".") + 1);
                        } else {
                            room = "群聊";
                        }
                        message = message.replace(value, "§7($<he=show_text•点击加入群聊|"
                            + "ce=run_command•/chat " + room
                            + "|c=#f8c291>" +room + "§7)§r");
                        break;
                    default:
                        break;
                }
            }
            event.setMessage(message);
        }

        // 聊天颜色
        if (player.hasPermission("essential.chat.color") && message.contains("&")) {
            message = message.replace("&", "§");
            event.setMessage(message);
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
                service.updateQuit(player.getLocation(), player.getUniqueId());
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
        }.runTaskLater(Essential.getPlugin(), 30);
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        String title = yaml.getWorld().getString(world.getName() + ".display");
        String message = yaml.getWorld().getString(world.getName() + ".message");
        player.sendTitle(title, message, 10, 70, 20);
    }

}
