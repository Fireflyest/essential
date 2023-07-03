package org.fireflyest.essential.gui;

import org.fireflyest.craftgui.api.View;
import org.fireflyest.essential.world.EssentialTimings;

public class TimingView implements View<TimingPage> {

    private EssentialTimings timings;

    public TimingView(EssentialTimings timings) {
        this.timings = timings;
    }

    @Override
    public TimingPage getFirstPage(String target) {
        return new TimingPage(0, timings);
    }

    @Override
    public void removePage(String target) {
        // 
    }
    
}
