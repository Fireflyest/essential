package org.fireflyest.essential.command;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.data.StateCache;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.essential.util.ChatUtils;

public class LoginCommand extends SimpleCommand {

    private EssentialService service;
    private StateCache cache;

    private final HashMap<String, Integer> errorNum = new HashMap<>();

    /**
     * 登录指令
     * @param service 数据服务
     * @param cache 缓存
     */
    public LoginCommand(EssentialService service, StateCache cache) {
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
        UUID uid = player.getUniqueId();

        // 是否未注册或已登录
        String state = cache.get(name + ".account.state");
        if (StateCache.UN_REGISTER.equals(state)) {
            player.sendMessage(Language.DON_REGISTER);
            return true;
        } else if (StateCache.LOGIN.equals(state)) {
            player.sendMessage(Language.HAS_LOGIN);
            return true;
        }
        // 密码错误
        String password = service.selectPassword(uid);
        if (!arg1.equals(password)) {
            player.sendMessage(Language.ERROR_PASSWORD.replace("%amount%", this.addError(name) + ""));
            if (this.getError(name) == 3) {
                ChatUtils.sendCommandButton(player, "忘记密码", "red", "点击找回密码", "/account losepw");
            }
            if (this.getError(name) >= 5) {
                player.kickPlayer(Language.ERROR_TOO_MUCH);
                this.clearError(name);
            }
            return true;
        }
        // 修改状态
        this.clearError(name);
        cache.set(name + ".account.state", StateCache.LOGIN);
        player.setGameMode(GameMode.SURVIVAL);
        player.sendMessage(Language.SUC_LOGIN);
        // 最后离开位置
        Location quit = service.selectQuit(player.getUniqueId());
        if (quit != null) {
            player.teleport(quit);
        }
        // 提醒绑定邮箱
        if ("".equals(service.selectEmail(uid))) {
            player.sendMessage(Language.REMIND_EMAIL);
        }
        
        return true;
    }
    
    private int getError(String name) {
        errorNum.computeIfAbsent(name, k -> 0);
        return errorNum.get(name);
    }

    private int addError(String name) {
        errorNum.computeIfAbsent(name, k -> 0);
        errorNum.put(name, errorNum.get(name) + 1);
        return errorNum.get(name);
    }

    private void clearError(String name) {
        errorNum.put(name, 0);
    }

}
