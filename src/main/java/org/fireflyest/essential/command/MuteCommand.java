package org.fireflyest.essential.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.data.StateCache;

public class MuteCommand extends SimpleCommand {

    private StateCache cache;

    public MuteCommand(StateCache cache) {
        this.cache = cache;
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
            target.sendMessage(Language.TITLE + "已被禁言§3" + arg2 + "§f分钟 (" + arg3 + ")");
            cache.setex(target.getName() + ".base.mute", second, arg3);
        }
        sender.sendMessage(Language.TITLE + "操作成功");
        return true;
    }
    
}
