package org.fireflyest.essential.gui;

import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.fireflyest.craftgui.button.ButtonItemBuilder;
import org.fireflyest.craftgui.view.TemplatePage;
import org.fireflyest.craftitem.builder.ItemBuilder;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.bean.Confer;
import org.fireflyest.essential.bean.Permit;
import org.fireflyest.essential.data.EssentialYaml;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.util.TimeUtils;

public class PermissionPage extends TemplatePage {

    private EssentialService service;
    private EssentialYaml yaml;

    /**
     * 权限页面
     * @param service 数据服务
     * @param yaml 配置数据
     * @param target uuid
     */
    protected PermissionPage(EssentialService service, EssentialYaml yaml, String target) {
        super("§9§l权限信息", target, 0, 54);
        this.service =service;
        this.yaml = yaml;

        this.refreshPage();
    }

    @Override
    public @Nonnull Map<Integer, ItemStack> getItemMap() {
        asyncButtonMap.clear();
        asyncButtonMap.putAll(buttonMap);

        Player player = Bukkit.getPlayerExact(target);
        if (player == null) {
            return asyncButtonMap;
        }
        UUID uid = player.getUniqueId();

        int pos = 0;
        for (String groupName : yaml.getGroup().getKeys(false)) {
            ItemStack item;
            String button = yaml.getGroup().getString(groupName + ".button");
            String name = yaml.getGroup().getString(groupName + ".name");
            String desc = yaml.getGroup().getString(groupName + ".desc");
            Confer confer = null;
            // 判断是否在该组
            if ("default".equals(groupName) 
                    || (confer = service.selectConfer(uid, groupName)) != null) {
                
                String deadline = (confer == null || confer.getDeadline() == -1) ? "§r$<c=#95afc0>无限期" : "§r$<c=#95afc0>" + TimeUtils.getLocalDate(confer.getDeadline()) + "过期";
                ItemBuilder itemBuilder = new ButtonItemBuilder(button)
                        .name("§r§l$<hg=#6ab04c:#78e08f>" + name + "§r$<c=#a6a6a6> (" + groupName + ")")
                        .lore("§r$<c=#f6f6f6>" + desc)
                        .lore(deadline);
                
                // 遍历权限
                for (String permission : yaml.getGroup().getStringList(groupName + ".permissions")) {
                    String[] kv = permission.split(",");
                    String permissionString = (Boolean.parseBoolean(kv[1]) ? "§r$<c=#a6a6a6>• $<c=#badc58>" : "§r$<c=#a6a6a6>• $<c=#ff7979>");
                    itemBuilder.lore(permissionString + kv[0].replace(".", "․"));
                }
                item = itemBuilder.colorful().build();
            } else {
                item = new ButtonItemBuilder(Material.RED_STAINED_GLASS)
                        .name("§r§l$<hg=#eb4d4b:#f0932b>" + name + "§r$<c=#a6a6a6> (" + groupName + ")")
                        .lore("§r$<c=#f6f6f6>" + desc)
                        .lore("§r$<c=#95afc0>未拥有")
                        .colorful()
                        .build();
            }
            asyncButtonMap.put(pos++, item);
        }

        ItemBuilder permissionBuilder = new ButtonItemBuilder(Material.BOOK)
                .name("§e个人权限");
        for (Permit permit : service.selectPermits(uid)) {
            String permissionString = (permit.isValue() ? "§r§7• §a" : "§r§7• §c");
            permissionBuilder.lore(permissionString + permit.getName().replace(".", "․"));
        }
        asyncButtonMap.put(45, permissionBuilder.build());

        return asyncButtonMap;
    }

    @Override
    public void refreshPage() {
        ItemStack close = new ButtonItemBuilder(Material.REDSTONE)
                .actionBack()
                .name("§c关闭")
                .build();
        buttonMap.put(53, close);
        ItemStack blank = new ButtonItemBuilder(Material.WHITE_STAINED_GLASS_PANE)
                .name(" ")
                .build();
        for (int i = 36; i < 45; i++) {
            buttonMap.put(i, blank);
        }
        ItemStack permission = new ButtonItemBuilder(Material.BOOK)
                .name("§e个人权限")
                .build();
        buttonMap.put(45, permission);
        ItemStack prefix = new ButtonItemBuilder(Material.NAME_TAG)
                .actionOpenPage(Essential.VIEW_PREFIX + "." + target)
                .name("§e个人称号")
                .build();
        buttonMap.put(46, prefix);
    }
    
}
