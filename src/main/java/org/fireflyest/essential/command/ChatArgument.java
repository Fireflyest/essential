package org.fireflyest.essential.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.fireflyest.craftcommand.argument.Argument;
import org.fireflyest.essential.data.StateCache;

public class ChatArgument implements Argument {

    private StateCache cache;

    public ChatArgument(StateCache cache) {
        this.cache = cache;
    }

    @Override
    public List<String> tab(CommandSender sender, String arg) {
        List<String> ret = new ArrayList<>();
        for (String room : cache.smembers("server.chat.room")) {
            if (room.startsWith(arg)) {
                ret.add(room);
            }
        }
        return ret;
    }
    
}
