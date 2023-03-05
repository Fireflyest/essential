package org.fireflyest.essential.gui;

import javax.annotation.Nullable;

import org.fireflyest.craftgui.api.View;
import org.fireflyest.essential.data.EssentialYaml;

public class WorldsView implements View<WorldsPage> {

    private final WorldsPage worldsPage;

    /**
     * 世界视图
     */
    public WorldsView(EssentialYaml yaml) {
        this.worldsPage = new WorldsPage(yaml, -1, 36);
    }

    @Override
    public @Nullable WorldsPage getFirstPage(@Nullable String s) {
        return worldsPage;
    }

    @Override
    public void removePage(@Nullable String s) {
        // 
    }

}
