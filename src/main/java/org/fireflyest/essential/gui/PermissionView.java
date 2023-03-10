package org.fireflyest.essential.gui;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.fireflyest.craftgui.api.View;
import org.fireflyest.essential.data.EssentialYaml;
import org.fireflyest.essential.service.EssentialService;

public class PermissionView implements View<PermissionPage> {

    private final Map<String, PermissionPage> pageMap = new HashMap<>();

    private EssentialService service;
    private EssentialYaml yaml;

    public PermissionView(EssentialService service, EssentialYaml yaml) {
        this.service = service;
        this.yaml = yaml;
    }

    @Override
    public @Nullable PermissionPage getFirstPage(@Nullable String target) {
        pageMap.computeIfAbsent(target, k -> new PermissionPage(service, yaml, target));
        return pageMap.get(target);
    }

    @Override
    public void removePage(@Nullable String target) {
        // 
    }
    
}
