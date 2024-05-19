package org.fireflyest.essential.gui;

import java.util.HashMap;
import java.util.Map;

import org.fireflyest.craftgui.api.View;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.essential.world.Dimension;

public class DomainView implements View<DomainPage> {

    private final Map<String, DomainPage> pageMap = new HashMap<>();

    private final EssentialService service;
    private final Map<String, Dimension> worldMap;
    
    public DomainView(EssentialService service, Map<String, Dimension> worldMap) {
        this.service = service;
        this.worldMap = worldMap;
    }

    @Override
    public DomainPage getFirstPage(String target) {
        pageMap.computeIfAbsent(target, k -> new DomainPage(service, worldMap, target));
        return pageMap.get(target);
    }

    @Override
    public void removePage(String target) {
        // 
    }
    
}
