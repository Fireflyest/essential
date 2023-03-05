package org.fireflyest.essential.command;

import org.bukkit.command.CommandSender;
import org.fireflyest.craftcommand.command.ComplexCommand;

public class AccountCommand extends ComplexCommand {

    @Override
    protected boolean execute(CommandSender sender) {
        sender.sendMessage("§b/account                                 §f - 指令帮助");
        sender.sendMessage("§b/account changepw 旧密码 新密码  §f - 修改密码");
        sender.sendMessage("§b/account email 邮箱                   §f - 绑定邮箱");
        sender.sendMessage("§b/account prove 认证码                 §f - 认证邮箱");
        return true;
    }
    
}
