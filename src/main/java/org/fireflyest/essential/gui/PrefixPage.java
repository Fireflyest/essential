package org.fireflyest.essential.gui;

import java.util.Map;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.fireflyest.craftgui.button.ButtonItemBuilder;
import org.fireflyest.craftgui.view.TemplatePage;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.bean.Prefix;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.util.ColorUtils;
import org.fireflyest.util.TimeUtils;

public class PrefixPage extends TemplatePage {

    private EssentialService service;

    protected PrefixPage(EssentialService service, String target) {
        super("§9§l个人头衔", target, 0, 54);
        this.service = service;
    }

    @Override
    public @Nonnull Map<Integer, ItemStack> getItemMap() {
        asyncButtonMap.clear();
        asyncButtonMap.putAll(buttonMap);

        int pos = 0;
        for (Prefix prefix : service.selectPrefixs(Bukkit.getPlayerExact(target).getUniqueId())) {
            String deadline = (prefix.getDeadline() == -1) ? "§r$<c=#95afc0>无限期" : "§r$<c=#95afc0>" + TimeUtils.getLocalDate(prefix.getDeadline()) + "过期";

            ItemStack item = new ButtonItemBuilder(Material.WHITE_BANNER)
                    .actionPlayerCommand("prefix " + prefix.getId())
                    .name("§f[" + prefix.getValue() + "$f]")
                    .lore(deadline)
                    .colorful()
                    .build();
            
            // TODO: 
            // BannerMeta bannerMeta = ((BannerMeta)item.getItemMeta());
            // bannerMeta.setPattern(0, new Pattern(DyeColor.getByColor(Color.AQUA), PatternType.));

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
    
}
