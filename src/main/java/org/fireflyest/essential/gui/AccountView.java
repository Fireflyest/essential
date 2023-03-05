package org.fireflyest.essential.gui;

import org.fireflyest.craftgui.api.View;
import org.fireflyest.essential.data.StateCache;

public class AccountView implements View<AccountPage> {

    private StateCache cache;

    /**
     * 账户界面
     * @param cache 缓存
     */
    public AccountView(StateCache cache) {
        this.cache = cache;
    }

    @Override
    public AccountPage getFirstPage(String target) {
        return new AccountPage(target, cache);
    }

    @Override
    public void removePage(String target) {
        // 无需
    }
    
}
