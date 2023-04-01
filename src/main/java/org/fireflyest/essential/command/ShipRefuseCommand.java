package org.fireflyest.essential.command;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.essential.bean.Ship;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.service.EssentialService;

public class ShipRefuseCommand extends SubCommand {

    private EssentialService service;

    public ShipRefuseCommand(EssentialService service) {
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
        UUID targetUid = UUID.fromString(service.selectSteveUid(arg1));
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetUid);
        if (target == null) {
            sender.sendMessage(Language.OFFLINE_PLAYER.replace("%player%", arg1));
            return true;
        }

        // 获取双方关系
        Ship toMe = service.selectShip(target.getUniqueId(), player.getUniqueId());

        if (toMe == null || "".equals(toMe.getRequest())) {
            sender.sendMessage(Language.SHIP_REQUEST_NULL);
            return true;
        }

        if ("friend".equals(toMe.getRequest())) {
            service.deleteShip(toMe.getBond());
        } else {
            service.updateShipRequest("", toMe.getBond());
        }
        sender.sendMessage(Language.SHIP_REFUSE);

        return true;
    }

}
