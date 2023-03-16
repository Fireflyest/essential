package org.fireflyest.essential.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.craftgui.api.ViewGuide;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.data.Language;

public class InteractCommand extends SimpleCommand {

    private ViewGuide guide;

    public InteractCommand(ViewGuide guide) {
        this.guide = guide;
    }

    @Override
    protected boolean execute(CommandSender sender) {
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        guide.openView(player, Essential.VIEW_INTERACT, player.getUniqueId().toString() + "&" + arg1);
        return true;
    }
    
}
