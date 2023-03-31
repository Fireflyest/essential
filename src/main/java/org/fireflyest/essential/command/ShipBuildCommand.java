package org.fireflyest.essential.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.essential.bean.Ship;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.util.TimeUtils;

public class ShipBuildCommand extends SubCommand {

    private EssentialService service;

    public ShipBuildCommand(EssentialService service) {
        this.service = service;
    }

    @Override
    protected boolean execute(CommandSender sender) {
        sender.sendMessage(Language.COMMAND_ARGUMENT);
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        Player target = Bukkit.getPlayerExact(arg1);
        if (target == null) {
            sender.sendMessage(Language.OFFLINE_PLAYER.replace("%player%", arg1));
            return true;
        }
        
        // 判断对方是否发送过好友请求，如果发送过，则为接受请求
        Ship toMe = service.selectShip(target.getUniqueId(), player.getUniqueId());
        Ship toHe = service.selectShip(player.getUniqueId(), target.getUniqueId());

        if (toMe != null && !"".equals(toMe.getRequest())) { 
            //对方有请求，接受后修改好友标签，去除请求
            service.updateShipTag(toMe.getRequest(), toMe.getBond());
            service.updateShipRequest("", toMe.getBond());
            // 更新我对对方的标签，如果还没有更新则建立
            if (toHe == null) {
                service.insertShip(player.getUniqueId(), target.getUniqueId(), toMe.getRequest(), "", arg1, TimeUtils.getTime());
            } else {
                service.updateShipTag(toMe.getRequest(), toHe.getBond());
            }
            sender.sendMessage(Language.SHIP_ACCEPT);
        } else {
            if (toHe != null) {
                // 已经是好友
                sender.sendMessage(Language.SHIP_EXIST);
            } else {
                // 发送好友申请
                service.insertShip(player.getUniqueId(), target.getUniqueId(), "", "friend", arg1, TimeUtils.getTime());
                sender.sendMessage(Language.SHIP_APPLY_FOR);
            }
        }
        return true;
    }
    
}
