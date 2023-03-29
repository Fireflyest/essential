package org.fireflyest.essential.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.argument.Argument;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.service.EssentialService;

public class PlotArgument implements Argument {

    private EssentialService service;

    public PlotArgument(EssentialService service) {
        this.service = service;
    }

    @Override
    public List<String> tab(CommandSender sender, String arg) {
        List<String> ret = new ArrayList<>();
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return ret;
        }
        for (String string : service.selectDomainsNameByPlayer(player.getUniqueId())) {
            if (string.startsWith(arg)) {
                ret.add(string);
            }
        }
        return ret;
    }
    
}
