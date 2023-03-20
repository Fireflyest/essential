package org.fireflyest.essential.command;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.craftgui.api.ViewGuide;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.bean.Prefix;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.util.TimeUtils;

public class PrefixCommand extends SimpleCommand {

    private EssentialService service;
    private ViewGuide guide;

    public PrefixCommand(EssentialService service, ViewGuide guide) {
        this.service = service;
        this.guide = guide;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        guide.openView(player, Essential.VIEW_PREFIX, player.getName());
        return true;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender, @Nonnull String arg1) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        int id = NumberConversions.toInt(arg1);
        Prefix prefix = service.selectPrefix(id);
        if (prefix.getOwner().equals(player.getUniqueId().toString())) {
            service.updatePrefix(prefix.getValue(), player.getUniqueId());
            player.sendMessage(Language.PREFIX_CHANGE);
        }
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
        arg2 = arg2.replace("&", "§");
        // 是否有权限
        if(!sender.hasPermission("essential.admin"))  {
            sender.sendMessage(Language.NOT_PERMISSION.replace("%permission%", "essential.admin"));
            return false;
        }
        Player target = Bukkit.getPlayerExact(arg1);
        if (target == null) {
            sender.sendMessage(Language.OFFLINE_PLAYER.replace("%player%", arg1));
        } else {
            target.sendMessage(Language.PREFIX_ADD.replace("%prefix%", arg2));
            int day = NumberConversions.toInt(arg3);
            Prefix prefix = service.selectPrefix(target.getUniqueId(), arg2);
            if (prefix == null) {
                long deadline = (day == -1 ? -1 : Instant.now().plus(day, ChronoUnit.DAYS).toEpochMilli());
                service.insertPrefix(target.getUniqueId(), arg2, deadline);
            } else {
                long deadline = (day == -1 ? -1 : TimeUtils.getInstant(prefix.getDeadline()).plus(day, ChronoUnit.DAYS).toEpochMilli());
                service.updatePrefix(deadline, prefix.getId());
            }
            sender.sendMessage(Language.TITLE + "操作成功");
        }
        return true;
    }
    
}
