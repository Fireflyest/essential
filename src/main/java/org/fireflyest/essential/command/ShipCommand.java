package org.fireflyest.essential.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.ComplexCommand;
import org.fireflyest.craftgui.api.ViewGuide;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.data.Language;

public class ShipCommand extends ComplexCommand {

    private ViewGuide guide;
    
    public ShipCommand(ViewGuide guide) {
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
    
}
