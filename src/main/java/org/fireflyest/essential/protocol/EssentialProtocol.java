package org.fireflyest.essential.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.EquivalentConverter;
import com.comphenix.protocol.wrappers.BukkitConverters;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.comphenix.protocol.wrappers.EnumWrappers.ChatType;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.WrappedServerPing.CompressedImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.fireflyest.craftgui.api.ViewGuide;
import org.fireflyest.craftgui.api.ViewPage;
import org.fireflyest.crafttext.formal.TextInteractFormal;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.bean.Domain;
import org.fireflyest.essential.bean.Profile;
import org.fireflyest.essential.bean.Steve;
import org.fireflyest.essential.data.Config;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.data.StateCache;
import org.fireflyest.essential.gui.AccountPage;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.essential.util.LocateUtils;
import org.fireflyest.essential.world.Dimension;
import org.fireflyest.essential.world.Plot;
import org.fireflyest.util.NetworkUtils;

public class EssentialProtocol {
    
    private ProtocolManager protocolManager;
    private ViewGuide guide;
    private EssentialService service;
    private StateCache cache;
    Map<String, Dimension> worldMap;

    private Map<String, CompressedImage> imgMap = new HashMap<>();
    private Map<String, String> ipMap = new HashMap<>();
    private Queue<String> loginQueue = new ArrayBlockingQueue<>(30);
    private Pattern pattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    private CompressedImage maintain = null;
    private String lastPassword = ""; 
    private PacketContainer playPacket = null;

    byte[] bytes0 = new byte[] {48, -127, -97, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3, -127, -115, 0, 48, -127, -119, 2, -127, -127, 0, -123, -29, -51, 88, 16, -62, -27, -26, 109, -67, -111, 66, 111, 22, 105, 1, -14, 49, -94, 3, 79, -54, -69, -110, -73, 115, 24, 47, 63, 28, 3, -59, -114, 32, -80, 50, -49, 23, -46, 3, 37, -98, 66, 51, -53, -125, 78, -45, 72, -64, 37, -51, -25, -13, 112, 120, 79, -2, -30, -24, 28, 21, 65, -96, 27, 33, -111, -60, -42, 91, -15, 34, -39, 89, 46, 21, -30, 25, 37, 123, -128, 18, 74, -54, 106, 9, 118, 119, -102, 33, 68, 84, 78, 6, -19, -8, -16, 56, -47, 69, 14, -95, 127, 8, -107, 28, -7, -77, 24, 15, -97, -120, 105, -20, -81, 40, -19, -15, 78, -125, 103, 116, 101, -70, 22, 73, 79, -95, 2, 3, 1, 0, 1};
    byte[] bytes1 = new byte[] {-117, -78, -115, -103};

    /**
     * 输入框协议包
     * @param viewGuide 导航
     */
    public EssentialProtocol(ViewGuide guide, EssentialService service, StateCache cache, Map<String, Dimension> worldMap) {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.guide = guide;
        this.service = service;
        this.cache = cache;
        this.worldMap = worldMap;
        
        this.setupLogin();
        this.setupChat();
        this.setupMotd();
        this.setupDomain();
    }

