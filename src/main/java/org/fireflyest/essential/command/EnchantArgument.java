package org.fireflyest.essential.command;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.fireflyest.craftcommand.argument.Argument;

public class EnchantArgument implements Argument {

    @Override
    public List<String> tab(@Nonnull CommandSender sender, @Nonnull String arg) {
        List<String> ret = new ArrayList<>();
        for (Enchantment enchantment : Enchantment.values()) {
            if (enchantment.getKey().getKey().startsWith(arg)) {
                ret.add(enchantment.getKey().getKey());
            }
        }
        return ret;
    }
    
}
