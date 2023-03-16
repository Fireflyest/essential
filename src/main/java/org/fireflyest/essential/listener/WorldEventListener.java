package org.fireflyest.essential.listener;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.bean.Dimension;
import org.fireflyest.essential.data.Config;
import org.fireflyest.essential.data.EssentialYaml;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.service.EssentialService;

public class WorldEventListener implements Listener {

    private HashMap<String, Dimension> worldMap = new HashMap<>();

    /**
     * 世界事件监听
     * @param yaml 数据
     * @param service 数据
     */
    public WorldEventListener(EssentialYaml yaml, EssentialService service) {
        // 加载主城
        WorldCreator creator = new WorldCreator(Config.MAIN_WORLD);
        creator.createWorld();

        // 加载世界数据
        for (String key : yaml.getWorld().getKeys(false)) {
            World world = Bukkit.getWorld(key);
            if (world == null) {
                continue;
            }
            // 数据存储
            Dimension dimension = new Dimension(key,
                    yaml.getWorld().getString(String.format("%s.title", key)),
                    yaml.getWorld().getBoolean(String.format("%s.protect", key)),
                    yaml.getWorld().getBoolean(String.format("%s.pvp", key)),
                    yaml.getWorld().getBoolean(String.format("%s.explode", key)),
                    service);
            worldMap.put(key, dimension);
            // 边界
            int border = yaml.getWorld().getInt(String.format("%s.border", key));
            world.getWorldBorder().setSize(border);
        }
    }

