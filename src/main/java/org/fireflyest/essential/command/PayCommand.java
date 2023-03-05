package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.service.EssentialEconomy;

public class PayCommand extends SimpleCommand {

    private EssentialEconomy economy;

    public PayCommand(EssentialEconomy economy) {
        this.economy = economy;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        return true;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender, @Nonnull String arg1, @Nonnull String arg2) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        double amount = NumberConversions.toDouble(arg2);
        if(!economy.has(player, amount)) {
            player.sendMessage("💰不足支付");
            return true;
        }
        //给钱
        economy.withdrawPlayer(player, amount);
        economy.depositPlayer(arg1, amount);

        String money = economy.format(economy.getBalance(arg1));
        sender.sendMessage(Language.TITLE + "玩家§3" + arg1 + "§f目前拥有§3" + money + economy.currencyNameSingular());

        Player target = Bukkit.getPlayer(arg1);
        if(target != null && target.isOnline() && !sender.getName().equals(arg1)) {
            target.sendMessage(Language.TITLE + "您目前拥有§3" + money + economy.currencyNameSingular());
        }
        return true;
    }
    
}
