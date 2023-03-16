package org.fireflyest.essential.gui;

import java.util.HashMap;
import java.util.Map;

import org.fireflyest.craftgui.api.View;
import org.fireflyest.essential.service.EssentialService;

public class InteractView implements View<InteractPage> {
    
    private final Map<String, InteractPage> pageMap = new HashMap<>();

    private EssentialService service;

    public InteractView(EssentialService service) {
        this.service = service;
    }

    @Override
    public InteractPage getFirstPage(String target) {
        pageMap.computeIfAbsent(target, k -> new InteractPage(service, target));
        return pageMap.get(target);
    }

    @Override
    public void removePage(String target) {
        // 
    }
    
}
