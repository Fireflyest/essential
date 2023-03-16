package org.fireflyest.essential.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.data.StateCache;

public class ChatCommand extends SimpleCommand {

    private StateCache cache;

    public ChatCommand(StateCache cache) {
        this.cache = cache;
    }

    @Override
    protected boolean execute(CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }

        String key = player.getName() + ".chat.range";
        if (cache.exist(key)) {
            player.sendMessage(Language.CHAT_NEAR);
            cache.del(key);
        } else {
            player.sendMessage(Language.CHAT_GLOBAL);
            cache.set(key, "globe");
        }

        return true;
    }
    
    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }

        if (!sender.hasPermission("essential.chat.room"))  {
            sender.sendMessage(Language.NOT_PERMISSION.replace("%permission%", "essential.chat.room"));
            return true;
        }

        String rangeKey = player.getName() + ".chat.range";
        String roomKey = "server.chat.room." + arg1;
        cache.set(rangeKey, roomKey);
        cache.sadd(roomKey, player.getName());
        cache.sadd("server.chat.room", arg1);
        player.sendMessage(Language.CHAT_ROOM + " §7" + cache.smembers(roomKey).toString());
        
        return true;
    }

}
