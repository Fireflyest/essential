package org.fireflyest.essential.world;

import java.util.EnumMap;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.scheduler.BukkitRunnable;
import org.fireflyest.essential.Essential;

public class WorldCleaner {
    
    private final World world;

    public WorldCleaner(World world) {
        this.world = world;
    }

    /**
     * 清理
     */
    public void clean() {
        if (world == null) {
            return;
        }
        for (Chunk chunk : world.getLoadedChunks()) {
            if (chunk.isForceLoaded()) {
                this.cleanChunk(chunk);
                continue;
            }
            if (chunk.getInhabitedTime() > 0) {
                chunk.setInhabitedTime(0);
                this.cleanChunk(chunk);
            } else {
                this.cleanChunk(chunk);
                chunk.unload();
            }
        }
    }

    /**
     * 清理区块
     * @param chunk 区块
     */
    private void cleanChunk(Chunk chunk) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Entity[] entities = chunk.getEntities();
                Map<EntityType, Integer> entityNumMap = new EnumMap<>(EntityType.class);
                int entityNum = entities.length;
                int maxAnimal;
                // 区块实体越多 动物限制约严格
                switch (entityNum / 100) {
                    case 0:
                        maxAnimal = 30;
                        break;
                    case 1:
                        maxAnimal = 25;
                        break;
                    case 2:
                        maxAnimal = 20;
                        break;
                    case 3:
                        maxAnimal = 15;
                        break;
                    case 4:
                        maxAnimal = 10;
                        break;
                    case 5:
                        maxAnimal = 5;
                        break;
                    default:
                        maxAnimal = 3;
                        break;
                }
                // 便利所有生物
                for (Entity entity : entities) {
                    if (entity instanceof Arrow 
                            || entity instanceof Item 
                            || entity instanceof Bat
                            || (entity instanceof Monster && entity.getCustomName() == null)) {
                        // 清理掉落物 箭头 蝙蝠 未命名怪物
                        entity.remove();
                    } else if (entity instanceof Animals && entity.getCustomName() == null) {
                        // 对动物数量进行限制
                        entityNumMap.putIfAbsent(entity.getType(), 0);
                        int num = entityNumMap.get(entity.getType()) + 1;
                        entityNumMap.put(entity.getType(), num);
                        if (num > maxAnimal) {
                            entity.remove();
                        }
                    }
                }
            }
        }.runTaskLater(Essential.getPlugin(), 20 * 15L);
    }

}
