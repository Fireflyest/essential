package org.fireflyest.essential.gui;

import java.util.Map;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.fireflyest.craftgui.button.ButtonItemBuilder;
import org.fireflyest.craftgui.view.TemplatePage;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.service.EssentialService;

public class MenuPage extends TemplatePage {

    private final EssentialService service;

    protected MenuPage(EssentialService service, String target) {
        super("Menu", target, 0, 54);
        this.service = service;
        
        this.refreshPage();
    }

    @Override
    public Map<Integer, ItemStack> getItemMap() {
        asyncButtonMap.clear();
        asyncButtonMap.putAll(buttonMap);


        return asyncButtonMap;
    }

    @Override
    public void refreshPage() {
        buttonMap.put(0, new ButtonItemBuilder("PLAYER_HEAD")
            .actionOpenPage(Essential.VIEW_MINE + "." + target)
            .name("&b&l个人信息")
            .build());

        buttonMap.put(4, new ButtonItemBuilder("SPYGLASS")
            .actionOpenPage("essential.menu." + target)
            .name("&b&l帮助")
            .build());
        buttonMap.put(5, new ButtonItemBuilder("OAK_BOAT")
            .actionOpenPage(Essential.VIEW_SHIP + "." + target)
            .name("&b&l好友")
            .build());
        buttonMap.put(6, new ButtonItemBuilder("PAINTING")
            .actionOpenPage("essential.menu." + target)
            .name("&b&l活动")
            .build());
        buttonMap.put(7, new ButtonItemBuilder("WRITABLE_BOOK")
            .actionOpenPage("essential.menu." + target)
            .name("&b&l任务")
            .build());
        buttonMap.put(8, new ButtonItemBuilder("RAW_GOLD")
            .actionOpenPage("essential.menu." + target)
            .name("&b&l商城")
            .build());
        
        buttonMap.put(29, new ButtonItemBuilder("ENDER_PEARL")
            .actionOpenPage("essential.menu." + target)
            .name("&5&l传送")
            .build());
        buttonMap.put(31, new ButtonItemBuilder("GOLDEN_AXE")
            .actionOpenPage("essential.menu." + target)
            .name("&4&l副本")
            .flags(ItemFlag.HIDE_ATTRIBUTES)
            .build());
        buttonMap.put(33, new ButtonItemBuilder("DIAMOND_SWORD")
            .actionOpenPage("essential.menu." + target)
            .name("&4&l对决")
            .flags(ItemFlag.HIDE_ATTRIBUTES)
            .build());
    }
    
}
