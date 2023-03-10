package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.craftgui.api.ViewGuide;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.service.EssentialPermission;

public class GroupCommand extends SimpleCommand {

    private EssentialPermission permission;
    private ViewGuide guide;


    public GroupCommand(EssentialPermission permission, ViewGuide guide) {
        this.permission = permission;
        this.guide = guide;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        guide.openView(player, Essential.VIEW_PERMISSION, player.getName());
        return true;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender, @Nonnull String arg1) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        guide.openView(player, Essential.VIEW_PERMISSION, arg1);
        return true;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender, @Nonnull String arg1, @Nonnull String arg2) {
        this.execute(sender, arg1, arg2, "-1");
        return true;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender, @Nonnull String arg1, @Nonnull String arg2,
            @Nonnull String arg3) {
        // 是否有权限
        if(!sender.hasPermission("essential.admin"))  {
            sender.sendMessage(Language.NOT_PERMISSION.replace("%permission%", "essential.admin"));
            return false;
        }
        Player target = Bukkit.getPlayerExact(arg1);
        if (target == null) {
            sender.sendMessage(Language.OFFLINE_PLAYER.replace("%player%", arg1));
        } else {
            target.sendMessage(Language.GROUP_ADD.replace("%group%", arg2));
            permission.playerAddGroup("", target, arg2, NumberConversions.toInt(arg3));
            sender.sendMessage(Language.TITLE + "操作成功");
        }
        return true;
    }
    
}
