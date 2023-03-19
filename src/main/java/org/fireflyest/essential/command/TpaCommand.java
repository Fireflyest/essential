package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.data.StateCache;
import org.fireflyest.essential.util.ChatUtils;

public class TpaCommand extends SimpleCommand {

    private StateCache cache;

    /**
     * 传送玩家
     * @param cache 数据缓存
     */
    public TpaCommand(StateCache cache) {
        this.cache = cache;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        return true;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender, @Nonnull String arg1) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }

        cache.setex(arg1 + ".base.tp", 120, player.getName());
        
        Player target = Bukkit.getPlayer(arg1);
        if (target == null || target.isInvisible()) {
            sender.sendMessage(Language.OFFLINE_PLAYER.replace("%player%", arg1));
            return true;
        }
        target.sendMessage(Language.TELEPORT_APPLY.replace("%player%", player.getName()));
        ChatUtils.sendApplyButton(target, "/tp");
        player.sendMessage(Language.SUCCEED_SEND_TP);
        return true;
    }
    
}
