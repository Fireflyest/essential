package org.fireflyest.essential.command;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.data.StateCache;
import org.fireflyest.essential.service.EssentialService;

public class RegisterCommand extends SimpleCommand {

    private EssentialService service;
    private StateCache cache;

    /**
     * 注册指令
     * @param service 数据服务
     * @param cache 缓存
     */
    public RegisterCommand(EssentialService service, StateCache cache) {
        this.service = service;
        this.cache = cache;
    }

    @Override
    protected boolean execute(CommandSender sender) {

        return false;
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        String name = player.getName();

        if (!StateCache.UN_REGISTER.equals(cache.get(name + ".account.state"))) {
            player.sendMessage(Language.HAS_REGISTER);// 已被注册
            return true;
        }
        if (arg1.equals(player.getName())) {
            player.sendMessage(Language.NAME_PASSWORD);// 使用游戏名
            return true;
        }
        if (arg1.length() < 6) {
            player.sendMessage(Language.SHORT_PASSWORD);// 太短
            return true;
        }

        // 更新密码
        service.updatePassword(arg1, player.getUniqueId());
        // 更新状态
        cache.set(player.getName() + ".account.state", StateCache.LOGIN);

        player.setGameMode(GameMode.SURVIVAL);
        player.sendMessage(Language.SUC_REGISTER);
        player.sendMessage(Language.REMIND_EMAIL);

        return true;
    }
    
}
