package org.fireflyest.essential.command;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.service.EssentialService;

public class LegalCommand extends SubCommand {

    private final EssentialService service;

    

    public LegalCommand(EssentialService service) {
        this.service = service;
    }

    @Override
    protected boolean execute(CommandSender sender) {
        String uid = service.selectSteveUid(sender.getName());
        if (!"".equals(uid)) {
            UUID uuid = UUID.fromString(uid);
            boolean legal = !service.selectLegal(uuid);
            service.updateLegal(legal, uuid);
            sender.sendMessage(Language.TITLE + "正版验证: §3" + legal);
        }
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        if (!sender.isOp()) {
            return false;
        }
        String uid = service.selectSteveUid(arg1);
        if (!"".equals(uid)) {
            UUID uuid = UUID.fromString(uid);
            boolean legal = !service.selectLegal(uuid);
            service.updateLegal(legal, uuid);
            sender.sendMessage(Language.TITLE + "玩家" + arg1 + "正版验证: §3" + legal);
        }
        return true;
    }
    
}
