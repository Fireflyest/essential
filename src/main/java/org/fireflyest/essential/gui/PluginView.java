package org.fireflyest.essential.gui;

import javax.annotation.Nullable;

import org.fireflyest.craftgui.api.View;

public class PluginView implements View<PluginPage> {
    
    private final PluginPage page;

    /**
     * 插件列表
     */
    public PluginView() {
        this.page = new PluginPage(0, 54);
    }

    @Override
    public @Nullable PluginPage getFirstPage(@Nullable String s) {
        return page;
    }

    @Override
    public void removePage(@Nullable String s) {
        // 
    }

}
