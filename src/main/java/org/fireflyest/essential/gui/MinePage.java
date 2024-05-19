package org.fireflyest.essential.gui;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.fireflyest.craftgui.button.ButtonItemBuilder;
import org.fireflyest.craftgui.view.TemplatePage;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.bean.Home;
import org.fireflyest.essential.service.EssentialService;

public class MinePage extends TemplatePage {

    private final EssentialService service;

    protected MinePage(EssentialService service, String target) {
        super("Mine", target, 0, 54);
        this.service = service;
        
        
        this.refreshPage();
    }

    @Override
    public Map<Integer, ItemStack> getItemMap() {
        asyncButtonMap.clear();
        asyncButtonMap.putAll(buttonMap);

        UUID uid = UUID.fromString(service.selectSteveUid(target));

        // 家
        int homeIndex = 49;
        for (Home home : service.selectHomes(uid)) {
            ItemStack homeItem = new ButtonItemBuilder("LIME_BANNER")
                .actionShiftCommand("home " + home.getName(), "delhome " + home.getName())
                .name("§e已设置家")
                .lore("§7点击传送§2/home " + home.getName())
                .lore("§7Shift+点击删除§2/delhome " + home.getName())
                .build();
            asyncButtonMap.put(homeIndex++, homeItem);
        }
        while (homeIndex < 54) {
            String randomName = UUID.randomUUID().toString().substring(0, 4);
            ItemStack homeItem = new ButtonItemBuilder("WHITE_BANNER")
                .actionPlayerCommand("sethome " + randomName)
                .name("§e设置家")
                .lore("§7设置传送点§2/sethome " + randomName)
                .build();
            asyncButtonMap.put(homeIndex++, homeItem);
        }
        
        // 地皮
        int domainIndex = 40;
        for (String domain : service.selectDomainsNameByPlayer(uid)) {
            ItemStack domainItem = new ButtonItemBuilder("LIME_BED")
                .actionPlayerCommand("plot manager " + domain)
                .name("§e地皮" + domain)
                .lore("§7点击管理§2/plot manager " + domain)
                .build();
            asyncButtonMap.put(domainIndex++, domainItem);
        }
        while (domainIndex < 45) {
            ItemStack domainItem = new ButtonItemBuilder("WHITE_BED")
                .name("§e未创建")
                .build();
            asyncButtonMap.put(domainIndex++, domainItem);
        }

        return asyncButtonMap;
    }

    @Override
    public void refreshPage() {
        buttonMap.put(10, new ButtonItemBuilder("PLAYER_HEAD")
            .name("&b&l" + target)
            .build());
        
        ItemStack permission = new ButtonItemBuilder(Material.BOOK)
            .actionOpenPage(Essential.VIEW_PERMISSION + "." + target)
            .name("§e个人权限")
            .build();
        buttonMap.put(4, permission);
        ItemStack prefix = new ButtonItemBuilder(Material.NAME_TAG)
            .actionOpenPage(Essential.VIEW_PREFIX + "." + target)
            .name("§e个人称号")
            .build();
        buttonMap.put(5, prefix);
    }
    
}
