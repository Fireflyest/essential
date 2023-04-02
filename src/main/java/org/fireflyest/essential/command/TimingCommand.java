package org.fireflyest.essential.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.craftgui.api.ViewGuide;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.world.EssentialTimings;
import org.fireflyest.essential.world.EssentialTimings.Line;

public class TimingCommand extends SimpleCommand {

    private EssentialTimings timings;
    private ViewGuide guide;
    
    public TimingCommand(EssentialTimings timings, ViewGuide guide) {
        this.timings = timings;
        this.guide = guide;
    }

    @Override
    protected boolean execute(CommandSender sender) {
        timings.refresh();
        for (Line line : timings.getLines()) {
            Essential.getPlugin().getLogger().info(line.name);
        }
        
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        guide.openView(player, Essential.VIEW_TIMING, null);
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        return true;
    }
    
}
