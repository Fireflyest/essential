package org.fireflyest.essential.command;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;

public class EnchantCommand extends SimpleCommand {

    @Override
    protected boolean execute(CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        // 给随机附魔
        int enchantNum = this.getRandNum(10);
        for (int i = 0; i < enchantNum; i++) {
            Enchantment enchantment = this.getRandEnchantment();
            if (enchantment.canEnchantItem(item)) {
                item.addEnchantment(enchantment, enchantment.getMaxLevel());
            }
        }

        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(arg1));
        item.addEnchantment(enchantment, enchantment.getMaxLevel());
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1, String arg2) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(arg1));
        item.addUnsafeEnchantment(enchantment, NumberConversions.toInt(arg2));
        return true;
    }

    /**
     * 随机附魔
     * @return 附魔
     */
    private Enchantment getRandEnchantment() {
        Random rand;
        try {
            rand = SecureRandom.getInstanceStrong();
            return Enchantment.values()[rand.nextInt(Enchantment.values().length)];
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return Enchantment.DAMAGE_ALL;
    }

    /**
     * 随机等级
     * @param max 最大等级
     * @return 等级
     */
    private int getRandNum(int max) {
        Random rand;
        try {
            rand = SecureRandom.getInstanceStrong();
            return rand.nextInt(max) + 1;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return 1;
    }
    
}
