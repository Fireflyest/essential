package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.craftgui.api.ViewGuide;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.data.Language;

public class PluginsCommand extends SimpleCommand {

    private ViewGuide guide;

    /**
     * 插件指令
     * @param guide 导航
     */
    public PluginsCommand(ViewGuide guide) {
        this.guide = guide;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        guide.openView(player, Essential.VIEW_PLUGIN, null);
        return true;
    }
    
}