    /**
     * motd包
     */
    private void setupMotd() {
        // 加载服务器图标
        File folder = new File(Essential.getPlugin().getDataFolder(), "logo");
        folder.mkdirs();
        for (File file : folder.listFiles()) {
            if (!file.getName().endsWith("png")) {
                continue;
            }
            String name = file.getName().replace(".png", "");
            try {
                BufferedImage image = ImageIO.read(new  FileInputStream(file));
                if ("maintain".equals(name)) {
                    maintain = CompressedImage.fromPng(image);
                } else {
                    imgMap.put(name, CompressedImage.fromPng(image));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // motd
        protocolManager.addPacketListener(
        new PacketAdapter(Essential.getPlugin(),
                ListenerPriority.NORMAL,
                PacketType.Status.Server.SERVER_INFO) {

            @Override
            public void onPacketSending(PacketEvent event) {
                // motd
                WrappedServerPing ping = event.getPacket().getServerPings().read(0);
                String tip = "     §7“§e§l你好，" + LocateUtils.locate(getIp(event.getPlayer())) + "！§7“";
                String icon  = cache.get("motd.type");
                if ("maintain".equals(icon)) {
                    tip = "     §c§l服务器维护中";
                    if (maintain != null) {
                        ping.setFavicon(maintain);
                    }
                } else {
                    CompressedImage compressedImage = imgMap.get(icon == null ? "default" : icon);
                    if (compressedImage != null) {
                        ping.setFavicon(compressedImage);
                    }
                }
                ping.setPlayersMaximum(LocalDateTime.now().getYear());
                ping.setMotD("§b⌈§7§lC§8§lE§b⌉             " +tip + "§6§l\n§b⌊§8§lD§7§lG§b⌋                §6§l" + Config.WEBSITE);

                event.getPacket().getServerPings().write(0, ping);
                }
            }
        );
    }

    /**
     * 聊天
     */
    private void setupChat() {
        // 聊天监听
        protocolManager.addPacketListener(
            new PacketAdapter(Essential.getPlugin(),
                    ListenerPriority.NORMAL,
                    PacketType.Play.Server.CHAT) {

                @Override
                public void onPacketSending(PacketEvent event) {
                    ChatType type = event.getPacket().getChatTypes().read(0);
                    WrappedChatComponent component = event.getPacket().getChatComponents().read(0);
                    if (component == null) {
                        return;
                    }
                    switch (type) {
                        case CHAT:
                            TextInteractFormal text = new TextInteractFormal(component.getJson());
                            WrappedChatComponent chatComponent = WrappedChatComponent.fromJson(text.toString());
                            event.getPacket().getChatComponents().write(0, chatComponent);
                            break;
                        case SYSTEM:
                            break;
                        case GAME_INFO:
                            break;
                        default:
                            break;
                    }
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
                    WrappedGameProfile gameProfile = event.getPacket().getGameProfiles().read(0);

                    String playerName = gameProfile.getName();
                    String ip = getIp(event.getPlayer());
                    Steve steve = service.selectSteveByName(playerName);
                    // 新玩家
                    if (steve == null) {
                        Bukkit.broadcastMessage(Language.NEW_PLAYER.replace("%player%", playerName));
                        String url = "https://api.mojang.com/users/profiles/minecraft/" + playerName + "?at=0";
                        Profile profile = NetworkUtils.doGet(url, Profile.class);
                        boolean legal = profile != null && profile.getId() != null;
                        if (Bukkit.getOnlineMode()) {
                            if (!legal) {
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
                            } else {
                                // 老玩家更名
                                service.updateSteveName(playerName, uid);
                            }
                            steve = service.selectSteveByUid(UUID.fromString(uid));
                        } else {
                            String uid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + playerName).getBytes()).toString();
                            service.insertSteve(playerName, uid, Instant.now().toEpochMilli(), Config.DEFAULT_PREFIX, Config.BASE_MONEY, legal);
                            steve = service.selectSteveByUid(UUID.fromString(uid));
                        }
                    }
                    // 正版自动登录
                    if (steve != null && steve.isLegal()) {
                        ipMap.putIfAbsent(playerName, ip);
                        // 换IP登入、未正版验证
                        if (!ipMap.get(playerName).equals(ip) || !cache.exist(playerName + ".account.legal")) {
                            if (cache.exist(playerName + ".account.auto")) {
                                cache.setex(playerName + ".account.legal", 600, "");
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
                        cache.setex(playerName + ".account.legal", 600, "");
                    }
                }
            }
        );

        protocolManager.addPacketListener(
            new PacketAdapter(Essential.getPlugin(),
                    ListenerPriority.NORMAL,
                    PacketType.Login.Server.SUCCESS) {

                @Override
                public void onPacketSending(PacketEvent event) {
                    // 正版模式下无需等待验证
                    if (Bukkit.getOnlineMode()) {
                        return;
                    }

                    WrappedGameProfile gameProfile = event.getPacket().getGameProfiles().read(0);
                    UUID uid = gameProfile.getUUID();
                    String playerName = gameProfile.getName();

                    Steve steve = service.selectSteveByUid(uid);
                    // // 等待客户端正版验证
                    // int num = 0;
                    // while (steve != null && steve.isLegal() && (!ipMap.containsKey(playerName) || !cache.exist(playerName + ".account.legal"))) {
                    //     // 超时踢出
                    //     if (++num > 15) {
                    //         event.setCancelled(true);
                    //         return;
                    //     }
                    //     System.out.println(num);
                    //     synchronized (ipMap) {
                    //         try {
                    //             ipMap.wait(50);
                    //         } catch (InterruptedException e) {
                    //             Thread.currentThread().interrupt();
                    //             e.printStackTrace();
                    //         }
                    //     }
                    // }
                }
            }
        );
   

        /*
         * int 
         * boolean
         * net.minecraft.world.level.EnumGamemode 
         * net.minecraft.world.level.EnumGamemode 
         * java.util.Set 
         * net.minecraft.core.IRegistryCustom$Dimension 
         * net.minecraft.core.Holder 
         * net.minecraft.resources.ResourceKey
         * long 
         * int 
         * int
         * int 
         * boolean 
         * boolean 
         * boolean 
         * boolean 
         */

         /*
          *    a=689
                b=false
                c=SURVIVAL
                d=SPECTATOR
                e=[ResourceKey[minecraft:dimension / minecraft:overworld], ResourceKey[minecraft:dimension / minecraft:the_nether], ResourceKey[minecraft:dimension / minecraft:the_end]]
                f=net.minecraft.core.IRegistryCustom$b@2c978895
                g=Reference{ResourceKey[minecraft:dimension_type / minecraft:overworld]=net.minecraft.world.level.dimension.DimensionManager@3990ab6e}
                h=ResourceKey[minecraft:dimension / minecraft:overworld]
                i=-4772185909438444142
                j=20
                k=10
                l=10
                m=false
                n=true
                o=false
                p=false
          */
        protocolManager.addPacketListener(
            new PacketAdapter(Essential.getPlugin(),
                    ListenerPriority.NORMAL,
                    PacketType.Play.Server.LOGIN) {

                @Override
                public void onPacketSending(PacketEvent event) {

                    if (playPacket == null) {
                        playPacket = event.getPacket();
                    }
           
                    // System.out.println("LOGIN event.getPlayer().getName() = " + event.getPlayer().getName());
                    // PacketContainer packet = event.getPacket();
                    // System.out.println(packet.getIntegers().read(0)); // id
                    // System.out.println(packet.getBooleans().read(0)); // false
                    // System.out.println(packet.getGameModes().read(0)); // SPECTATOR
                    // System.out.println(packet.getGameModes().read(1)); // SPECTATOR
                    // System.out.println(packet.getSets(BukkitConverters.getMapConverter(null, null)).read(0));
                    // System.out.println(packet.getMinecraftKeys().read(0));
                    // holder
                    // System.out.println(packet.getMinecraftKeys().read(0));
                    // System.out.println(packet.getLongs().read(0)); // world's seed. Used client side for biome noise       -4772185909438444142
                    // System.out.println(packet.getIntegers().read(1)); // Bukkit.getMaxPlayers()
                    // System.out.println(packet.getIntegers().read(2)); // Bukkit.getViewDistance()
                    // System.out.println(packet.getIntegers().read(3)); // Bukkit.getSimulationDistance()
                    // System.out.println(packet.getBooleans().read(1)); // false
                    // System.out.println(packet.getBooleans().read(2)); // true
                    // System.out.println(packet.getBooleans().read(3)); // false
                    // System.out.println(packet.getBooleans().read(4)); // false
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
                    PlayerInfoAction action = packet.getPlayerInfoAction().read(0);
                    List<PlayerInfoData> dataList = packet.getPlayerInfoDataLists().read(0);
                    PlayerInfoData data = dataList.get(0);
                    switch (action) {
                        case ADD_PLAYER:
                            // WrappedGameProfile profile = data.getProfile();
                            // WrappedGameProfile newProfile = new WrappedGameProfile(UUID.fromString("076e12f1-8be4-4a3e-9064-4c953a3c44dd"), profile.getName());
                            // // WrappedGameProfile newProfile = profile;
                            // System.out.println("newProfile.getId() = " + newProfile.getId());
                            // WrappedSignedProperty signedProperty = new WrappedSignedProperty("textures", "ewogICJ0aW1lc3RhbXAiIDogMTY4MjEzMDI1OTIwMiwKICAicHJvZmlsZUlkIiA6ICIwNzZlMTJmMThiZTQ0YTNlOTA2NDRjOTUzYTNjNDRkZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJGaXJlZmx5ZXN0IiwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2MyODlmZDE2YzZkMDE2MmU3MDQ3OTMzY2RlNzZmMTU4ZTY0MGMzMTViNWQ4ZDc4MDc0NWIxMDZiOTRmYWM0OGUiCiAgICB9LAogICAgIkNBUEUiIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzIzNDBjMGUwM2RkMjRhMTFiMTVhOGIzM2MyYTdlOWUzMmFiYjIwNTFiMjQ4MWQwYmE3ZGVmZDYzNWNhN2E5MzMiCiAgICB9CiAgfQp9", "");
                            // newProfile.getProperties().put("textures", signedProperty);
                            // PlayerInfoData newData  = new PlayerInfoData(newProfile, 0, NativeGameMode.SURVIVAL, null);
                            // dataList.set(0, newData);
                            // packet.getPlayerInfoDataLists().write(0, dataList);
                            // event.setPacket(packet);
                            break;
                        case UPDATE_GAME_MODE:
                            break;
                        case UPDATE_LATENCY:
                            int latency = data.getLatency();
                            String pingColor;
                            if (latency < 90) {
                                pingColor = "§2";
                            } else if (latency < 180) {
                                pingColor = "§e";
                            } else {
                                pingColor = "§c";
                            }
                            String footer = String.format("§7Players§8: §2%s§8/%s        §7Ping§8: %s%s§8ms", Bukkit.getOnlinePlayers().size(), LocalDateTime.now().getYear(), pingColor, latency);
                            player.setPlayerListFooter("\n" + footer);
                            break;
                        case UPDATE_DISPLAY_NAME:
                            break;
                        case REMOVE_PLAYER:
                            break;
                        default:
                            break;
                    }
                }
            }
        );

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
                        player.sendMessage(Language.PLOT_ENTER.replace("%domain%", domainName));
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
                try {
                    protocolManager.sendServerPacket(player, packet);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
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
