package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.data.StateCache;

public class TprefuseCommand extends SimpleCommand {

    private StateCache cache;

    /**
     * 传送玩家
     * @param cache 数据缓存
     */
    public TprefuseCommand(StateCache cache) {
        this.cache = cache;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }

        String tp = cache.get(player.getName() + ".base.tp");
        if (tp != null) {
            Player target = Bukkit.getPlayer(tp);
            if (target != null) {
                cache.del(target.getName() + ".base.tp");
                target.sendMessage(Language.TP_APPLY_REFUSE);
            }
            return true;
        }

        String tphere = cache.get(player.getName() + ".base.tphere");
        if (tphere != null) {
            Player target = Bukkit.getPlayer(tphere);
            if (target != null) {
                cache.del(target.getName() + ".base.tphere");
                target.sendMessage(Language.TP_APPLY_REFUSE);
            }
        }
        return true;
    }
    
}
