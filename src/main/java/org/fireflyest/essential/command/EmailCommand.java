package org.fireflyest.essential.command;

import java.util.UUID;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.service.EssentialService;

public class EmailCommand extends SubCommand {

    private EssentialService service;

    /**
     * 邮箱绑定指令
     * @param service 数据服务
     */
    public EmailCommand(EssentialService service) {
        this.service = service;
    }

    @Override
    protected boolean execute(CommandSender sender) {
        return false;
    }
    
    @Override
    protected boolean execute(@Nonnull CommandSender sender, @Nonnull String arg1) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        UUID uid = player.getUniqueId();
        String email = service.selectEmail(uid);
        if (!"".equals(email)) {
            player.sendMessage(Language.ALREADY_PROVE);
            return true;
        }
        if (!Pattern.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", arg1)) {
            player.sendMessage(Language.INVALID_EMAIL);
            return true;
        }
        service.updateEmail(email, uid);
        player.sendMessage(Language.SUC_SET_EMAIL);
        return true;
    }

}
