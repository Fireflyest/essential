package org.fireflyest.essential.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.EnumWrappers.ChatType;

import org.fireflyest.craftgui.api.ViewGuide;
import org.fireflyest.craftgui.api.ViewPage;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.gui.AccountPage;

public class EssentialProtocol {
    
    private ProtocolManager protocolManager;
    private String lastPassword = ""; 

    /**
     * 输入框协议包
     * @param viewGuide 导航
     */
    public EssentialProtocol(ViewGuide viewGuide) {
        this.protocolManager = ProtocolLibrary.getProtocolManager();

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
                });

         // 聊天监听
        protocolManager.addPacketListener(
            new PacketAdapter(Essential.getPlugin(),
                    ListenerPriority.NORMAL,
                    PacketType.Play.Server.CHAT) {

                @Override
                public void onPacketSending(PacketEvent event) {
                    ChatType type = event.getPacket().getChatTypes().read(0);

                    System.out.println("getChatTypes=" + type);
                    switch (type) {
                        case CHAT:
                            
                            break;
                        case SYSTEM:

                            break;
                        case GAME_INFO:
                        
                            break;
                        default:
                            break;
                    }
                    WrappedChatComponent component = event.getPacket().getChatComponents().read(0);
                    if (component != null) {
                        System.out.println("getChatComponents=" + component.getJson());
                    } 
                }
            });
    }

    

}
