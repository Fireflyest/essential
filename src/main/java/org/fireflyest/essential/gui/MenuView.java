package org.fireflyest.essential.gui;

import java.util.HashMap;
import java.util.Map;

import org.fireflyest.craftgui.api.View;
import org.fireflyest.essential.service.EssentialService;

public class MenuView implements View<MenuPage> {

    private final Map<String, MenuPage> pageMap = new HashMap<>();

    private final EssentialService service;

    public MenuView(EssentialService service) {
        this.service = service;
    }

    @Override
    public MenuPage getFirstPage(String target) {
        pageMap.computeIfAbsent(target, k -> new MenuPage(service, target));
        return pageMap.get(target);
    }

    @Override
    public void removePage(String target) {
        // 
    }
    
}
