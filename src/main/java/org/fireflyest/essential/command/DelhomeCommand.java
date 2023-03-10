package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.service.EssentialService;

public class DelhomeCommand extends SimpleCommand {

    private EssentialService service;

    /**
     * 删除家
     * @param service 数据服务
     */
    public DelhomeCommand(EssentialService service) {
        this.service = service;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        sender.sendMessage(Language.TITLE + service.selectHomes(player.getUniqueId()));
        return true;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender, @Nonnull String arg1) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        long num = service.deleteHome(player.getUniqueId(), arg1);
        if (num > 0) {
            sender.sendMessage(Language.SUCCEED_DELETE.replace("%home%", arg1));
        } else {
            sender.sendMessage(Language.HAVE_NOT_SETTLE.replace("%home%", arg1));
        }
        return true;
    }

    
    
}
