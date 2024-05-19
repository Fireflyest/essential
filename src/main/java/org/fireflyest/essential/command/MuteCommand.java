package org.fireflyest.essential.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.craftmsg.MessageService;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.data.StateCache;

public class MuteCommand extends SimpleCommand {

    private final StateCache cache;
    private final MessageService message;

    public MuteCommand(StateCache cache, MessageService message) {
        this.cache = cache;
        this.message = message;
    }

    @Override
    protected boolean execute(CommandSender sender) {
        return false;
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        return execute(sender, arg1, "5");
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1, String arg2) {
        return execute(sender, arg1, arg2, "禁言");
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1, String arg2, String arg3) {
        Player target = Bukkit.getPlayerExact(arg1);
        int second = NumberConversions.toInt(arg2) * 60;
        if (target != null) {
            message.popMessage(target, "你已被禁言" + arg2 + "分钟 (" + arg3 + ")");
            cache.setex(target.getName() + ".base.mute", second, arg3);
        }
        sender.sendMessage(Language.TITLE + "操作成功");
        return true;
    }
    
}
