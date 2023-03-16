package org.fireflyest.essential.gui;

import java.util.HashMap;
import java.util.Map;

import org.fireflyest.craftgui.api.View;
import org.fireflyest.essential.service.EssentialService;

public class ShipView implements View<ShipPage> {

    private final Map<String, ShipPage> pageMap = new HashMap<>();

    private EssentialService service;

    public ShipView(EssentialService service) {
        this.service = service;
    }

    @Override
    public ShipPage getFirstPage(String target) {
        pageMap.computeIfAbsent(target, k -> new ShipPage(service, target));
        return pageMap.get(target);
    }

    @Override
    public void removePage(String target) {
        pageMap.remove(target);
    }
    
}
