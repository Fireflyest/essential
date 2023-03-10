package org.fireflyest.essential.command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.argument.Argument;
import org.fireflyest.essential.bean.Home;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.service.EssentialService;

public class HomeArgument implements Argument {

    private EssentialService service;

    /**
     * 家的提示
     * @param service 数据服务
     */
    public HomeArgument(EssentialService service) {
        this.service = service;
    }

    @Override
    public List<String> tab(@Nonnull CommandSender sender, @Nonnull String arg) {
        List<String> homes = new ArrayList<>();
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return homes;
        }
        UUID uid = player.getUniqueId();
        Home[] hs = service.selectHomes(uid);
        for (Home h : hs) {
            if (h.getName().startsWith(arg)) {
                homes.add(h.getName());
            }
        }
        return homes;
    }
    
}
