package org.fireflyest.essential.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
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

    public ChunksPage(String target, int size) {
        super("§9§l区块管理", target, -1, size);
        this.world = Bukkit.getWorld(target);
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
                Chunk chunk = world.getChunkAt(x, z);

                ItemStack item = null;
                if (chunk.isLoaded() && chunk.getInhabitedTime() > 0) {
                    item = new ButtonItemBuilder(Material.WHITE_STAINED_GLASS_PANE)
                            .name(name)
                            .build();
                    // for (Entity entity : chunk.getEntities()) {
                    //     this.addEntity(item, entity);
                    // }
                } else {
                    item = new ButtonItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                            .name(name)
                            .build();
                }

                asyncButtonMap.put(pos++, item);
            }
        }

        // // 判断是否有区块
        // if (chunks == null || chunks.length == 0) return crashMap;

        // int pos = 0;
        // for (int i = (page-1) * 54; i < page * 54; i++) {
        //     if (chunks.length <= i) break;
        //     Chunk chunk = chunks[i];
        //     String material, color;
        //     ViewItemBuilder chunkButtonBuilder;
        //     ItemStack chunkButton;
        //     if (chunk.isLoaded()){
        //         int entityAmount = chunk.getEntities().length;
        //         StringBuilder state = new StringBuilder();
        //         for (int j = 0; j < entityAmount / 10; j++) state.append("▎");
        //         if (entityAmount == 0){
        //             color = "§f";
        //             material = "WHITE_STAINED_GLASS_PANE";
        //         } else if (entityAmount < 30){
        //             color = "§a";
        //             material = "LIME_STAINED_GLASS_PANE";
        //         } else if (entityAmount < 80) {
        //             color = "§e";
        //             material = "YELLOW_STAINED_GLASS_PANE";
        //         } else if (entityAmount < 120) {
        //             color = "§c";
        //             material = "PINK_STAINED_GLASS_PANE";
        //         } else {
        //             color = "§4";
        //             material = "RED_STAINED_GLASS_PANE";
        //         }
        //         // 信息
        //         chunkButtonBuilder = new ViewItemBuilder(material)
        //                 .name(String.format("§7[§3%s, %s§7]", chunk.getX(), chunk.getZ()))
        //                 .lore(String.format("§3载荷§7: §f%s%s", color, state));
        //         if (world.getEnvironment() == World.Environment.NORMAL){
        //             chunkButtonBuilder.lore(String.format("§3史莱姆§7: §f%s", chunk.isSlimeChunk()));
        //         }
        //         // 记录
        //         ChunkData chunkData = EssentialWorld.getChunkDataMap(
        //                 String.format("%s[%s, %s]", target, chunk.getX(), chunk.getZ()));
        //         if (chunkData != null) {
        //             chunkButtonBuilder.lore(String.format("§3闲置§7: §f%s", chunkData.getIdle()))
        //                     .lore(String.format("§3活跃§7: §f%s",  chunkData.getDelta() / (20*60*60F)));
        //         }
        //         // 展示实体
        //         if (entityAmount > 0) chunkButtonBuilder.lore("§3实体§7: §f");
        //         chunkButton = chunkButtonBuilder.build();
        //         for (Entity entity : chunk.getEntities()) {
        //             this.addEntity(chunkButton, entity);
        //         }
        //     }else {
        //         // 信息
        //         chunkButtonBuilder = new ViewItemBuilder("BLACK_STAINED_GLASS_PANE")
        //                 .name(String.format("§7[§3%s, %s§7]", chunk.getX(), chunk.getZ()))
        //                 .lore("§3状态§7: §f未加载");
        //         chunkButton = chunkButtonBuilder.build();
        //     }
        //     crashMap.put(pos++, chunkButton);

        //     if (pos > 44) break;
        // }
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
                centerX += 1;
                centerZ +=1;
                break;
            case 8:
                centerX -= 1;
                centerZ +=1;
                break;
            case 45:
                centerX += 1;
                centerZ -=1;
                break;
            case 53:
                centerX -= 1;
                centerZ -=1;
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
}
