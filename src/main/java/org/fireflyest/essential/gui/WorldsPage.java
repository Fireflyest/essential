package org.fireflyest.essential.gui;

import java.util.Map;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.fireflyest.craftgui.button.ButtonItemBuilder;
import org.fireflyest.craftgui.view.TemplatePage;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.data.EssentialYaml;

public class WorldsPage extends TemplatePage {

    private EssentialYaml yaml;

    /**
     * 世界管理界面
     * @param page 页码
     * @param size 页面大小
     */
    public WorldsPage(EssentialYaml yaml, int page, int size) {
        super("§9§l世界管理", null, page, size);
        this.yaml = yaml;
    }

    @Override
    public @Nonnull Map<Integer, ItemStack> getItemMap() {
        asyncButtonMap.clear();
        asyncButtonMap.putAll(buttonMap);

        int i = 0;
        // 遍历每个世界
        for (World world : Bukkit.getWorlds()) {
            // 根据环境不同显示按钮
            String worldName = world.getName();
            String environment;
            String color;
            switch (world.getEnvironment()) {
                case NETHER:
                    environment = "NETHERRACK";
                    color = "$<hg=#c62021:#f18065>";
                    break;
                case THE_END:
                    environment = "END_STONE";
                    color = "$<hg=#7e349d:#ab69c6>";
                    break;
                case NORMAL:
                default:
                    environment = "GRASS_BLOCK";
                    color = "$<hg=#179e50:#3edc81>";
            }
            // 配置数据
            String display =  yaml.getWorld().getString(String.format("%s.display", worldName));
            boolean protect =  yaml.getWorld().getBoolean(String.format("%s.protect", worldName));
            boolean pvp =  yaml.getWorld().getBoolean(String.format("%s.pvp", worldName));
            boolean explode =  yaml.getWorld().getBoolean(String.format("%s.explode", worldName));


            ItemStack worldButton = new ButtonItemBuilder(environment)
                    .actionOpenPage(Essential.VIEW_CHUNKS + "." + worldName)
                    .name(String.format("§r%s%s", color, world.getName()))
                    .lore(String.format("§r$<c=#6ab04c>名称$<c=#f6f6f6>: $<c=#ffbe76>%s", display))
                    .lore(String.format("§r$<c=#6ab04c>保护$<c=#f6f6f6>: $<c=#ffbe76>%s", protect))
                    .lore(String.format("§r$<c=#6ab04c>杀伤$<c=#f6f6f6>: $<c=#ffbe76>%s", pvp))
                    .lore(String.format("§r$<c=#6ab04c>爆炸$<c=#f6f6f6>: $<c=#ffbe76>%s", explode))
                    .lore(String.format("§r$<c=#6ab04c>区块$<c=#f6f6f6>: $<c=#ffbe76>%s", world.getLoadedChunks().length))
                    .lore(String.format("§r$<c=#6ab04c>玩家$<c=#f6f6f6>: $<c=#ffbe76>%s", world.getPlayers().size()))
                    .colorful()
                    .build();
            asyncButtonMap.put(i++, worldButton);
        }

        return asyncButtonMap;
    }

    @Override
    public void refreshPage() {
        ItemStack close = new ButtonItemBuilder("REDSTONE")
                .actionClose()
                .name("§c关闭")
                .build();
        buttonMap.put(35, close);
    }
}

