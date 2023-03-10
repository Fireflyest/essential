package org.fireflyest.essential.gui;

import java.util.HashMap;
import java.util.Map;

import org.fireflyest.craftgui.api.View;
import org.fireflyest.essential.service.EssentialService;

public class PrefixView implements View<PrefixPage> {

    private final Map<String, PrefixPage> pageMap = new HashMap<>();

    private EssentialService service;

    public PrefixView(EssentialService service) {
        this.service = service;
    }

    @Override
    public PrefixPage getFirstPage(String target){
        pageMap.computeIfAbsent(target, k -> new PrefixPage(service, target));
        return pageMap.get(target);
    }

    @Override
    public void removePage(String target) {
        pageMap.remove(target);
    }

}
