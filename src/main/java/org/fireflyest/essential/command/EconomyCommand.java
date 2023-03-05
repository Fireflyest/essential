package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.service.EssentialEconomy;

public class EconomyCommand extends SimpleCommand {

    private EssentialEconomy economy;

    public EconomyCommand(EssentialEconomy economy) {
        this.economy = economy;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        return true;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender, @Nonnull String arg1, @Nonnull String arg2) {
        economy.depositPlayer(arg1, NumberConversions.toDouble(arg2));
        String money = economy.format(economy.getBalance(arg1));
        //提示信息
        sender.sendMessage(Language.TITLE + "玩家§3" + arg1 + "§f目前拥有§3" + money + economy.currencyNameSingular());

        Player target = Bukkit.getPlayer(arg1);
        if(target != null && target.isOnline() && !sender.getName().equals(arg1)) {
            target.sendMessage(Language.TITLE + "您目前拥有§3" + money + economy.currencyNameSingular());
        }
        return true;
    }
    
}
