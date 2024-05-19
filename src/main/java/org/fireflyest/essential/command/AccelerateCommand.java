package org.fireflyest.essential.command;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.NumberConversions;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.Essential;

public class AccelerateCommand extends SimpleCommand {

    @Override
    protected boolean execute(CommandSender sender) {
        return this.execute(sender, "120");
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            return false;
        }
        new BukkitRunnable() {
            int count = NumberConversions.toInt(arg1);
            @Override
            public void run() {
                if (count < 0) {
                    cancel();
                }
                World world = player.getWorld();
                world.setTime(world.getTime() + 100);
                count--;
            }
        }.runTaskTimer(Essential.getPlugin(), 0, 0L);
        return true;
    }
    
}
