package org.fireflyest.essential.gui;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.fireflyest.craftgui.api.View;

public class ChunksView implements View<ChunksPage> {

    private final Map<String, ChunksPage> pageMap = new HashMap<>();

    public ChunksView() {
    }

    @Override
    public @Nullable ChunksPage getFirstPage(@Nullable String s) {
        if (!pageMap.containsKey(s)){
            pageMap.put(s, new ChunksPage(s, 54));
        }
        return pageMap.get(s);
    }

    @Override
    public void removePage(@Nullable String s) {
        // 
    }

}
