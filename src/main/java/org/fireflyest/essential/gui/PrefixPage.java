package org.fireflyest.essential.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.checkerframework.checker.units.qual.min;
import org.fireflyest.craftgui.button.ButtonItemBuilder;
import org.fireflyest.craftgui.view.TemplatePage;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.bean.Prefix;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.util.ColorUtils;
import org.fireflyest.util.TimeUtils;

public class PrefixPage extends TemplatePage {

    private EssentialService service;

    private final Pattern colorPattern = Pattern.compile("#([0-9a-fA-F]{6})");

    protected PrefixPage(EssentialService service, String target) {
        super("§9§l个人头衔", target, 0, 54);
        this.service = service;

        this.refreshPage();
    }

    @Override
    public @Nonnull Map<Integer, ItemStack> getItemMap() {
        asyncButtonMap.clear();
        asyncButtonMap.putAll(buttonMap);

        int pos = 0;
        for (Prefix prefix : service.selectPrefixs(Bukkit.getPlayerExact(target).getUniqueId())) {
            String deadline = (prefix.getDeadline() == -1) ? "§r$<c=#95afc0>无限期" : "§r$<c=#95afc0>" + TimeUtils.getLocalDate(prefix.getDeadline()) + "过期";

            Material material = Material.WHITE_BANNER;
            ItemStack item;
            if (prefix.getValue().contains("#")) {
                item = new ButtonItemBuilder(material)
                    .actionPlayerCommand("prefix " + prefix.getId())
                    .name("§f[" + prefix.getValue() + "§f]")
                    .lore(deadline)
                    .colorful()
                    .build();

                Matcher matcher = colorPattern.matcher(prefix.getValue());
                List<Color> colors = new ArrayList<>();
                while (matcher.find()) {
                    String colorString = matcher.group().replace("#", "");
                    Color color = ColorUtils.toColor(colorString);
                    colors.add(color);
                }
                BannerMeta bannerMeta = ((BannerMeta)item.getItemMeta());
                if (colors.size() == 1) {
                    DyeColor dyeColor = this.getNearDyeColor(colors.get(0));
                    org.bukkit.block.banner.Pattern pattern = new org.bukkit.block.banner.Pattern(dyeColor, PatternType.BASE);
                    bannerMeta.addPattern(pattern);
                } else if (colors.size() == 2) {
                    org.bukkit.block.banner.Pattern pattern0 = new org.bukkit.block.banner.Pattern(DyeColor.WHITE, PatternType.BASE);
                    bannerMeta.addPattern(pattern0);
                    
                    DyeColor dyeColor1 = this.getNearDyeColor(colors.get(0));
                    org.bukkit.block.banner.Pattern pattern1 = new org.bukkit.block.banner.Pattern(dyeColor1, PatternType.HALF_VERTICAL);
                    bannerMeta.addPattern(pattern1);
                    
                    DyeColor dyeColor2 = this.getNearDyeColor(colors.get(1));
                    org.bukkit.block.banner.Pattern pattern2 = new org.bukkit.block.banner.Pattern(dyeColor2, PatternType.HALF_VERTICAL_MIRROR);
                    bannerMeta.addPattern(pattern2);
                }
                item.setItemMeta(bannerMeta);

            } else {
                if (prefix.getValue().startsWith("§6")){
                    material = Material.ORANGE_BANNER;
                } else if (prefix.getValue().startsWith("§d")) {
                    material = Material.MAGENTA_BANNER;
                } else if (prefix.getValue().startsWith("§b")) {
                    material = Material.LIGHT_BLUE_BANNER;
                } else if (prefix.getValue().startsWith("§e")) {
                    material = Material.YELLOW_BANNER;
                } else if (prefix.getValue().startsWith("§a")) {
                    material = Material.LIME_BANNER;
                } else if (prefix.getValue().startsWith("§c")) {
                    material = Material.PINK_BANNER;
                } else if (prefix.getValue().startsWith("§8")) {
                    material = Material.GRAY_BANNER;
                } else if (prefix.getValue().startsWith("§7")) {
                    material = Material.LIGHT_GRAY_BANNER;
                } else if (prefix.getValue().startsWith("§3")) {
                    material = Material.CYAN_BANNER;
                } else if (prefix.getValue().startsWith("§5")) {
                    material = Material.PURPLE_BANNER;
                } else if (prefix.getValue().startsWith("§1") || prefix.getValue().startsWith("§9")) {
                    material = Material.BLUE_BANNER;
                } else if (prefix.getValue().startsWith("§2")) {
                    material = Material.GREEN_BANNER;
                } else if (prefix.getValue().startsWith("§4")) {
                    material = Material.RED_BANNER;
                } else if (prefix.getValue().startsWith("§0")) {
                    material = Material.BLACK_BANNER;
                }else {
                    material = Material.WHITE_BANNER;
                }
                item = new ButtonItemBuilder(material)
                    .actionPlayerCommand("prefix " + prefix.getId())
                    .name("§f[" + prefix.getValue() + "§f]")
                    .lore(deadline)
                    .colorful()
                    .build();
            }

            asyncButtonMap.put(pos++, item);
        }

        return asyncButtonMap;
    }

    @Override
    public void refreshPage() {
        ItemStack close = new ButtonItemBuilder(Material.REDSTONE)
                .actionBack()
                .name("§c关闭")
                .build();
        buttonMap.put(53, close);
        ItemStack blank = new ButtonItemBuilder(Material.WHITE_STAINED_GLASS_PANE)
                .name(" ")
                .build();
        for (int i = 36; i < 45; i++) {
            buttonMap.put(i, blank);
        }
        ItemStack prefix = new ButtonItemBuilder(Material.BOOK)
                .actionOpenPage(Essential.VIEW_PERMISSION + "." + target)
                .name("§e权限信息")
                .lore("§r§f点击跳转")
                .build();
        buttonMap.put(45, prefix);
    }
    
    /**
     * 获取和颜色最接近的染色
     * @param color 颜色
     * @return 染色
     */
    private DyeColor getNearDyeColor(Color color) {
        int min = 255 * 255 * 3;
        DyeColor ret = DyeColor.WHITE;
        for (DyeColor dyeColor : DyeColor.values()) {
            int distance = ColorUtils.distance(color, dyeColor.getColor());
            if (distance < min) {
                min = distance;
                ret = dyeColor;
            }
        }
        return ret;
    }

}
