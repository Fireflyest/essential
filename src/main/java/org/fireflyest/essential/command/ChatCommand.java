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
        String range = cache.get(key);
        if (range == null) { // 附近切换到全球
            player.sendMessage(Language.CHAT_GLOBAL);
            cache.set(key, "globe");
        } else if ("globe".equals(range)) { // 全球切换到附近
            player.sendMessage(Language.CHAT_NEAR);
            cache.del(key);
        } else { // 群聊切换到附近
            cache.srem(range, player.getName());
            player.sendMessage(Language.CHAT_NEAR);
            cache.del(key);
        }

        return true;
    }
    
    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        execute(sender, arg1, null);
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1, String arg2) {
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
        String passwordKey = "server.chat.password." + arg1;
        
        // 判断房间是否已经存在
        if (cache.smembers("server.chat.room").contains(arg1)) {
            String password;
            if ((password = cache.get(passwordKey)) != null && !password.equals(arg2)) {
                sender.sendMessage(Language.CHAT_ROOM_FAIL);
                return true;
            } else {
                // 密码正确加入
                cache.set(rangeKey, roomKey);
                cache.sadd(roomKey, player.getName());
            }
        } else {
            // 创建房间并加入
            cache.set(rangeKey, roomKey);
            cache.set(passwordKey, arg2);
            cache.sadd(roomKey, player.getName());
            cache.sadd("server.chat.room", arg1);
        }
        player.sendMessage(Language.CHAT_ROOM + " §7" + cache.smembers(roomKey).toString());
        
        return true;
    }

}
