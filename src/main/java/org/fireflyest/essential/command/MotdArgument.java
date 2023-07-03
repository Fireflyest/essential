package org.fireflyest.essential.command;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.bukkit.command.CommandSender;
import org.fireflyest.craftcommand.argument.Argument;
import org.fireflyest.essential.Essential;

public class MotdArgument implements Argument {

    private final Set<String> set = new HashSet<>();

    public MotdArgument() {
        File folder = new File(Essential.getPlugin().getDataFolder(), "logo");
        folder.mkdirs();
        for (String imageFile : folder.list()) {
            if (imageFile.endsWith("png")) {
                set.add(imageFile.replace(".png", "")); 
            }
        }  
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
