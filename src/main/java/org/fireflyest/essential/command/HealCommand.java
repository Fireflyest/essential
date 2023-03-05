package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.essential.data.Language;

public class HealCommand extends SimpleCommand {

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        this.healPlayer(player);
        return true;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender, @Nonnull String arg1) {
        Player target = Bukkit.getPlayer(arg1);
        if (target == null) {
            sender.sendMessage(Language.OFFLINE_PLAYER.replace("%player%", arg1));
            return true;
        }
        this.healPlayer(target);
        return super.execute(sender, arg1);
    }

    private void healPlayer(Player player) {
        double maxHealth = 20;
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute != null) {
            maxHealth = attribute.getValue();
        }
        player.setHealth(maxHealth);
        player.setFoodLevel(20);
        for (PotionEffect effect: player.getActivePotionEffects()) {
            if (effect.getType() == PotionEffectType.FIRE_RESISTANCE
                    || effect.getType() == PotionEffectType.DAMAGE_RESISTANCE
                    || effect.getType() == PotionEffectType.JUMP
                    || effect.getType() == PotionEffectType.LUCK
                    || effect.getType() == PotionEffectType.HERO_OF_THE_VILLAGE
                    || effect.getType() == PotionEffectType.REGENERATION
                    || effect.getType() == PotionEffectType.SPEED
                    || effect.getType() == PotionEffectType.INVISIBILITY) {
                continue;
            }
            player.removePotionEffect(effect.getType());
        }
        player.setFireTicks(0);
        player.sendMessage(Language.TITLE + "已恢复状态");
    }
    
}
