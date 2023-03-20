package org.fireflyest.essential.command;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.essential.data.Language;

public class WorldTpCommand extends SubCommand {

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
        World world = Bukkit.getWorld(arg1);
        if (world != null) {
            player.teleport(world.getSpawnLocation());
        }
        return true;
    }
    
}
