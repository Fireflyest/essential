package org.fireflyest.essential.command;

import org.bukkit.command.CommandSender;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.data.StateCache;

public class MotdCommand extends SimpleCommand {

    private StateCache cache;

    public MotdCommand(StateCache cache) {
        this.cache = cache;

    }

    @Override
    protected boolean execute(CommandSender sender) {
        if (cache.exist("motd.type")) {
            cache.del("motd.type");
            sender.sendMessage(Language.MOTD_UPDATE);
        } else {
            cache.set("motd.type", "maintain");
            sender.sendMessage(Language.MOTD_MAINTAIN);
        }
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        cache.set("motd.type", arg1);
        sender.sendMessage(Language.MOTD_UPDATE);
        return true;
    }
    
}
