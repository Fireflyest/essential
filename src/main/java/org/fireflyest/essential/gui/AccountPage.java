package org.fireflyest.essential.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.fireflyest.craftgui.api.ViewPage;
import org.fireflyest.craftitem.builder.ItemBuilder;
import org.fireflyest.essential.data.StateCache;

public class AccountPage implements ViewPage {

    public static final String TIP_TEXT = "在此输入密码";

    private final Map<Integer, ItemStack> itemMap = new HashMap<>();
    private final Map<Integer, ItemStack> crashMap = new HashMap<>();
    private Inventory inventory;
    private final String target;
    private String password = "";
    private boolean isRegister = false;

    /**
     * 登录界面
     * @param target 玩家
     * @param cache 缓存
     */
    public AccountPage(String target, StateCache cache) {
        this.target = target;
        this.isRegister = StateCache.UN_REGISTER.equals(cache.get(target + ".account.state"));

        this.updateTitle(isRegister ? "§9§l注册账号" : "§9§l登录账号");
    }

    @Override
    public Map<Integer, ItemStack> getItemMap() {
        ItemStack tip = new ItemBuilder(Material.NAME_TAG)
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .name(TIP_TEXT)
                .lore("§7在上方输入框输入密码")
                .build();

        ItemStack login = new ItemBuilder(Material.NAME_TAG)
                .name("§f[§a点击确认§f]")
                .lore("§3密码§7: §f" + password)
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .build();

        crashMap.put(0, tip);
        crashMap.put(2, login);
        return crashMap;
    }

    @Override
    public Map<Integer, ItemStack> getButtonMap() {
        return itemMap;
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot != 2) {
            return crashMap.get(slot);
        }
        Player player = Bukkit.getPlayerExact(target);
        if (player != null && !Objects.equals(password, TIP_TEXT)) {
            if (isRegister) {
                player.performCommand("register " + password);
            } else {
                player.performCommand("login " + password);
            }
            player.closeInventory();
        }
        return crashMap.get(slot);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public int getPage() {
        return 0;
    }

    @Override
    public ViewPage getNext() {
        return null;
    }

    @Override
    public ViewPage getPre() {
        return null;
    }

    @Override
    public void setNext(ViewPage next) {
        // 只有一页
    }

    @Override
    public void setPre(ViewPage pre) {      
        // 只有一页  
    }

    @Override
    public void refreshPage() {
        // 一次性
    }

    @Override
    public void updateTitle(String title) {
        this.inventory = Bukkit.createInventory(null, InventoryType.ANVIL, title);
    }

    /**
     * 更新密码
     * @param passwordStr 密码
     */
    public void updatePassword(String passwordStr) {
        if (!passwordStr.equals(password)) {
            password = passwordStr;
        }
    }

    /**
     * 删减
     */
    public void reducePassword() {
        if (password.length() > 0) {
            password = password.substring(0, password.length() - 1);
        }
    }
    
}
