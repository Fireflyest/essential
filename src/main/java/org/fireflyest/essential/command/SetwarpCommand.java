package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.bean.Point;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.service.EssentialService;

public class SetwarpCommand extends SimpleCommand {

    private EssentialService service;

    /**
     * 设置传送点
     * @param service 数据服务
     */
    public SetwarpCommand(EssentialService service) {
        this.service = service;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        return true;
    }
    
    @Override
    protected boolean execute(@Nonnull CommandSender sender, @Nonnull String arg1) {
        return execute(sender, arg1, "0");
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender, @Nonnull String arg1, @Nonnull String arg2) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        Point point = service.selectPoint(arg1);
        if (point != null) {
            service.updatePoint(player.getLocation(), arg1);
        } else {
            service.insertPoint(arg1, player.getLocation(), NumberConversions.toInt(arg2));
        }
        player.sendMessage(Language.TITLE + "成功设置点§3" + arg1);
        return true;
    }

}
