package org.fireflyest.essential.listener;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameRule;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.data.Config;
import org.fireflyest.essential.data.EssentialYaml;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.data.StateCache;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.essential.world.Dimension;
import org.fireflyest.essential.world.Dimension.EventResult;

public class WorldEventListener implements Listener {

    private Map<String, Dimension> worldMap;

    /**
     * 世界事件监听
     * @param yaml 数据
     * @param service 数据
     */
    public WorldEventListener(EssentialYaml yaml, EssentialService service, StateCache cache, Map<String, Dimension> worldMap) {
        this.worldMap = worldMap;

        worldMap.clear();
        List<String> files = Arrays.asList(Bukkit.getWorldContainer().list());
        
        // 加载世界数据
        for (String key : yaml.getWorld().getKeys(false)) {
            World world = Bukkit.getWorld(key);
            if (world == null && files.contains(key)) {
                ChunkGenerator chunkGenerator = Essential.getPlugin().getDefaultWorldGenerator(key, key);
                WorldCreator creator = new WorldCreator(key)
                    .generator(chunkGenerator);
                world = creator.createWorld();
            }
            if (world == null) {
                continue;
            }
            // 数据存储
            Dimension dimension = new Dimension(key,
                    yaml.getWorld().getString(String.format("%s.title", key)),
                    yaml.getWorld().getBoolean(String.format("%s.protect", key)),
                    yaml.getWorld().getBoolean(String.format("%s.pvp", key)),
                    yaml.getWorld().getBoolean(String.format("%s.explode", key)),
                    service, cache);
            worldMap.put(key, dimension);
            // 边界
            int border = yaml.getWorld().getInt(String.format("%s.border", key));
            world.getWorldBorder().setSize(border);

            // 死亡保护
            if (Config.KEEP_INVENTORY) {
                world.setGameRule(GameRule.KEEP_INVENTORY, true);
            }

            // 地皮世界生物生成限制
            if (world.getName().equals(Config.PLOT_WORLD)) {
                world.setSpawnLimit(SpawnCategory.MONSTER, 10);
                world.setTicksPerSpawns(SpawnCategory.MONSTER, 400);
                world.setSpawnLimit(SpawnCategory.ANIMAL, 10);
                world.setTicksPerSpawns(SpawnCategory.ANIMAL, 600);
                world.setSpawnLimit(SpawnCategory.WATER_ANIMAL, 10);
                world.setTicksPerSpawns(SpawnCategory.WATER_ANIMAL, 400);
                world.setSpawnLimit(SpawnCategory.WATER_AMBIENT, 10);
                world.setTicksPerSpawns(SpawnCategory.WATER_AMBIENT, 400);
                world.setSpawnLimit(SpawnCategory.WATER_UNDERGROUND_CREATURE, 10);
                world.setTicksPerSpawns(SpawnCategory.WATER_UNDERGROUND_CREATURE, 600);
                world.setSpawnLimit(SpawnCategory.AMBIENT, 10);
                world.setTicksPerSpawns(SpawnCategory.AMBIENT, 600);
                world.setSpawnLimit(SpawnCategory.AXOLOTL, 10);
                world.setTicksPerSpawns(SpawnCategory.AXOLOTL, 600);
            }
        }

        // 结构体
        this.loadStructure();
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
            return;
        }

