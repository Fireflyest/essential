package org.fireflyest.essential.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.NumberConversions;
import org.fireflyest.craftgui.api.ViewGuide;
import org.fireflyest.craftgui.api.ViewPage;
import org.fireflyest.craftitem.builder.ItemBuilder;
import org.fireflyest.crafttext.formal.TextInteractFormal;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.bean.Profile;
import org.fireflyest.essential.bean.Steve;
import org.fireflyest.essential.data.Config;
import org.fireflyest.essential.data.EssentialYaml;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.data.StateCache;
import org.fireflyest.essential.gui.AccountPage;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.essential.world.Dimension;
import org.fireflyest.essential.world.Plot;
import org.fireflyest.util.NetworkUtils;

public class EssentialProtocol {
    
    private final ProtocolManager protocolManager;
    private final ViewGuide guide;
    private final EssentialService service;
    private final EssentialYaml yaml;
    private final StateCache cache;
    private final Map<String, Dimension> worldMap;

    private final Map<String, String> ipMap = new HashMap<>();
    private final Queue<String> loginQueue = new ArrayDeque<>();
    private final Pattern pattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    private final Pattern varPattern = Pattern.compile("`([^`]*)`");
    private String lastPassword = ""; 

    byte[] bytes0 = new byte[] {48, -127, -97, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3, -127, -115, 0, 48, -127, -119, 2, -127, -127, 0, -123, -29, -51, 88, 16, -62, -27, -26, 109, -67, -111, 66, 111, 22, 105, 1, -14, 49, -94, 3, 79, -54, -69, -110, -73, 115, 24, 47, 63, 28, 3, -59, -114, 32, -80, 50, -49, 23, -46, 3, 37, -98, 66, 51, -53, -125, 78, -45, 72, -64, 37, -51, -25, -13, 112, 120, 79, -2, -30, -24, 28, 21, 65, -96, 27, 33, -111, -60, -42, 91, -15, 34, -39, 89, 46, 21, -30, 25, 37, 123, -128, 18, 74, -54, 106, 9, 118, 119, -102, 33, 68, 84, 78, 6, -19, -8, -16, 56, -47, 69, 14, -95, 127, 8, -107, 28, -7, -77, 24, 15, -97, -120, 105, -20, -81, 40, -19, -15, 78, -125, 103, 116, 101, -70, 22, 73, 79, -95, 2, 3, 1, 0, 1};
    byte[] bytes1 = new byte[] {-117, -78, -115, -103};

    /**
     * 输入框协议包
     * @param viewGuide 导航
     */
    public EssentialProtocol(ViewGuide guide, EssentialService service, EssentialYaml yaml, StateCache cache, Map<String, Dimension> worldMap) {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.guide = guide;
        this.service = service;
        this.yaml = yaml;
        this.cache = cache;
        this.worldMap = worldMap;
        
        this.setupLogin();
        this.setupChat();
        this.setupDomain();
    }

    /**
     * 聊天
     */
    private void setupChat() {
        // 聊天监听
        protocolManager.addPacketListener(
            new PacketAdapter(Essential.getPlugin(),
                    ListenerPriority.NORMAL,
                    PacketType.Play.Server.SYSTEM_CHAT) {

                @Override
                public void onPacketSending(PacketEvent event) {
                    // String componentJson = event.getPacket().getStrings().read(0);

                    WrappedChatComponent component = event.getPacket().getChatComponents().read(0);
                    String componentJson = component.getJson();

                    // 判断是否玩家聊天
                    // {"translate":"commands.setworldspawn.success","with":["172","-49","-40","0.0"]}
                    System.out.println(componentJson);
                    if (componentJson.contains("clickEvent") || componentJson.contains("hoverEvent") 
                        || componentJson.contains("translate") || !componentJson.contains("`")) {
                        return;
                    }
                    // 匹配变量
                    Matcher matcher = varPattern.matcher(componentJson);
                    while (matcher.find()) {
                        String varString = matcher.group();
                        componentJson = componentJson.replace(varString, cache.get(varString.substring(1, varString.length() - 1)));
                    }
                    TextInteractFormal text = new TextInteractFormal(componentJson);

                    // event.getPacket().getStrings().write(0, text.toString());

                    component.setJson(text.toString());
                    event.getPacket().getChatComponents().write(0, component);
                }
            }
        );
    }

