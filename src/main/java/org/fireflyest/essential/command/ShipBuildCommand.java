package org.fireflyest.essential.command;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.essential.bean.Ship;
import org.fireflyest.essential.data.Config;
import org.fireflyest.essential.data.EssentialYaml;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.util.TimeUtils;

public class ShipBuildCommand extends SubCommand {

    private EssentialYaml yaml;
    private EssentialService service;

    private Set<String> ships = new HashSet<>();

    public ShipBuildCommand(EssentialYaml yaml, EssentialService service) {
        this.yaml = yaml;
        this.service = service;

        ships = yaml.getShip().getKeys(false);
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
                service.updateShipIntimate(true, toHe.getBond());
                service.updateShipIntimate(true, toMe.getBond());
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
                target.sendMessage(Language.SHIP_NEW);
            }
        }
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1, String arg2) {
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
        Ship toHe = service.selectShip(player.getUniqueId(), target.getUniqueId());

        switch (arg2) {
            case "friend":
                if (toHe != null) {
                    // 已经存在关系
                    sender.sendMessage(Language.SHIP_EXIST);
                }else if (toMe != null) {
                    // 接受好友申请
                    service.updateShipTag(toMe.getRequest(), toMe.getBond());
                    service.updateShipRequest("", toMe.getBond());
                    service.insertShip(player.getUniqueId(), target.getUniqueId(), toMe.getRequest(), "", arg1, TimeUtils.getTime());
                    sender.sendMessage(Language.SHIP_ACCEPT);
                } else {
                    // 发送好友申请
                    service.insertShip(player.getUniqueId(), target.getUniqueId(), "", "friend", arg1, TimeUtils.getTime());
                    sender.sendMessage(Language.SHIP_APPLY_FOR);
                    if (target.isOnline()) {
                        target.getPlayer().sendMessage(Language.SHIP_NEW);
                    }
                }
                break;
            case "master":
                 // 是否已经建立好友关系
                 if (toHe == null || toMe == null) {
                    sender.sendMessage(Language.SHIP_NULL);
                    return true;
                }
                if (service.selectShipBondByTag(arg2, player.getUniqueId()).length > Config.MAX_MASTER) {
                    sender.sendMessage(Language.SHIP_LIMIT);
                    return true;
                }
                service.updateShipRequest(arg2, toHe.getBond());
                player.sendMessage(Language.SHIP_MASTER);
                if (target.isOnline()) {
                    target.getPlayer().sendMessage(Language.SHIP_NEW);
                }
                break;
            case "apprentice":
                 // 是否已经建立好友关系
                 if (toHe == null || toMe == null) {
                    sender.sendMessage(Language.SHIP_NULL);
                    return true;
                }
                if (service.selectShipBondByTag(arg2, player.getUniqueId()).length > Config.MAX_APPRENTICE) {
                    sender.sendMessage(Language.SHIP_LIMIT);
                    return true;
                }
                service.updateShipRequest(arg2, toHe.getBond());
                player.sendMessage(Language.SHIP_APPRENTICE);
                if (target.isOnline()) {
                    target.getPlayer().sendMessage(Language.SHIP_NEW);
                }
                break;
            case "lover":
                 // 是否已经建立好友关系
                 if (toHe == null || toMe == null) {
                    sender.sendMessage(Language.SHIP_NULL);
                    return true;
                }
                if (service.selectShipBondByTag(arg2, player.getUniqueId()).length > 1
                    || service.selectShipBondByTag("couple", player.getUniqueId()).length > 1) {
                    sender.sendMessage(Language.SHIP_LIMIT);
                    return true;
                }
                service.updateShipRequest(arg2, toHe.getBond());
                player.sendMessage(Language.SHIP_LOVER);
                if (target.isOnline()) {
                    target.getPlayer().sendMessage(Language.SHIP_NEW);
                }
                break;
            case "couple":
                 // 是否已经建立好友关系
                 if (toHe == null || toMe == null) {
                    sender.sendMessage(Language.SHIP_NULL);
                    return true;
                }
                if (service.selectShipBondByTag(arg2, player.getUniqueId()).length != 1) {
                    sender.sendMessage(Language.SHIP_LIMIT);
                    return true;
                }
                service.updateShipRequest(arg2, toHe.getBond());
                player.sendMessage(Language.SHIP_COUPLE);
                if (target.isOnline()) {
                    target.getPlayer().sendMessage(Language.SHIP_NEW);
                }
                break;
            default:
                // 是否已经建立好友关系
                if (toHe == null || toMe == null) {
                    sender.sendMessage(Language.SHIP_NULL);
                    return true;
                }
                // 是否存在关系
                if (!ships.contains(arg2)) {
                    sender.sendMessage(Language.SHIP_UNKNOWN);
                    return true;
                }
                if (!"friend".equals(toHe.getTag())) {
                    sender.sendMessage(Language.SHIP_UP_FAIL);
                    return true;
                }
                int limit = yaml.getShip().getInt(arg2 + ".limit");
                if (service.selectShipBondByTag(arg2, player.getUniqueId()).length > limit) {
                    sender.sendMessage(Language.SHIP_LIMIT);
                    return true;
                }
                service.updateShipRequest(arg2, toHe.getBond());
                player.sendMessage(Language.SHIP_UP);
                if (target.isOnline()) {
                    target.getPlayer().sendMessage(Language.SHIP_NEW);
                }
                break;
        }

        return true;
    }
    
}
