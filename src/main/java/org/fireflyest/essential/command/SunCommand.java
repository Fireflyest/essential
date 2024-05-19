package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;

public class SunCommand extends SimpleCommand {

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "weather clear");
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        Player player = Bukkit.getPlayer(arg1);
        if (player != null) {
            player.setPlayerWeather(WeatherType.CLEAR);
        }
        return true;
    }
    
}