    /**
     * 账户
     */
    private void setupLogin() {
        // 打开登录界面监听
        protocolManager.addPacketListener(
            new PacketAdapter(Essential.getPlugin(),
                    ListenerPriority.NORMAL,
                    PacketType.Play.Client.ITEM_NAME) {

                @Override
                public void onPacketReceiving(PacketEvent event) {
                    if (event.getPacketType() != PacketType.Play.Client.ITEM_NAME) {
                        return;
                    }
                    // 获取数据包
                    String playerName = event.getPlayer().getName();
                    PacketContainer packet = event.getPacket();

                    // 不是正在登录
                    if (guide.unUsed(playerName)) {
                        return;
                    }
                    // 判断是否登录界面
                    ViewPage page = guide.getUsingPage(playerName);
                    if (! (page instanceof AccountPage)) {
                        return;
                    }
                    AccountPage loginPage = ((AccountPage) page);
                    String password = packet.getStrings().read(0);

                    if ("".equals(password) || lastPassword.equals(password) || AccountPage.TIP_TEXT.equals(password)) {
                        return;
                    }
                    lastPassword = password;
                    loginPage.updatePassword(password);

                    guide.refreshPage(playerName);
                }
            }
        );

        /*
         * The login process is as follows:
         *  C→S: Handshake with Next State set to 2 (login)
         *  C→S: Login Start
         *  S→C: Encryption Request
         *  Client auth
         *  C→S: Encryption Response
         *  Server auth, both enable encryption
         *  S→C: Set Compression (optional)
         *  S→C: Login Success
         * For unauthenticated ("cracked"/offline-mode) 
         * and integrated servers (either of the two conditions is enough for an unencrypted connection) 
         * there is no encryption. In that case Login Start is directly followed by Login Success. 
         */
        protocolManager.addPacketListener(
            new PacketAdapter(Essential.getPlugin(),
                    ListenerPriority.NORMAL,
                    PacketType.Login.Client.START) {

                @Override
                public void onPacketReceiving(PacketEvent event) {
                    // WrappedGameProfile gameProfile = event.getPacket().getGameProfiles().read(0);
                    String playerName = event.getPacket().getStrings().read(0);
                    String ip = getIp(event.getPlayer());
                    Steve steve = service.selectSteveByName(playerName);
                    // 新玩家
                    if (steve == null) {
                        Bukkit.broadcastMessage(Language.NEW_PLAYER.replace("%player%", playerName));
                        String url = "https://api.mojang.com/users/profiles/minecraft/" + playerName + "?at=0";
                        Profile profile = NetworkUtils.doGet(url, Profile.class);
                        boolean legal = profile != null && profile.getId() != null;
                        // 正版模式
                        if (Bukkit.getOnlineMode()) {
                            if (!legal) {
                                // 离线玩家不插入数据
                                return;
                            }
                            StringBuilder stringBuilder = new StringBuilder(profile.getId());
                            stringBuilder.insert(8, "-");
                            stringBuilder.insert(13, "-");
                            stringBuilder.insert(18, "-");
                            stringBuilder.insert(23, "-");
                            String uid = stringBuilder.toString();
                            if ("".equals(service.selectSteveName(uid))) {
                                // 新玩家
                                service.insertSteve(playerName, uid, Instant.now().toEpochMilli(), Config.DEFAULT_PREFIX, Config.BASE_MONEY, true);
                                giveNewPlayerKits(playerName);
                            } else {
                                // 老玩家更名
                                service.updateSteveName(playerName, uid);
                            }
                            steve = service.selectSteveByUid(UUID.fromString(uid));
                        } else {
                            // 服务器在离线模式
                            String uid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + playerName).getBytes()).toString();
                            service.insertSteve(playerName, uid, Instant.now().toEpochMilli(), Config.DEFAULT_PREFIX, Config.BASE_MONEY, false);
                            giveNewPlayerKits(playerName);
                            steve = service.selectSteveByUid(UUID.fromString(uid));
                        }
                    }
                    // 服务器离线模式下正版自动登录
                    if (!Bukkit.getOnlineMode() && steve != null && steve.isLegal() && Config.AUTO_LOGIN) {
                        ipMap.putIfAbsent(playerName, ip);
                        // 换IP登入、未正版验证
                        if (!ipMap.get(playerName).equals(ip) || !cache.exist(playerName + StateCache.ACCOUNT_LEGAL)) {
                            if (cache.exist(playerName + StateCache.ACCOUNT_AUTO)) {
                                cache.setex(playerName + StateCache.ACCOUNT_LEGAL, 120, "");
                                return;
                            }
                            loginQueue.add(playerName);
                            PacketContainer ePacket = new PacketContainer(PacketType.Login.Server.ENCRYPTION_BEGIN);
                            ePacket.getStrings().write(0, "");
                            ePacket.getByteArrays().write(0, bytes0);
                            ePacket.getByteArrays().write(1, bytes1);
                            sendEncryptionPacket(event.getPlayer(), ePacket);
                            // 等待客户端正版验证
                            event.setCancelled(true);

                            new BukkitRunnable() {
                                public void run() {
                                    Player player = event.getPlayer();
                                    if (player != null) {
                                        player.kickPlayer("正版验证完成\n请重新登入");
                                    }
                                }
                            }.runTaskLater(Essential.getPlugin(), 40);
                        }
                    }
                }
            }
        );

        protocolManager.addPacketListener(
            new PacketAdapter(Essential.getPlugin(),
                    ListenerPriority.NORMAL,
                    PacketType.Login.Client.ENCRYPTION_BEGIN) {

                @Override
                public void onPacketReceiving(PacketEvent event) {
                    if (Bukkit.getOnlineMode()) {
                        return;  
                    }
                    event.setCancelled(true);
                    String playerName = loginQueue.poll();
                    if (playerName != null && getIp(event.getPlayer()).equals(ipMap.get(playerName))) {
                        cache.setex(playerName + StateCache.ACCOUNT_LEGAL, 120, "");
                    }
                }
            }
        );

        protocolManager.addPacketListener(
            new PacketAdapter(Essential.getPlugin(),
                    ListenerPriority.NORMAL,
                    PacketType.Play.Server.PLAYER_INFO) {
                
                @Override
                public void onPacketSending(PacketEvent event) {
                    PacketContainer packet = event.getPacket();
                    Player player = event.getPlayer();
                    String info = packet.getModifier().read(1).toString();
                    Matcher matcher = Pattern.compile("latency=\\d*").matcher(info);
                    if (matcher.find()) {
                        int latency = NumberConversions.toInt(matcher.group().split("=")[1]);
                        String pingColor;
                        if (latency < 90) {
                            pingColor = "§2";
                        } else if (latency < 180) {
                            pingColor = "§e";
                        } else {
                            pingColor = "§c";
                        }
                        String footer = String.format("§7玩家§8: §2%s§8/%s        §7延迟§8: %s%s§8ms", Bukkit.getOnlinePlayers().size(), LocalDateTime.now().getYear(), pingColor, latency);
                        player.setPlayerListFooter("\n" + footer);
                    }
                }
            }
        );

    }

    private void giveNewPlayerKits(String playerName) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = Bukkit.getPlayerExact(playerName);
                ItemBuilder kitBuilder = yaml.getItemBuilder(Config.NEW_PLAYER_KITS);
                if (player != null && kitBuilder != null) {
                    player.getInventory().addItem(kitBuilder.build());
                }
            }
        }.runTaskLater(Essential.getPlugin(), 20 * 5L);
    }

    private void setupDomain() {
        protocolManager.addPacketListener(
            new PacketAdapter(Essential.getPlugin(),
                    ListenerPriority.NORMAL,
                    PacketType.Play.Client.POSITION) {

                @Override
                public void onPacketReceiving(PacketEvent event) {
                    Player player = event.getPlayer();
                    Location point = player.getLocation();

                    Dimension dimension = worldMap.get(point.getWorld().getName());
                    if (dimension == null) {
                        return;
                    }

                    String key = player.getName() + StateCache.DOMAIN_STAY;
                    String loc = point.getChunk().getX() + ":" + point.getChunk().getZ();
                    Plot plot = dimension.getPlot(loc);
                    // 进入领地
                    if (plot != null && cache.get(key) == null) {
                        String domainName = plot.getDomain().getName();
                        String ownerName = Bukkit.getOfflinePlayer(UUID.fromString(plot.getDomain().getOwner())).getName();
                        player.sendMessage(Language.PLOT_ENTER.replace("%domain%", domainName).replace("%player%", ownerName));
                        cache.set(key, domainName);
                    } else if (plot == null && cache.get(key) != null) {
                        player.sendMessage(Language.PLOT_LEAVE.replace("%domain%", cache.get(key)));
                        cache.del(key);
                    }
                }
            }
        );
    }

    private void sendEncryptionPacket(Player player, PacketContainer packet) {
        new BukkitRunnable() {
            @Override
            public void run() {
                protocolManager.sendServerPacket(player, packet);
            }
        }.runTaskAsynchronously(Essential.getPlugin());
    }

    /**
     * 获取IP地址
     * @param player 玩家
     * @return ip
     */
    private String getIp(Player player) {
        String ip = "";
        Matcher matcher = pattern.matcher(player.getName());
        if (matcher.find()) {
            ip = matcher.group();
        }
        return ip;
    }

}