        if (event.getCause() == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) {
            Player player = event.getPlayer();
            if (player == null || player.hasPermission("essential.build")) {
                return;
            }
            String worldName = event.getBlock().getWorld().getName();
            Dimension dimension = worldMap.get(worldName);
            String uid = player.getUniqueId().toString();
            if (dimension != null) {
                String loc = this.getLoc(event.getBlock().getChunk());
                Dimension.EventResult result = dimension.triggerPermit(loc, uid, Dimension.PERMISSION_IGNITE, !dimension.isProtect());
                if  (!result.isAllow()) {
                    event.setCancelled(true);
                    switch (result.getType()) {
                        case EventResult.IN_DOMAIN:
                        case EventResult.IN_LEAGUE:
                        case EventResult.SERVER_PROTECT:
                            this.sendPermitMessage(player, result.getDomain(), "ignite");
                            break;
                        case EventResult.WORLD_PROTECT:
                            player.sendMessage(Language.PLOT_PROTECT_WORLD);
                            break;
                        case EventResult.IN_ROAD:
                        case EventResult.IN_SHARE_ROAD:
                            player.sendMessage(Language.PLOT_PROTECT_ROAD);
                            break;
                        default:
                            break;
                    }
                }
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
     * 融化
     * @param event 事件
     */
    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        String worldName = event.getBlock().getWorld().getName();
        Dimension dimension = worldMap.get(worldName);
        if (dimension != null && dimension.isProtect()) {
            event.setCancelled(true);
        }
    }

    /**
     * 凋落
     * @param event 事件
     */
    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        String worldName = event.getBlock().getWorld().getName();
        Dimension dimension = worldMap.get(worldName);
        if (dimension != null && dimension.isProtect()) {
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
        if (type.isAlive() && (type == EntityType.TRADER_LLAMA || type == EntityType.WANDERING_TRADER)) {
            event.getEntity().remove();
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
     * 爆炸破坏
     * @param event 事件
     */
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        String worldName = event.getBlock().getWorld().getName();
        Dimension dimension = worldMap.get(worldName);
        if (dimension != null) {
            Map<String, Boolean> protectLoc = new HashMap<>();
            Iterator<Block> iterator = event.blockList().iterator();
            while (iterator.hasNext()) {
                Block block = iterator.next();
                String loc = this.getLoc(block.getLocation().getChunk());
                protectLoc.computeIfAbsent(loc, k -> {
                    Dimension.EventResult result = dimension.triggerFlag(loc, Dimension.FLAG_EXPLODE, dimension.isExplode());
                    return result.isAllow();
                });
                if (protectLoc.containsKey(loc) && Boolean.FALSE.equals(protectLoc.get(loc))) {
                    iterator.remove();
                }
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
            Map<String, Boolean> protectLoc = new HashMap<>();
            Iterator<Block> iterator = event.blockList().iterator();
            while (iterator.hasNext()) {
                Block block = iterator.next();
                String loc = this.getLoc(block.getLocation().getChunk());
                protectLoc.computeIfAbsent(loc, k -> {
                    Dimension.EventResult result = dimension.triggerFlag(loc, Dimension.FLAG_EXPLODE, dimension.isExplode());
                    return result.isAllow();
                });
                if (protectLoc.containsKey(loc) && Boolean.FALSE.equals(protectLoc.get(loc))) {
                    iterator.remove();
                }
            }
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        String worldName = event.getBlock().getWorld().getName();
        Dimension dimension = worldMap.get(worldName);
        if (dimension != null) {
            String loc = this.getLoc(event.getBlock().getChunk());
            Dimension.EventResult result;
            switch (event.getBlock().getType()) {
                case LAVA:
                    result = dimension.triggerFlag(loc, Dimension.FLAG_FLOW_LAVA);
                    break;
                case WATER:
                    result = dimension.triggerFlag(loc, Dimension.FLAG_FLOW_WATER);
                    break;
                default:
                    result = dimension.triggerFlag(loc, Dimension.PERMISSION_DESTROY);
                    break;
            }
            if  (!result.isAllow()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        String worldName = event.getBlock().getWorld().getName();
        Dimension dimension = worldMap.get(worldName);
        if (dimension != null) {
            String loc = this.getLoc(event.getBlock().getChunk());
            Dimension.EventResult result = dimension.triggerFlag(loc, Dimension.FLAG_PISTON);
            if  (!result.isAllow()) {
                event.setCancelled(true);
            }
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
        if (player.hasPermission("essential.build")) {
            return;
        }
        String uid = player.getUniqueId().toString();
        if (dimension != null) {
            String loc = this.getLoc(event.getBlock().getChunk());
            Dimension.EventResult result = dimension.triggerPermit(loc, uid, Dimension.PERMISSION_ARMOR, !dimension.isProtect());
            if  (!result.isAllow()) {
                event.setCancelled(true);
                switch (result.getType()) {
                    case EventResult.IN_DOMAIN:
                    case EventResult.IN_LEAGUE:
                    case EventResult.SERVER_PROTECT:
                        this.sendPermitMessage(player, result.getDomain(), "armor");
                        break;
                    case EventResult.WORLD_PROTECT:
                        player.sendMessage(Language.PLOT_PROTECT_WORLD);
                        break;
                    case EventResult.IN_ROAD:
                    case EventResult.IN_SHARE_ROAD:
                        player.sendMessage(Language.PLOT_PROTECT_ROAD);
                        break;
                    default:
                        break;
                }
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
        if (player.hasPermission("essential.build")) {
            return;
        }
        String uid = player.getUniqueId().toString();
        if (dimension != null) {
            String loc = this.getLoc(player.getLocation().getChunk());
            Dimension.EventResult result = dimension.triggerPermit(loc, uid, Dimension.PERMISSION_ARMOR, !dimension.isProtect());
            if  (!result.isAllow()) {
                event.setCancelled(true);
                switch (result.getType()) {
                    case EventResult.IN_DOMAIN:
                    case EventResult.IN_LEAGUE:
                    case EventResult.SERVER_PROTECT:
                        this.sendPermitMessage(player, result.getDomain(), "armor");
                        break;
                    case EventResult.WORLD_PROTECT:
                        player.sendMessage(Language.PLOT_PROTECT_WORLD);
                        break;
                    case EventResult.IN_ROAD:
                    case EventResult.IN_SHARE_ROAD:
                        player.sendMessage(Language.PLOT_PROTECT_ROAD);
                        break;
                    default:
                        break;
                }
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
        if (player.hasPermission("essential.build")) {
            return;
        }
        String uid = player.getUniqueId().toString();
        if (dimension != null) {
            String loc = this.getLoc(event.getBlock().getChunk());
            Dimension.EventResult result = dimension.triggerPermit(loc, uid, Dimension.PERMISSION_BUCKET, !dimension.isProtect());
            if  (!result.isAllow()) {
                event.setCancelled(true);
                switch (result.getType()) {
                    case EventResult.IN_DOMAIN:
                    case EventResult.IN_LEAGUE:
                    case EventResult.SERVER_PROTECT:
                        this.sendPermitMessage(player, result.getDomain(), "bucket");
                        break;
                    case EventResult.WORLD_PROTECT:
                        player.sendMessage(Language.PLOT_PROTECT_WORLD);
                        break;
                    case EventResult.IN_ROAD:
                    case EventResult.IN_SHARE_ROAD:
                        player.sendMessage(Language.PLOT_PROTECT_ROAD);
                        break;
                    default:
                        break;
                }
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
        if (player.hasPermission("essential.build")) {
            return;
        }
        String uid = player.getUniqueId().toString();
        if (dimension != null) {
            String loc = this.getLoc(event.getBlock().getChunk());
            Dimension.EventResult result = dimension.triggerPermit(loc, uid, Dimension.PERMISSION_BUCKET, !dimension.isProtect());
            if  (!result.isAllow()) {
                event.setCancelled(true);
                switch (result.getType()) {
                    case EventResult.IN_DOMAIN:
                    case EventResult.IN_LEAGUE:
                    case EventResult.SERVER_PROTECT:
                        this.sendPermitMessage(player, result.getDomain(), "bucket");
                        break;
                    case EventResult.WORLD_PROTECT:
                        player.sendMessage(Language.PLOT_PROTECT_WORLD);
                        break;
                    case EventResult.IN_ROAD:
                    case EventResult.IN_SHARE_ROAD:
                        player.sendMessage(Language.PLOT_PROTECT_ROAD);
                        break;
                    default:
                        break;
                }
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
        if (player.hasPermission("essential.build")) {
            return;
        }
        String uid = player.getUniqueId().toString();
        if (dimension != null) {
            String loc = this.getLoc(event.getBlock().getChunk());
            Dimension.EventResult result = dimension.triggerPermit(loc, uid, Dimension.PERMISSION_PLACE, !dimension.isProtect());
            if  (!result.isAllow()) {
                event.setCancelled(true);
                switch (result.getType()) {
                    case EventResult.IN_DOMAIN:
                    case EventResult.IN_LEAGUE:
                    case EventResult.SERVER_PROTECT:
                        this.sendPermitMessage(player, result.getDomain(), "place");
                        break;
                    case EventResult.WORLD_PROTECT:
                        player.sendMessage(Language.PLOT_PROTECT_WORLD);
                        break;
                    case EventResult.IN_ROAD:
                    case EventResult.IN_SHARE_ROAD:
                        player.sendMessage(Language.PLOT_PROTECT_ROAD);
                        break;
                    default:
                        break;
                }
            }
        }
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
        if (player.hasPermission("essential.build")) {
            return;
        }
        String uid = player.getUniqueId().toString();
        if (dimension != null) {
            String loc = this.getLoc(event.getBlock().getChunk());
            Dimension.EventResult result = dimension.triggerPermit(loc, uid, Dimension.PERMISSION_DESTROY, !dimension.isProtect());
            if  (!result.isAllow()) {
                event.setCancelled(true);
                switch (result.getType()) {
                    case EventResult.IN_DOMAIN:
                    case EventResult.IN_LEAGUE:
                    case EventResult.SERVER_PROTECT:
                        this.sendPermitMessage(player, result.getDomain(), "destroy");
                        break;
                    case EventResult.WORLD_PROTECT:
                        player.sendMessage(Language.PLOT_PROTECT_WORLD);
                        break;
                    case EventResult.IN_ROAD:
                    case EventResult.IN_SHARE_ROAD:
                        player.sendMessage(Language.PLOT_PROTECT_ROAD);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("essential.admin")) {
            return;
        }
        String worldName = player.getWorld().getName();
        Dimension dimension = worldMap.get(worldName);
        String uid = player.getUniqueId().toString();
        if (dimension != null) {
            String loc = this.getLoc(player.getLocation().getChunk());
            Dimension.EventResult result = dimension.triggerPermit(loc, uid, Dimension.PERMISSION_TP);
            if  (!result.isAllow()) {
                event.setCancelled(true);
                this.sendPermitMessage(player, result.getDomain(), "tp");
            }
        }
    }

    /**
     * 生物生成
     * @param event 事件
     */
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        // TODO: Bukkit.selectEntities

        // Chunk chunk = event.getEntity().getLocation().getChunk();
        // int amount = chunk.getEntities().length;
        
        // if (amount > 1000) {
        //     String msg = String.format("区块实体超载 §3%s§7[§3%s, %s§7]", chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
        //     Essential.getPlugin().getLogger().warning(msg);
        //     event.setCancelled(true);
        // }

        if (event.getSpawnReason().equals(SpawnReason.NATURAL)) {
            String worldName = event.getLocation().getWorld().getName();
            Dimension dimension = worldMap.get(worldName);
            if (dimension != null) {
                String loc = this.getLoc(event.getLocation().getChunk());
                Dimension.EventResult result = dimension.triggerFlag(loc, Dimension.FLAG_MONSTER);
                if  (!result.isAllow()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * 伤害事件
     * @param event 事件
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity target = event.getEntity();
        Entity damager = event.getDamager();

        Player targetPlayer = null;
        Player damagerPlayer = null;
        // 目标是否玩家
        if (target instanceof Player) {
            // 战斗时不允许飞行
            targetPlayer = ((Player)target);
            targetPlayer.setFlying(false);
        }

        // 判断攻击方是否玩家
        if (event.getCause() == DamageCause.ENTITY_ATTACK) {
            if (!(damager instanceof Player)) {
                return;
            }
            damagerPlayer = ((Player)damager);
        } else if (event.getCause() == DamageCause.PROJECTILE) {
            Projectile projectile = ((Projectile)event.getDamager());
            if (!(projectile.getShooter() instanceof Player)) {
                return;
            }
            damagerPlayer = ((Player)projectile.getShooter());
        }
        // 攻击方不是玩家则不管
        if (damagerPlayer == null) {
            return;
        }
        damagerPlayer.setFlying(false);
        // 是否PVP
        boolean pvp = targetPlayer != null;
        
        String worldName = event.getEntity().getWorld().getName();
        Dimension dimension = worldMap.get(worldName);
        if (dimension != null) {
            String loc = this.getLoc(event.getEntity().getLocation().getChunk());
            Dimension.EventResult result = 
                pvp ? dimension.triggerFlag(loc, Dimension.FLAG_PVP, dimension.isPvp()) : dimension.triggerPermit(loc, damagerPlayer.getUniqueId().toString(), Dimension.PERMISSION_PVE);
            if  (!result.isAllow()) {
                event.setCancelled(true);
                switch (result.getType()) {
                    case EventResult.IN_DOMAIN:
                    case EventResult.IN_LEAGUE:
                    case EventResult.SERVER_PROTECT:
                        if (pvp) {
                            damagerPlayer.sendMessage(Language.PLOT_PVP);
                        } else {
                            this.sendPermitMessage(damagerPlayer, result.getDomain(), "pve");
                        }
                        break;
                    case EventResult.WORLD_PROTECT:
                        damagerPlayer.sendMessage(Language.PLOT_PROTECT_WORLD);
                        break;
                    case EventResult.IN_ROAD:
                    case EventResult.IN_SHARE_ROAD:
                        damagerPlayer.sendMessage(Language.PLOT_PROTECT_ROAD);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasBlock()) {
            return;
        }
        Block block = event.getClickedBlock();
        
        // 方块是否可交互
        if (!block.getType().isInteractable()) {
            return;
        }
        // 是否容器
        boolean isContainer = block.getState() instanceof Container;

        Player player = event.getPlayer();
        if (player.hasPermission("essential.admin")) {
            return;
        }
        String worldName = player.getWorld().getName();
        Dimension dimension = worldMap.get(worldName);
        String uid = player.getUniqueId().toString();
        if (dimension != null) {
            String loc = this.getLoc(player.getLocation().getChunk());
            Dimension.EventResult result =  isContainer ? dimension.triggerPermit(loc, uid, Dimension.PERMISSION_OPEN) : dimension.triggerPermit(loc, uid, Dimension.PERMISSION_USE);
            if  (!result.isAllow()) {
                event.setCancelled(true);
                this.sendPermitMessage(player, result.getDomain(), isContainer ? "open" : "use");
            }
        }
    }


    @EventHandler
    public void onChunkPopulate(ChunkPopulateEvent event) {
        // Chunk chunk = event.getChunk();
        // World world = chunk.getWorld();
        // if (!world.getName().equals(Config.PLOT_WORLD)) {
        //     return;
        // }
        // ChunkGenerator generator = Essential.getPlugin().getDefaultWorldGenerator(world.getName(), "plot");
        // ChunkData chunkData = Bukkit.createChunkData(world);
        // for (int i = 0; i < 16; i++) {
        //     for (int j = 0; j < 16; j++) {
        //         for (int k = 3; k < 59; k++) {
        //             chunkData.setBlock(i, j, k, Material.DIRT);
        //         }
        //     }
        // }
        // generator.generateBedrock(world, new Random(world.getSeed()), chunk.getX(), chunk.getZ(), chunkData);
    }

    
    /**
     * 区块转化为键
     * @param chunk 区块
     * @return 键
     */
    private String getLoc(Chunk chunk) {
        return chunk.getX() + ":" + chunk.getZ();
    }

    /**
     * 权限提示
     * @param player 玩家
     * @param domain 领地
     * @param permission 权限
     */
    private void sendPermitMessage(Player player, String domain, String permission) {
        player.sendMessage(Language.PLOT_FLAG
            .replace("%domain%", domain)
            .replace("%flag%", permission));
    }

    /**
     * 加载结构体
     */
    private void loadStructure() {
        if (Config.STRUCTURE_ENABLE) {
            StructureManager manager = Bukkit.getServer().getStructureManager();
            File folder = new File(Essential.getPlugin().getDataFolder(), "structures");
                folder.mkdirs();
                for (String structName : folder.list()) {
                    if (!structName.endsWith(".struct")) {
                        continue;
                    }
                    File structFile = new File(folder, structName);
                    try {
                        Structure loadStructure = manager.loadStructure(structFile);
                        NamespacedKey key = NamespacedKey.fromString(structName.replace(".struct", ""));
                        manager.registerStructure(key, loadStructure);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            String info = Language.STRUCTURE_LOAD.replace("%num%", String.valueOf(manager.getStructures().size()));
            Essential.getPlugin().getLogger().info(info);
        }
    }

}
