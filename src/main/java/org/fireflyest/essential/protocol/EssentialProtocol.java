package org.fireflyest.essential.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import com.comphenix.protocol.wrappers.EnumWrappers.ChatType;
import com.comphenix.protocol.wrappers.WrappedServerPing.CompressedImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

import org.bukkit.entity.Player;
import org.fireflyest.craftgui.api.ViewGuide;
import org.fireflyest.craftgui.api.ViewPage;
import org.fireflyest.crafttext.formal.TextInteractFormal;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.data.StateCache;
import org.fireflyest.essential.gui.AccountPage;
import org.fireflyest.essential.util.LocateUtils;

public class EssentialProtocol {
    
    private ProtocolManager protocolManager;
    private StateCache cache;

    private Map<String, CompressedImage> imgMap = new HashMap<>();
    private Pattern pattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    private CompressedImage maintain = null;
    private String lastPassword = ""; 

    /**
     * 输入框协议包
     * @param viewGuide 导航
     */
    public EssentialProtocol(ViewGuide viewGuide, StateCache cache) {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.cache = cache;

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
                        if (viewGuide.unUsed(playerName)) {
                            return;
                        }
                        // 判断是否登录界面
                        ViewPage page = viewGuide.getUsingPage(playerName);
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

                        viewGuide.refreshPage(playerName);
                    }
                }
        );

        // 聊天监听
        protocolManager.addPacketListener(
                new PacketAdapter(Essential.getPlugin(),
                        ListenerPriority.NORMAL,
                        PacketType.Play.Server.CHAT) {

                    @Override
                    public void onPacketSending(PacketEvent event) {
                        ChatType type = event.getPacket().getChatTypes().read(0);
                        WrappedChatComponent component = event.getPacket().getChatComponents().read(0);

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

        // 聊天监听
        protocolManager.addPacketListener(
            new PacketAdapter(Essential.getPlugin(),
                    ListenerPriority.NORMAL,
                    PacketType.Status.Server.SERVER_INFO) {

                @Override
                public void onPacketSending(PacketEvent event) {
                    // motd
                    WrappedServerPing ping = event.getPacket().getServerPings().read(0);
                    String tip = "     §7“§e§l你好，" + queryLoc(event.getPlayer()) + "！§7“";
                    String website = "www.craftedge.com";
                    String icon  = cache.get("motd.type");
                    if ("maintain".equals(icon)) {
                        ping.setFavicon(maintain);
                        tip = "     §c§l服务器维护中";
                    } else {
                        CompressedImage compressedImage = imgMap.get(icon == null ? "default" : icon);
                        if (compressedImage != null) {
                            ping.setFavicon(compressedImage);
                        }
                    }
                    ping.setPlayersMaximum(LocalDateTime.now().getYear());
                    ping.setMotD("§b⌈§7§lC§8§lE§b⌉          " +tip + "§6§l\n§b⌊§8§lD§7§lG§b⌋          §6§l" + website);

                    event.getPacket().getServerPings().write(0, ping);

                }
            }
        );

    }

    private String queryLoc(Player player) {
        String ip = null;
        Matcher matcher = pattern.matcher(player.getName());
        if (matcher.find()) {
            ip = matcher.group();
        }
        return LocateUtils.locate(ip);
    }

}
