package org.fireflyest.essential.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.craftgui.api.ViewGuide;
import org.fireflyest.craftmsg.MessageService;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.data.StateCache;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class ServersCommand extends SimpleCommand {

    private final ViewGuide guide;
    private final StateCache cache;
    private final MessageService message;

    public ServersCommand(ViewGuide guide, StateCache cache, MessageService message) {
        this.guide = guide;
        this.cache = cache;
        this.message = message;
    }

    @Override
    protected boolean execute(CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            return false;
        }
        // guide.openView(player, Essential.VIEW_SERVER, player.getName());
        return true;
    }
    
    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            return false;
        }

        cache.setex(player.getName() + StateCache.SERVERS_CHANGE, 3, "");

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(arg1);

        player.sendPluginMessage(Essential.getPlugin(), "BungeeCord", out.toByteArray());

        message.popGlobalMessage(sender.getName() + "前往" + this.getServerName(arg1));
        return true;
    }

    public String getServerName(String server) {
        switch (server) {
            case "lobby":
                return "大厅";
            case "survival":
                return "生存";
            case "island":
                return "空岛";
            case "game":
                return "娱乐";
            default:
                break;
        }
        return "未知";
    }

}
