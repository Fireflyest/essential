package org.fireflyest.essential.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.craftgui.api.ViewGuide;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.bean.Ship;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.util.TimeUtils;

public class ShipCommand extends SimpleCommand {

    private EssentialService service;
    private ViewGuide guide;
    
    public ShipCommand(EssentialService service, ViewGuide guide) {
        this.service = service;
        this.guide = guide;
    }

    @Override
    protected boolean execute(CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        guide.openView(player, Essential.VIEW_SHIP, player.getName());
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
        } else {
            // 判断对方是否发送过好友请求，如果发送过，则为接受请求
            Ship ship = service.selectShip(target.getUniqueId(), player.getUniqueId());
            if (ship != null) {
                service.updateShipTag("friend", ship.getBond());
                service.insertShip(player.getUniqueId(), target.getUniqueId(), "friend", arg1, TimeUtils.getTime());
                sender.sendMessage(Language.SHIP_ACCEPT);
            } else {
                if (service.selectShip(player.getUniqueId(), target.getUniqueId()) != null) {
                    sender.sendMessage(Language.SHIP_EXIST);
                } else {
                    service.insertShip(player.getUniqueId(), target.getUniqueId(), "apply_for", arg1, TimeUtils.getTime());
                    sender.sendMessage(Language.SHIP_APPLY_FOR);
                }
            }
        }
        return true;
    }
    
}
