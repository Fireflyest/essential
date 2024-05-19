package org.fireflyest.essential.command;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.fireflyest.craftcommand.command.ComplexCommand;

public class TipCommand extends ComplexCommand {

    private static ArmorStand select;

    @Override
    protected boolean execute(CommandSender sender) {
        if (select != null) {
            select.removePotionEffect(PotionEffectType.GLOWING);
        }
        return true;
    }

    /**
     * 选择某行
     * @param armorStand 选择对象
     */
    public static void selectArmorStand(ArmorStand armorStand) {
        if (select != null) {
            select.removePotionEffect(PotionEffectType.GLOWING);
        }
        armorStand.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 60, 1));
        select = armorStand;
    }

    /**
     * 删除选中的
     */
    public static void removeArmorStand() {
        select.remove();
    }

    /**
     * 选中最下一行
     */
    public static void lastArmorStand() {
        double height = select.getLocation().getY();
        ArmorStand lastArmorStand = null;
        for (Entity entity : select.getNearbyEntities(0, 10, 0)) {
            if (!(entity instanceof ArmorStand)) {
                continue;
            }
            ArmorStand armorStand = ((ArmorStand)entity);
            double tempHeight;
            if ((tempHeight = armorStand.getLocation().getY()) < height) {
                height = tempHeight;
                lastArmorStand = armorStand;
            }
        }
         // 选择
        if (lastArmorStand != null) {
            selectArmorStand(lastArmorStand);
        }
    }

    /**
     * 获取选中的位置
     */
    public static Location getSelectLocation() {
        return select.getLocation();
    }

    /**
     * 下一行
     */
    public static void nextArmorStand() {
        for (Entity entity : select.getNearbyEntities(0.1, 1, 0.1)) {
            if ((entity instanceof ArmorStand)) {
                ArmorStand armorStand = ((ArmorStand)entity);
                if (Math.abs(select.getLocation().getY() - armorStand.getLocation().getY() - 0.3) < 0.1) {
                    selectArmorStand(armorStand);
                    break;
                }
            }
        }
    }

    /**
     * 上一行
     */
    public static void preArmorStand() {
        for (Entity entity : select.getNearbyEntities(0.1, 1, 0.1)) {
            if ((entity instanceof ArmorStand)) {
                ArmorStand armorStand = ((ArmorStand)entity);
                if (Math.abs(select.getLocation().getY() - armorStand.getLocation().getY() + 0.3) < 0.1) {
                    selectArmorStand(armorStand);
                    break;
                }
            }
        }
    }

    /**
     * 修改选中的
     */
    public static void editArmorStand(String text) {
        if (select != null) {
            select.setCustomName(text);
        }
    }
    
}
