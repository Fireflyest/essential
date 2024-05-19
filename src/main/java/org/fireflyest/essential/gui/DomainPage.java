package org.fireflyest.essential.gui;

import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;
import org.fireflyest.craftgui.button.ButtonItemBuilder;
import org.fireflyest.craftgui.view.TemplatePage;
import org.fireflyest.essential.bean.Domain;
import org.fireflyest.essential.data.Config;
import org.fireflyest.essential.service.EssentialEconomy;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.essential.world.Dimension;
import org.fireflyest.essential.world.Plot;

public class DomainPage extends TemplatePage {

    private final EssentialService service;
    private final Map<String, Dimension> worldMap;

    protected DomainPage(EssentialService service, Map<String, Dimension> worldMap, String target) {
        super("§9" + target, target, 0, 54);
        this.service = service;
        this.worldMap = worldMap;

        this.refreshPage();
    }

    @Override
    public Map<Integer, ItemStack> getItemMap() {
        asyncButtonMap.clear();
        asyncButtonMap.putAll(buttonMap);

        String world = service.selectDomainWorld(target);
        Dimension dimension = worldMap.get(world);
        Domain domain;
        if (dimension == null || (domain = dimension.getDomain(target)) == null) {
            return asyncButtonMap;
        }

        List<Plot> plotList = domain.getPlotList();
        for (Plot plot : plotList) {
            int row = Math.abs(plot.getX() % 4);
            int column = Math.abs(plot.getZ() % 4);
            int index = row * 9 + column;
            ItemStack plotItem = new ButtonItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
                .name("§b" + plot.getX() + "§7:§b" + plot.getZ())
                .build();
            asyncButtonMap.put(index, plotItem);
        }
        for (String roadLoc : dimension.nearRoads(domain)) {
            String[] loc = roadLoc.split(":");
            int x = NumberConversions.toInt(loc[0]);
            int z = NumberConversions.toInt(loc[1]);
            int row = Math.abs(x % 4);
            int column = Math.abs(z  % 4);
            if (row != 0 && column != 0) {
                int index = row * 9 + column;
                double cost = Config.BASE_CHUNK_PRICE + (domain.getLevel() * domain.getLevel() * (Config.BASE_CHUNK_PRICE / 5.0));
                ItemStack roadItem = new ButtonItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                    .actionPlayerCommand("plot expand " + roadLoc)
                    .name("§a" + x + "§7:§a" + z)
                    .lore("§7点击扩张")
                    .lore("§7花费§f" + cost + EssentialEconomy.currencyNameDefault())
                    .build();
                asyncButtonMap.put(index, roadItem);
            }
        }

        boolean pve = dimension.isPermit(domain.getGlobe(), Dimension.PERMISSION_PVE);
        ItemStack pveItem = new ButtonItemBuilder(Material.IRON_AXE)
            .actionPlayerCommand("plot set pve")
            .flags(ItemFlag.HIDE_ATTRIBUTES)
            .name((pve ? "§a" : "§c§m") + "生物伤害")
            .lore(pve ? "§7可对非玩家生物造成伤害" : "§7无法对非玩家生物造成伤害")
            .build();
        asyncButtonMap.put(5, pveItem);

        boolean pvp = dimension.isPermit(domain.getGlobe(), Dimension.FLAG_PVP);
        ItemStack pvpItem = new ButtonItemBuilder(Material.IRON_SWORD)
            .actionPlayerCommand("plot set pvp")
            .flags(ItemFlag.HIDE_ATTRIBUTES)
            .name((pvp ? "§a" : "§c§m") + "玩家伤害")
            .lore(pvp ? "§7可对玩家造成伤害" : "§7无法对玩家造成伤害")
            .build();
        asyncButtonMap.put(5 + 9, pvpItem);

        boolean tp = dimension.isPermit(domain.getGlobe(), Dimension.PERMISSION_TP);
        ItemStack tpItem = new ButtonItemBuilder(Material.ENDER_EYE)
            .actionPlayerCommand("plot set tp")
            .name((tp ? "§a" : "§c§m") + "玩家传送")
            .lore(tp ? "§7其他玩家可传送" : "§7其他玩家无法传送")
            .build();
        asyncButtonMap.put(6, tpItem);

        boolean explode = dimension.isPermit(domain.getGlobe(), Dimension.FLAG_EXPLODE);
        ItemStack explodeItem = new ButtonItemBuilder(Material.FIRE_CHARGE)
            .actionPlayerCommand("plot set explode")
            .name((explode ? "§a" : "§c§m") + "爆炸破坏")
            .lore(explode ? "§7爆炸可破坏方块" : "§7爆炸无法破坏方块")
            .build();
        asyncButtonMap.put(6 + 9, explodeItem);

        boolean water = dimension.isPermit(domain.getGlobe(), Dimension.FLAG_FLOW_WATER);
        ItemStack waterItem = new ButtonItemBuilder(Material.WATER_BUCKET)
            .actionPlayerCommand("plot set water")
            .name((water ? "§a" : "§c§m") + "水流动")
            .lore(water ? "§7水可以流动" : "§7水无法流动")
            .build();
        asyncButtonMap.put(7, waterItem);

        boolean lava = dimension.isPermit(domain.getGlobe(), Dimension.FLAG_FLOW_LAVA);
        ItemStack lavaItem = new ButtonItemBuilder(Material.LAVA_BUCKET)
            .actionPlayerCommand("plot set lava")
            .name((lava ? "§a" : "§c§m") + "岩浆流动")
            .lore(lava ? "§7岩浆可以流动" : "§7岩浆无法流动")
            .build();
        asyncButtonMap.put(7 + 9, lavaItem);

        boolean monster = dimension.isPermit(domain.getGlobe(), Dimension.FLAG_MONSTER);
        ItemStack monsterItem = new ButtonItemBuilder(Material.SPAWNER)
            .actionPlayerCommand("plot set monster")
            .name((monster ? "§a" : "§c§m") + "怪物生成")
            .lore(monster ? "§7可生成敌对生物" : "§7无法可生成敌对生物")
            .build();
        asyncButtonMap.put(8, monsterItem);

        boolean piston = dimension.isPermit(domain.getGlobe(), Dimension.FLAG_PISTON);
        ItemStack pistonItem = new ButtonItemBuilder(Material.PISTON)
            .actionPlayerCommand("plot set piston")
            .name((piston ? "§a" : "§c§m") + "活塞推动")
            .lore(piston ? "§7活塞可激活" : "§7活塞无法正常工作")
            .build();
        asyncButtonMap.put(8 + 9, pistonItem);
    
        ItemStack globalSetting = new ButtonItemBuilder(Material.CREEPER_HEAD)
            .name("§b§l全局设置")
            .lore(this.getPermission(dimension, domain.getGlobe()))
            .lore(this.getBuildPermission(dimension, domain.getGlobe()))
            .build();
        asyncButtonMap.put(32, globalSetting);
        ItemStack friendSetting = new ButtonItemBuilder(Material.SKELETON_SKULL)
            .name("§b§l好友设置")
            .lore(this.getPermission(dimension, domain.getFriend()))
            .lore(this.getBuildPermission(dimension, domain.getFriend()))
            .build();
        asyncButtonMap.put(33, friendSetting);
        ItemStack intimateSetting = new ButtonItemBuilder(Material.WITHER_SKELETON_SKULL)
            .name("§b§l密友设置")
            .lore(this.getPermission(dimension, domain.getIntimate()))
            .lore(this.getBuildPermission(dimension, domain.getIntimate()))
            .build();
        asyncButtonMap.put(34, intimateSetting);
        ItemStack playerSetting = new ButtonItemBuilder(Material.PLAYER_HEAD)
            .name("§b§l玩家设置")
            .lore("todo")// TODO: 特定玩家权限显示
            .build();
        asyncButtonMap.put(35, playerSetting);


        return asyncButtonMap;
    }

