package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;

public class ModeCommand extends SimpleCommand {

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        this.switchMode(player);
        return true;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender, @Nonnull String arg1) {
        Player target = Bukkit.getPlayer(arg1);
        if (target == null) {
            sender.sendMessage(Language.OFFLINE_PLAYER.replace("%player%", arg1));
            return false;
        }
        sender.sendMessage(Language.SUCCEED_SWITCH + "§3" + target.getName());
        this.switchMode(target);
        return true;
    }
    
    /**
     * 切换模式
     * @param player 玩家
     */
    private void switchMode(Player player) {
        player.setGameMode(GameMode.SURVIVAL.equals(player.getGameMode()) ? GameMode.CREATIVE : GameMode.SURVIVAL);
        player.sendMessage(Language.TITLE + "模式: §3" + player.getGameMode().name().toLowerCase());
    }

}
