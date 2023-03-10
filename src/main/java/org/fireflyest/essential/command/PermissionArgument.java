package org.fireflyest.essential.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.fireflyest.craftcommand.argument.Argument;

public class PermissionArgument implements Argument {

    @Override
    public List<String> tab(@Nonnull CommandSender sender, @Nonnull String arg) {
        List<String> ret = new ArrayList<>();
        Set<Permission> permissions = Bukkit.getPluginManager().getPermissions();
        for (Permission permission : permissions) {
            if (permission.getName().startsWith(arg)) {
                ret.add(permission.getName());
            }
        }
        return ret;
    }
    
}
