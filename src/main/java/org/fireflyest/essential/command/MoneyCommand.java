package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.service.EssentialEconomy;

public class MoneyCommand extends SimpleCommand {

    private EssentialEconomy economy;

    /**
     * 游戏币指令
     * @param service 数据服务
     */
    public MoneyCommand(EssentialEconomy economy) {
        this.economy = economy;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }

        String money = economy.format(economy.getBalance(player));
        sender.sendMessage(Language.ECONOMY_PLAYER.replace("%player%", "").replace("%money%", money));

        return true;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender, @Nonnull String arg1) {
        if(!sender.hasPermission("essential.money"))  {
            sender.sendMessage(Language.NOT_PERMISSION.replace("%permission%", "essential.money"));
            return false;
        }

        String money = economy.format(economy.getBalance(arg1));
        sender.sendMessage(Language.ECONOMY_PLAYER.replace("%player%", money).replace("%money%", money));

        return true;
    }
    
}
