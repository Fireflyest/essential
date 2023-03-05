package org.fireflyest.essential.command;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.bean.Home;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.service.EssentialService;

public class SethomeCommand extends SimpleCommand {

    private EssentialService service;

    /**
     * 设置家
     * @param service 数据服务
     */
    public SethomeCommand(EssentialService service) {
        this.service = service;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        return execute(sender, "a");
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender, @Nonnull String arg1) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        String uid = player.getUniqueId().toString();
        List<Home> homes = Arrays.asList(service.selectHomes(uid));
        if ((homes.size() > 3 && !player.hasPermission("essential.vip"))) {
            player.sendMessage(Language.MAXIMUM_AMOUNT);
            return true;
        }
        if (homes.size() > 5) {
            player.sendMessage(Language.MAXIMUM_AMOUNT);
            return true;
        }
        Home home = service.selectHome(uid, arg1);
        if (home != null) {
            service.updateHome(player.getLocation(), uid, arg1);
        } else {
            service.insertHome(uid, arg1, player.getLocation());
        }
        player.sendMessage(Language.SUCCEED_SETTLE.replace("%home%", arg1));
        return true;
    }
    
}
