package org.fireflyest.essential.command;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.essential.data.Language;

public class WorldGenerateCommand extends SubCommand {
    
    @Override
    protected boolean execute(CommandSender sender) {
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        return this.execute(sender, arg1, "Essential");
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1, String arg2) {
        return this.execute(sender, arg1, arg2, arg1);
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1, String arg2, String arg3) {
        sender.sendMessage(Language.WORLD_CREATE + arg1);
        WorldCreator worldCreator = new WorldCreator(arg1)
            .generator(arg2 + ":" + arg3, sender);
        Bukkit.createWorld(worldCreator);
        return true;
    }
}
