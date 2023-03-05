package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.service.EssentialService;

public class ChangepwCommand extends SubCommand {

    private EssentialService service;
    
    /**
     * 修改密码
     * @param service 数据服务
     */
    public ChangepwCommand(EssentialService service) {
        this.service = service;
    }

    @Override
    protected boolean execute(CommandSender sender) {
        return false;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender, @Nonnull String arg1, @Nonnull String arg2) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        String uid = player.getUniqueId().toString();
        String password = service.selectPassword(uid);
        
        if (password.equals(arg1)) {
            service.updatePassword(arg2, uid);
            player.sendMessage(Language.SUC_CHANGE_PW);
        } else {
            player.sendMessage(Language.ERROR_PASSWORD.replace("%amount%", "1"));
        }
        return true;
    }
    
}