    /**
     * 火焰传播
     * @param event 事件
     */
    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        // 防止火传播和火箭点然
        if ((event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD && !Config.IGNITE_SPREAD)
                || (event.getCause() == BlockIgniteEvent.IgniteCause.ARROW && !Config.IGNITE_ARROW)) {
            event.setCancelled(true);
        }
    }

    
    /**
     * 爆炸破坏
     * @param event 事件
     */
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        String worldName = event.getBlock().getWorld().getName();
        Dimension dimension = worldMap.get(worldName);
        if (dimension != null) {
            String loc = this.getLoc(event.getBlock().getChunk());
            Dimension.EventResult result = dimension.explode(loc);
            if  (result.cancel) {
                event.blockList().clear();
            }
        }
    }

    /**
     * 红石监听
     * @param event 事件
     */
    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent event) {
        // 
    }

    /**
     * 方块破坏
     * @param event 事件
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        String worldName = event.getBlock().getWorld().getName();
        Dimension dimension = worldMap.get(worldName);
        Player player = event.getPlayer();
        String uid = player.getUniqueId().toString();
        if (dimension != null) {
            String loc = this.getLoc(event.getBlock().getChunk());
            Dimension.EventResult result = dimension.destroy(loc, uid);
            if  (result.cancel) {
                event.setCancelled(true);
                // TODO: 
                player.sendMessage(Language.TITLE + "这个世界已被保护");
            }
        }
    }

    /**
     * 烧毁
     * @param event 事件
     */
    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        String worldName = event.getBlock().getWorld().getName();
        Dimension dimension = worldMap.get(worldName);
        if (dimension != null && dimension.isProtect()) {
            event.setCancelled(true);
        }
    }

    /**
     * 框甲架交互
     * @param event 事件
     */
    @EventHandler
    public void onBlockDispenseArmor(BlockDispenseArmorEvent event) {
        String worldName = event.getBlock().getWorld().getName();
        Dimension dimension = worldMap.get(worldName);
        Player player = ((Player) event.getTargetEntity());
        String uid = player.getUniqueId().toString();
        if (dimension != null) {
            String loc = this.getLoc(event.getBlock().getChunk());
            Dimension.EventResult result = dimension.use(loc, uid);
            if  (result.cancel) {
                event.setCancelled(true);
                // TODO: 
                player.sendMessage(Language.TITLE + "你不能给框架物品");
            }
        }
    }

    /**
     * 框甲架交互
     * @param event 事件
     */
    @EventHandler
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        String worldName = event.getPlayer().getWorld().getName();
        Dimension dimension = worldMap.get(worldName);
        Player player = event.getPlayer();
        String uid = player.getUniqueId().toString();
        if (dimension != null) {
            String loc = this.getLoc(player.getLocation().getChunk());
            Dimension.EventResult result = dimension.use(loc, uid);
            if  (result.cancel) {
                event.setCancelled(true);
                // TODO: 
                player.sendMessage(Language.TITLE + "你不能和框架交互");
            }
        }
    }

    /**
     * 桶
     * @param event 事件
     */
    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        String worldName = event.getBlock().getWorld().getName();
        Dimension dimension = worldMap.get(worldName);
        Player player = event.getPlayer();
        String uid = player.getUniqueId().toString();
        if (dimension != null) {
            String loc = this.getLoc(event.getBlock().getChunk());
            Dimension.EventResult result = dimension.bucket(loc, uid);
            if  (result.cancel) {
                event.setCancelled(true);
                // TODO: 
                player.sendMessage(Language.TITLE + "你不能在这用桶");
            }
        }
    }

    /**
     * 桶
     * @param event 事件
     */
    @EventHandler
    public void onPlayerFillEmpty(PlayerBucketFillEvent event) {
        String worldName = event.getBlock().getWorld().getName();
        Dimension dimension = worldMap.get(worldName);
        Player player = event.getPlayer();
        String uid = player.getUniqueId().toString();
        if (dimension != null) {
            String loc = this.getLoc(event.getBlock().getChunk());
            Dimension.EventResult result = dimension.bucket(loc, uid);
            if  (result.cancel) {
                event.setCancelled(true);
                // TODO: 
                player.sendMessage(Language.TITLE + "你不能在这用桶");
            }
        }
    }

    /**
     * 放置
     * @param event 事件
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        String worldName = event.getBlock().getWorld().getName();
        Dimension dimension = worldMap.get(worldName);
        Player player = event.getPlayer();
        String uid = player.getUniqueId().toString();
        if (dimension != null) {
            String loc = this.getLoc(event.getBlock().getChunk());
            Dimension.EventResult result = dimension.build(loc, uid);
            if  (result.cancel) {
                event.setCancelled(true);
                // TODO: 
                player.sendMessage(Language.TITLE + "你不能放置物品！");
            }
        }
    }


    /**
     * 生物生成
     * @param event 事件
     */
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        Chunk chunk = event.getEntity().getLocation().getChunk();
        int amount = chunk.getEntities().length;
        
        if (amount > 1000) {
            String msg = String.format("区块实体超载 §3%s§7[§3%s, %s§7]", chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
            Essential.getPlugin().getLogger().warning(msg);
            event.setCancelled(true);
        }
    }

    /**
     * 防止利用传送门刷物品
     * @param event 事件
     */
    @EventHandler
    public void onEntityPortalEnter(EntityPortalEnterEvent event) {
        EntityType type = event.getEntityType();
        if (!type.isAlive()) {
            return;
        }
        if (type == EntityType.TRADER_LLAMA || type == EntityType.WANDERING_TRADER) {
            event.getEntity().remove();
        }
    }

    /**
     * pvp
     * @param event 事件
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        // 战斗时不允许飞行
        Player player = ((Player)event.getEntity());
        player.setFlying(false);
        // 判断是否pvp
        if (event.getCause() == DamageCause.ENTITY_ATTACK&& !(event.getDamager() instanceof Player)) {
            return;
        } else if (event.getCause() == DamageCause.PROJECTILE) {
            Projectile projectile = ((Projectile)event.getDamager());
            if (!(projectile.getShooter() instanceof Player)) {
                return;
            }
        }
        
        String worldName = event.getEntity().getWorld().getName();
        Dimension dimension = worldMap.get(worldName);
        if (dimension != null) {
            String loc = this.getLoc(event.getEntity().getLocation().getChunk());
            Dimension.EventResult result = dimension.pvp(loc);
            if  (result.cancel) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * 生物爆炸
     * @param event 事件
     */
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        String worldName = event.getEntity().getWorld().getName();
        Dimension dimension = worldMap.get(worldName);
        if (dimension != null) {
            String loc = this.getLoc(event.getEntity().getLocation().getChunk());
            Dimension.EventResult result = dimension.explode(loc);
            if  (result.cancel) {
                event.blockList().clear();
            }
        }
    }
    
    /**
     * 区块转化为键
     * @param chunk 区块
     * @return 键
     */
    private String getLoc(Chunk chunk) {
        return chunk.getX() + ":" + chunk.getZ();
    }

}
