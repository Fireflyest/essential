package org.fireflyest.essential.gui;

import java.util.HashMap;
import java.util.Map;

import org.fireflyest.craftgui.api.View;
import org.fireflyest.essential.service.EssentialService;

public class MineView implements View<MinePage> {

    private final Map<String, MinePage> pageMap = new HashMap<>();

    private final EssentialService service;

    public MineView(EssentialService service) {
        this.service = service;
    }

    @Override
    public MinePage getFirstPage(String target) {
        pageMap.computeIfAbsent(target, k -> new MinePage(service, target));
        return pageMap.get(target);
    }

    @Override
    public void removePage(String target) {
        // 
    }

}