    @Override
    public void refreshPage() {
        ItemStack close = new ButtonItemBuilder(Material.REDSTONE)
            .actionBack()
            .name("§c关闭")
            .build();
        buttonMap.put(53, close);

        // 道路
        ItemStack road = new ButtonItemBuilder(Material.WHITE_STAINED_GLASS_PANE)
            .name(" ")
            .build();
        ItemStack plot = new ButtonItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
            .name(" ")
            .build();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                int index = i * 9 + j;
                if (i % 4 == 0 || j % 4 == 0) {
                    buttonMap.put(index, road);
                } else {
                    buttonMap.put(index, plot);
                }
            }
        }

        for (int i = 41; i < 45; i++) {
            buttonMap.put(i, road);
        }

        ItemStack info = new ButtonItemBuilder(Material.OAK_SIGN)
            .actionPlayerCommand("")
            .name("§b§l地皮信息")
            .build();
        buttonMap.put(45, info);
        ItemStack tp = new ButtonItemBuilder(Material.ENDER_PEARL)
            .actionPlayerCommand("plot tp " + target)
            .name("§b§l传送")
            .lore("§7传送到地皮")
            .build();
        buttonMap.put(46, tp);
        ItemStack center = new ButtonItemBuilder(Material.WHITE_BANNER)
            .actionPlayerCommand("plot tpset")
            .name("§b§l设置点")
            .lore("§7将脚下的点作为传送点")
            .build();
        buttonMap.put(47, center);
        ItemStack map = new ButtonItemBuilder(Material.SHEARS)
            .actionPlayerCommand("plot abandon")
            .name("§c§l割弃")
            .lore("§7放弃脚下部分地皮并返还扩张费用")
            .build();
        buttonMap.put(48, map);
        ItemStack remove = new ButtonItemBuilder(Material.RED_DYE)
            .actionPlayerCommand("plot remove " + target)
            .name("§c§l删除")
            .lore("§7放弃对整个地皮的占领")
            .build();
        buttonMap.put(49, remove);
    }
    
    /**
     * 获取权限信息文本
     * @param dimension 维度
     * @param setting 设置
     * @return 权限信息
     */
    private String getPermission(Dimension dimension, long setting) {
        StringBuilder builder = new StringBuilder();
        builder.append((dimension.isPermit(setting, Dimension.PERMISSION_USE) ? "§a" : "§c§m") + "use§r ");
        builder.append((dimension.isPermit(setting, Dimension.PERMISSION_PVE) ? "§a" : "§c§m") + "pve§r ");
        builder.append((dimension.isPermit(setting, Dimension.PERMISSION_OPEN) ? "§a" : "§c§m") + "open§r ");
        builder.append((dimension.isPermit(setting, Dimension.PERMISSION_TP) ? "§a" : "§c§m") + "tp§r ");
        builder.append((dimension.isPermit(setting, Dimension.PERMISSION_ARMOR) ? "§a" : "§c§m") + "armor§r ");
        return builder.toString();
    }

/**
     * 获取建筑权限信息文本
     * @param dimension 维度
     * @param setting 设置
     * @return 权限信息
     */
    private String getBuildPermission(Dimension dimension, long setting) {
        StringBuilder builder = new StringBuilder();
        builder.append((dimension.isPermit(setting, Dimension.PERMISSION_DESTROY) ? "§a" : "§c§m") + "destroy§r ");
        builder.append((dimension.isPermit(setting, Dimension.PERMISSION_PLACE) ? "§a" : "§c§m") + "place§r ");
        builder.append((dimension.isPermit(setting, Dimension.PERMISSION_IGNITE) ? "§a" : "§c§m") + "ignite§r ");
        return builder.toString();
    }

}
