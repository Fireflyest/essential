package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.essential.util.EmailUtils;

public class LosepwCommand extends SubCommand {

    private EssentialService service;

    private static final String SUBJECT = "EdgeCraft - Multiplayer Server of Minecraft";
    private static final String BLANK_LINE = "    <br>\n";

    private static final String PASSWORD_TEXT = "<body>\n"
            + "    <h3>找回你在游戏中的账户密码，请妥善保管好你的密码：</h3>\n"
            + BLANK_LINE
            + BLANK_LINE
            + "    <b style=\"font-size: small; color: #6c5ce7;\">密码刮刮乐</b><br>\n"
            + "    <b style=\"color: #000000; font-size: x-large; background-color: #000000;\">%password%</b>\n"
            + BLANK_LINE
            + BLANK_LINE
            + BLANK_LINE
            + "    <span>\n"
            + "        <p>游戏账户<b style=\"color: #0984e3; font-size: large;\"> %player% </b></p>\n"
            + "        <p>修改密码可使用 <b style=\"color: #d63031; font-size: large;\">/account changepw </b><b style=\"color: #d63031; font-size: small;\">[旧密码] [新密码]</b></p>\n"
            + "        <p>祝你游戏愉快~</p>\n"
            + "    </span>\n"
            + BLANK_LINE
            + "    <p style=\"color: #636e72;\">如果这不是您本人手动所为，请忽略该邮件，并且您目前无需执行任何其它操作。</p>\n"
            + "</body>";


    /**
     * 找回密码
     * @param service 数据服务
     */
    public LosepwCommand(EssentialService service) {
        this.service = service;
    }


    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        String name = player.getName();
        String uid = player.getUniqueId().toString();
        String email = service.selectEmail(uid);
        if ("".equals(email)) {
            player.sendMessage(Language.DON_HAS_EMAIL);
        } else {
            String password = service.selectPassword(uid);
            String contend = PASSWORD_TEXT
                    .replace("%password%", password)
                    .replace("%player%", name);
            EmailUtils.sendEmail(email, SUBJECT, contend);
            player.sendMessage(Language.SUC_SEND_EMAIL.replace("%email%", email));
        }
        return true;
    }

}
