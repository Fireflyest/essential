package org.fireflyest.essential.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.bukkit.command.CommandSender;
import org.fireflyest.craftcommand.argument.Argument;
import org.fireflyest.essential.data.EssentialYaml;

public class GroupArgument implements Argument {

    private EssentialYaml yaml;

    /**
     * 权限组提示
     * @param yaml 数据文件
     */
    public GroupArgument(EssentialYaml yaml) {
        this.yaml = yaml;
    }

    @Override
    public List<String> tab(@Nonnull CommandSender sender, @Nonnull String arg) {
        List<String> ret = new ArrayList<>();
        Set<String>groups = yaml.getGroup().getKeys(false);
        for (String group : groups) {
            if (group.startsWith(arg)) {
                ret.add(group);
            }
        }
        return ret;
    }
    
}
