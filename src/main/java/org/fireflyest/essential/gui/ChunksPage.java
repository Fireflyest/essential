package org.fireflyest.essential.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.NumberConversions;
import org.fireflyest.craftgui.api.ViewPage;
import org.fireflyest.craftgui.button.ButtonItemBuilder;
import org.fireflyest.craftgui.view.TemplatePage;

public class ChunksPage extends TemplatePage {

    private World world;
    private int centerX;
    private int centerZ;

    private Map<String,ChunkInfo> chunkEntityMap;

    public ChunksPage(String target, int size) {
        super("§9§l区块管理", target, -1, size);
        this.centerX = 0;
        this.centerZ = 0;
    }

    @Override
    public @Nonnull Map<Integer, ItemStack> getItemMap() {
        asyncButtonMap.clear();
        asyncButtonMap.putAll(buttonMap);

        int pos = 1;
        for (int i = -2; i < 4; i++) {
            for (int j = -3; j < 4; j++) {
                if ((pos+1) % 9 == 0) {
                    pos += 2;
                }
                int x = centerX + j;
                int z = centerZ + i;
                String name = "§r[" + x + ":" + z + "]";
                ChunkInfo chunkInfo = chunkEntityMap.get(name);

                ItemStack item = null;
                if (chunkInfo != null) {
                    Material material;
                    if (chunkInfo.entities.isEmpty()){
                        material = Material.WHITE_STAINED_GLASS_PANE;
                    } else if (chunkInfo.entities.size() < 30){
                        material = Material.LIME_STAINED_GLASS_PANE;
                    } else if (chunkInfo.entities.size() < 80) {
                        material = Material.YELLOW_STAINED_GLASS_PANE;
                    } else if (chunkInfo.entities.size() < 120) {
                        material = Material.PINK_STAINED_GLASS_PANE;
                    } else {
                        material = Material.RED_STAINED_GLASS_PANE;
                    }
                    item = new ButtonItemBuilder(material)
                            .actionPlayerCommand("tp " + (x * 16) + " 80 " + (z * 16))
                            .name(name)
                            .lore("§3占据§7: " + chunkInfo.inhabited / (20 * 60))
                            .build();
                    for (Entity entity : chunkInfo.entities) {
                        this.addEntity(item, entity);
                    }
                } else {
                    item = new ButtonItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                            .actionPlayerCommand("tp " + (x * 16) + " 80 " + (z * 16))
                            .name(name)
                            .build();
                }

                asyncButtonMap.put(pos++, item);
            }
        }
        return asyncButtonMap;
    }

    @Override
    public ViewPage getNext() {
        return null;
    }

    @Override
    public @Nullable ItemStack getItem(int slot) {
        switch (slot) {
            case 0:
                centerX -= 1;
                centerZ -=1;
                break;
            case 8:
                centerX += 1;
                centerZ -=1;
                break;
            case 45:
                centerX -= 1;
                centerZ +=1;
                break;
            case 53:
                centerX += 1;
                centerZ +=1;
                break;
            case 35:
                centerX = 0;
                centerZ =0;
                break;
            case 26:
                this.refreshPage();
                break;
            default:
                break;
        }
        return super.getItem(slot);
    }

    @Override
    public void refreshPage() {
        ItemStack lu = new ButtonItemBuilder(Material.ENDER_PEARL)
                .actionEdit()
                .name("§r↖")
                .build();
        ItemStack ru = new ButtonItemBuilder(Material.ENDER_PEARL)
                .actionEdit()
                .name("§r↗")
                .build();
        ItemStack ld = new ButtonItemBuilder(Material.ENDER_PEARL)
                .actionEdit()
                .name("§r↙")
                .build();
        ItemStack rd = new ButtonItemBuilder(Material.ENDER_PEARL)
                .actionEdit()
                .name("§r↘")
                .build();
        buttonMap.put(0, lu);
        buttonMap.put(8, ru);
        buttonMap.put(45, ld);
        buttonMap.put(53, rd);

        ItemStack refresh = new ButtonItemBuilder(Material.STRING)
                .actionEdit()
                .name("§e刷新")
                .build();
        buttonMap.put(26, refresh);
        ItemStack center = new ButtonItemBuilder(Material.ENDER_EYE)
                .actionEdit()
                .name("§e原点")
                .build();
        buttonMap.put(35, center);
        ItemStack tp = new ButtonItemBuilder(Material.FIREWORK_ROCKET)
                .actionPlayerCommand("world " + target + " tp")
                .name("§e传送")
                .build();
        buttonMap.put(18, tp);
        ItemStack back = new ButtonItemBuilder(Material.REDSTONE)
                .actionBack()
                .name("§c返回")
                .build();
        buttonMap.put(27, back);

        // 记录生物
        this.world = Bukkit.getWorld(target);
        this.chunkEntityMap = new HashMap<>();
        for (Chunk chunk : world.getLoadedChunks()) {
            String name = "§r[" + chunk.getX() + ":" + chunk.getZ() + "]";

            // ChunkSnapshot chunkSnapshot = chunk.getChunkSnapshot();

            ChunkInfo chunkInfo = new ChunkInfo();
            chunkInfo.inhabited = chunk.getInhabitedTime();
            chunkInfo.entities.addAll(Arrays.asList(chunk.getEntities()));



            chunkEntityMap.put(name, chunkInfo);
        }
    }

    private void addEntity(ItemStack item, Entity entity){
        ItemMeta meta = item.getItemMeta();
        List<String> lores = null;
        EntityType entityType = entity.getType();
        String entityStr;
        if (entityType.equals(EntityType.PLAYER)){
            entityStr = String.format("§7 • §b%-14s", entity.getName());
        }else {
            entityStr = String.format("§7 • §f%-14s", entityType.name());
        }
        if (meta != null) lores = meta.getLore();
        if (lores == null) lores = new ArrayList<>();
        int size = lores.size();
        if (size == 0) {
            lores.add(entityStr);
        }else {
            int line = size - 1;
            String lastLine = lores.get(line);
            if (lastLine.contains(entityStr)) {
                if (lastLine.contains("x")){
                    String[] lastLineValue = lastLine.split("x");
                    lastLine = lastLineValue[0] + "x" + (NumberConversions.toInt(lastLineValue[1]) +1);
                }else {
                    lastLine += "x2";
                }
                lores.set(line, lastLine);
            } else {
                lores.add(entityStr);
            }
        }

        if (meta != null) {
            meta.setLore(lores);
            item.setItemMeta(meta);
        }
    }

    class ChunkInfo {
        public long inhabited;
        public List<Entity> entities = new ArrayList<>(); 
    }

}
