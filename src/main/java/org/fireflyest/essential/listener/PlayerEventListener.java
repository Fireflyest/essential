package org.fireflyest.essential.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
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
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.fireflyest.craftgui.api.ViewGuide;
import org.fireflyest.craftmsg.MessageService;
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
    private MessageService message;
    private ViewGuide guide;
    private final ItemStack menu;

    private final Pattern aitePattern = Pattern.compile("@[a-z0-9A-Z][^ ]+");
    private final Pattern varPattern = Pattern.compile("%([^%]*)%");

    private final String motdHeader = 
            "   .·*ᄽ´¯`§7*·.¸¸ᄿ*·.  §e§l◎  §6§l%s§r  §e§l◎ §7 .·*ᄽ¸¸.·*§f´¯`ᄿ*·.   \n"
            + "\n"
            + "§f[ §a%s §f]\n"
            + "";

    /**
     * 玩家事件监听
     * @param service 数据服务
     * @param cache 缓存
     */
    public PlayerEventListener(EssentialService service, EssentialYaml yaml, EssentialPermission permission, StateCache cache, MessageService message, ViewGuide guide) {
        this.service = service;
        this.yaml = yaml;
        this.permission = permission;
        this.cache = cache;
        this.message = message;
        this.guide = guide;

        for (Player player : Bukkit.getOnlinePlayers()) {
            cache.set(player.getName() + StateCache.ACCOUNT_STATE, StateCache.LOGIN);
        }

        this.menu = yaml.getItemBuilder("menu").build();
    }

    /**
     * 玩家登入
     * @param event 事件
     */
    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setPlayerListHeader(String.format(motdHeader, Config.SERVER_NAME, Config.WEBSITE));

        if (!player.getInventory().contains(menu.getType())) {
            player.getInventory().addItem(menu.clone());
        }

        // 地块占领工具失效
        // int mapIndex = player.getInventory().first(Material.FILLED_MAP);
        // if (mapIndex != -1) {
        //     ItemStack item = player.getInventory().getItem(mapIndex);
        //     if (ItemUtils.getDisplayName(item).contains("圈地工具")) {
        //         player.getInventory().remove(item);
        //         player.getInventory().setItem(mapIndex, yaml.getItemBuilder("kit_default_01").build());
        //     }
        // }

        message.playerJoin(player);
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
        player.teleport(mainWorld.getSpawnLocation());

        // 进入提示
        // message.pushGlobalMessage(player.getName() + " §f[§a§l+§f]", 5);
        message.popGlobalMessage(player.getName() + "上线了");
        event.setJoinMessage(null);

        // 玩家数据
        Steve steve = service.selectSteveByUid(player.getUniqueId());
        // 是否已经注册
        cache.set(player.getName() + StateCache.ACCOUNT_STATE, "".equals(steve.getPassword()) ? StateCache.UN_REGISTER : StateCache.UN_LOGIN);
        
        // 刷新权限
        permission.refreshPlayerPermission(player);

        // 人少默认设置全局聊天
        cache.set(player.getName() + StateCache.CHAT_RANGE, Config.CHAT_RANGE);

        if ((steve.isLegal() && cache.exist(player.getName() + StateCache.ACCOUNT_AUTO)) || cache.exist(player.getName() + StateCache.ACCOUNT_AUTO) || Bukkit.getOnlineMode()) {
            // 自动登录
            new BukkitRunnable() {
                @Override
                public void run() {
                    cache.set(player.getName() + StateCache.ACCOUNT_STATE, StateCache.LOGIN);
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
                    if (StateCache.LOGIN.equals(cache.get(player.getName() + StateCache.ACCOUNT_STATE))
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
            }.runTaskTimer(Essential.getPlugin(), 60, 80);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        // 是否禁言
        if (cache.get(player.getName() + ".base.mute") != null) {
            player.sendMessage(Language.TITLE + "禁言还剩§3" + cache.ttl(player.getName() + ".base.mute") + "秒");
            event.setCancelled(true);
        }

        World world = player.getWorld();
        String chatRangeName;
        String chatRangeColor;
        // 聊天范围
        String range = cache.get(player.getName() + StateCache.CHAT_RANGE);
        if (message.length() > 3 && message.startsWith("?")) {
            chatRangeName = "求助";
            chatRangeColor = "c=#8c7ae6";
            // TODO 附近人可接收  聊天机器人
            Iterator<Player> iterator = event.getRecipients().iterator();
            while (iterator.hasNext()) {
                Player p = iterator.next();
                if (!p.getWorld().equals(world) || p.getLocation().distance(player.getLocation()) > 100) {
                    iterator.remove();
                }
            }
        } else if ("globe".equals(range) || message.startsWith("!")) { // 全部可见
            chatRangeName = yaml.getWorld().getString(world.getName() + ".display");
            chatRangeColor = yaml.getWorld().getString(world.getName() + ".color");
        } else if (range == null  || message.contains("%room%")) { // 附近可见
            chatRangeName = "附近";
            chatRangeColor = "c=#747d8c";
            // 附近人可接收
            Iterator<Player> iterator = event.getRecipients().iterator();
            while (iterator.hasNext()) {
                Player p = iterator.next();
                if (!p.getWorld().equals(world) || p.getLocation().distance(player.getLocation()) > 200) {
                    iterator.remove();
                }
            }
        } else { // 群聊
            Set<String> smembers = cache.smembers(range);
            if (smembers == null) {
                cache.del(player.getName() + StateCache.CHAT_RANGE);
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
        prefix = prefix.replace("<", "<he=show_text•头衔|ce=run_command•/prefix|"); // 点击头衔切换
        String headText = "§f[$<he=show_text•切换|ce=run_command•/chat|" // 聊天范围显示
            + chatRangeColor + ">" + chatRangeName + "$<c=#ffffff>][" 
            + prefix + "$<c=#ffffff>]$<he=show_text•交互|ce=run_command•/interact "  // 点击名称交互
            + player.getName() + "|c=#b8e994>";
        String headTextVar = UUID.randomUUID().toString().substring(0, 8);
        cache.setex(headTextVar, 10, headText);
        event.setFormat("§r`" + headTextVar + "`%1$s §7▷§r %2$s"); 

        // 处理聊天内容
        if (message.contains("@")) {
            Matcher matcher = aitePattern.matcher(event.getMessage());
            while (matcher.find()) {
                String aite = matcher.group();
                Player target = Bukkit.getPlayer(aite.substring(1));
                if (target != null) {
                    message = message.replace(aite, "§e@" + target.getName() + "§r");
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
                String textVar = UUID.randomUUID().toString().substring(0, 8);
                switch (value) {
                    case "%item%":
                    ItemStack itemStack = player.getInventory().getItemInMainHand();
                        String tag = ItemUtils.toNbtString(itemStack).replace("\"", "\\\"");
                        cache.setex(textVar, 10, "§7($<he=show_item•minecraft:" + itemStack.getType().toString().toLowerCase() 
                            + "•" + tag 
                            + "|c=#f8c291>物品§7)§r");
                        break;
                    case "%room%":
                        String room = range != null && range.contains(".") ? range.substring(range.lastIndexOf(".") + 1) : "群聊";
                        cache.setex(textVar, 10, "§7($<he=show_text•加入|"
                                + "ce=run_command•/chat " + room
                                + "|c=#f8c291>" +room + "§7)§r");
                        break;
                    default:
                        break;
                }
                message = message.replace(value, "`" + textVar + "`");
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
        // 维护的时候不给进
        if ("maintain".equals(cache.get("motd.type"))) {
            Set<OfflinePlayer> operators = Bukkit.getOperators();
            boolean isAllow = false;
            for (OfflinePlayer offlinePlayer : operators) {
                if (offlinePlayer.getName().equals(event.getName())) {
                    isAllow = true;
                    break;
                }
            }
            if (!isAllow) {
                event.disallow(Result.KICK_OTHER, "服务器维护中\n请留意官网或者群消息");
            }
        }

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
        if (!event.getName().matches("[0-9a-zA-Z_]{1,30}") || event.getName().length() > 30) {
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
        if (!cache.exist(name + StateCache.SERVERS_CHANGE)) {
            // message.pushGlobalMessage(name + " §f[§c§l-§f]", 5);
        }
        event.setQuitMessage(null);

        // 是否已登录
        if (!StateCache.LOGIN.equals(cache.get(name + StateCache.ACCOUNT_STATE))) {
            return;
        }
        // 记录最后在线，5分钟内自动登录
        cache.setex(name + StateCache.ACCOUNT_AUTO, 600, name);
        // 保存下线位置
        new BukkitRunnable() {
            @Override
            public void run() {
                service.updateQuit(player.getLocation(), player.getUniqueId());
            }
        }.runTaskAsynchronously(Essential.getPlugin());

        message.playerQuit(player);
    }

    /**
     * 指令输入判断，未登录不能输入其他指令
     * @param event 指令输入事件
     */
    @EventHandler
    public void onSendCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        if (StateCache.LOGIN.equals(cache.get(name + StateCache.ACCOUNT_STATE))) {
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

        if (StateCache.UN_REGISTER.equals(cache.get(name + StateCache.ACCOUNT_STATE))) {
            player.sendMessage(Language.REGISTER);
        } else {
            player.sendMessage(Language.DON_LOGIN);
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        // 保存死亡地点
        Location loc = player.getLocation();
        cache.set(player.getName() + ".base.back", SerializationUtil.serialize(loc));
        // 死亡信息
        String name = event.getEntity().getName();
        String msg = event.getDeathMessage();
        EntityDamageEvent entityDamageEvent = event.getEntity().getLastDamageCause();
        if (entityDamageEvent != null) {
            msg = DeathMsgUtils.convertDeathMsg(name, msg, entityDamageEvent.getCause());
            if (msg != null) {
                event.setDeathMessage(msg);
            }
        }
        // 强行复活到主城
        event.getEntity().setBedSpawnLocation(Objects.requireNonNull(Bukkit.getWorld(Config.MAIN_WORLD)).getSpawnLocation(), true);
        
        // 如果未开启死亡保护，判断是否保护特定物品
        final Map<Integer, ItemStack> itemMap = new HashMap<>();
        if (!Config.KEEP_INVENTORY) {
            Iterator<ItemStack> iterator = player.getInventory().iterator();
            int slot = 0;
            while (iterator.hasNext()) {
                ItemStack item = iterator.next();
                // TODO: 
                if (item != null && ((item.getType().isItem() && item.getType().getMaxDurability() > 0)
                        || slot == 39 // 头上戴的
                        || ItemUtils.hasCustomNBT(item)
                        || item.getType().name().endsWith("SKULL")
                        || item.getType().name().endsWith("HEAD")
                        || item.getType().name().endsWith("INGOT")
                        || item.getType().name().startsWith("RAW")
                        || item.getType().name().equals("EMERALD")
                        || item.getType().name().equals("DIAMOND")
                        || item.getType().name().equals("NETHERITE_SCRAP")
                        || item.getType() == Material.NETHER_STAR
                        || item.getType() == Material.BEACON
                        || item.getType() == Material.DRAGON_EGG)) {
                    itemMap.put(slot, item);
                    event.getDrops().remove(item);
                }
                slot++;
            }
        }
        
        // 自动复活
        new BukkitRunnable() {
            @Override
            public void run() {
                event.getEntity().spigot().respawn();
                for (Entry<Integer, ItemStack> entry : itemMap.entrySet()) {
                    player.getInventory().setItem(entry.getKey(), entry.getValue());
                }
            }
        }.runTaskLater(Essential.getPlugin(), 20);
        
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
