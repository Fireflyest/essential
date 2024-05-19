package org.fireflyest.essential.command;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.craftmsg.MessageService;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.world.WorldCleaner;

public class CleanCommand extends SimpleCommand {

    private final MessageService message;

    public CleanCommand(MessageService message) {
        this.message = message;
    }

    @Override
    protected boolean execute(CommandSender sender) {
        message.pushGlobalMessage("§f" + Language.WORLD_CLEAN, 15);
        for (World world : Bukkit.getWorlds()) {
            WorldCleaner cleaner = new WorldCleaner(world);
            cleaner.clean();
        }
        return true;
    }
    
}
