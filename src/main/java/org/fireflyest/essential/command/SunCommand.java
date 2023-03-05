package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.fireflyest.craftcommand.command.SimpleCommand;

public class SunCommand extends SimpleCommand {

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "weather clear");
        return true;
    }
    
}
