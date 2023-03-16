package org.fireflyest.essential.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.bukkit.command.CommandSender;
import org.fireflyest.craftcommand.argument.Argument;

public class MuteArgument implements Argument {

    private final Set<String> set = new HashSet<>();

    public MuteArgument() {
        set.add("发送聊天信息过于频繁");
        set.add("辱骂或侮辱他人");
        set.add("私自发送宣传广告");
        set.add("发送不当言论");
        set.add("敏感话题");
    }

    @Override
    public List<String> tab(@Nonnull CommandSender sender, @Nonnull String arg) {
        List<String> ret = new ArrayList<>();
        for (String hg : set) {
            if (hg.startsWith(arg)) {
                ret.add(hg);
            }
        }
        return ret;
    }
    
}
