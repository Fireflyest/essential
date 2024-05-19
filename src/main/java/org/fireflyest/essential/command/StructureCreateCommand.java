package org.fireflyest.essential.command;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;
import org.bukkit.util.RayTraceResult;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.craftparticle.Brush;
import org.fireflyest.craftparticle.DynamicLocation;
import org.fireflyest.craftparticle.ParticleTasks;
import org.fireflyest.craftparticle.stroke.CuboidStroke;
import org.fireflyest.craftparticle.stroke.Stroke;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.data.StateCache;
import org.fireflyest.util.SerializationUtil;

public class StructureCreateCommand extends SubCommand {

    private final StateCache cache;
    private final ParticleTasks particleTasks;
    private final Brush<Material> brush;

    private final Map<String, Stroke<?>> strokeMap = new HashMap<>();

    public StructureCreateCommand(StateCache cache, ParticleTasks particleTasks) {
        this.cache = cache;
        this.particleTasks = particleTasks;

        this.brush = new Brush<>(Particle.END_ROD);
    }

    @Override
    protected boolean execute(CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }

        String cornerKey1 = player.getName() + StateCache.STRUCTURE_CORNER_1;
        String cornerKey2= player.getName() + StateCache.STRUCTURE_CORNER_2;
        RayTraceResult result = player.rayTraceBlocks(100);
        Block hitBlock = result == null ? null : result.getHitBlock();
        Location point = hitBlock == null ? player.getLocation() : hitBlock.getLocation().add(.5, .5, .5);
        String pointString = String.format("%s,%s,%s", point.getX(), point.getY(), point.getZ());

        String corner1 = cache.get(cornerKey1);
        String corner2 = cache.get(cornerKey2);

        if (corner1 == null) {
            cache.setex(cornerKey1, 120, SerializationUtil.serialize(point));
            sender.sendMessage(Language.STRUCTURE_CREATE_FIRST.replace("%point%", pointString));
        } else if (corner2 == null) {
            cache.setex(cornerKey2, 120, SerializationUtil.serialize(point));
            sender.sendMessage(Language.STRUCTURE_CREATE_SECOND.replace("%point%", pointString));
            this.sizeDisplay(player.getName());
        } else {
            cache.del(cornerKey1);
            cache.del(cornerKey2);
            sender.sendMessage(Language.STRUCTURE_CREATE_TIP);
            if (strokeMap.containsKey(sender.getName())) {
                strokeMap.get(sender.getName()).stop();
            }
        }
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }

        String cornerKey1 = player.getName() + StateCache.STRUCTURE_CORNER_1;
        String cornerKey2= player.getName() + StateCache.STRUCTURE_CORNER_2;
        String corner1 = cache.get(cornerKey1);
        String corner2 = cache.get(cornerKey2);
        if (corner1 == null || corner2 == null) {
            sender.sendMessage(Language.STRUCTURE_CREATE_FAIL);
            return true;
        }
        Location point1 = SerializationUtil.deserialize(corner1, Location.class);
        Location point2 = SerializationUtil.deserialize(corner2, Location.class);
        StructureManager manager = Bukkit.getServer().getStructureManager();
        Structure structure = manager.createStructure();
        this.fixLocation(point1, point2);
        structure.fill(point1, point2, true);
        manager.registerStructure(NamespacedKey.fromString(arg1), structure);

        File folder = new File(Essential.getPlugin().getDataFolder(), "structures");
        File structureFile = new File(folder, arg1 + ".struct");
        try {
            boolean create = structureFile.createNewFile();
            if (create) {
                manager.saveStructure(structureFile, structure);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        cache.del(cornerKey1);
        cache.del(cornerKey2);
        
        sender.sendMessage(Language.STRUCTURE_CREATE_FINISH + "§3" + arg1);
        return true;
    }

    /**
     * 角落有方块圈不到，要修复偏差
     * @param loc1 角点1
     * @param loc2 角点2
     */
    private void fixLocation(@Nonnull Location loc1, @Nonnull Location loc2) {
        if (loc1.getY() > loc2.getY()) {
            loc1.add(0, 1, 0);
        } else {
            loc2.add(0, 1, 0);
        }
        if (loc1.getX() > loc2.getX()) {
            loc1.add(1, 0, 0);
        } else {
            loc2.add(1, 0, 0);
        }
        if (loc1.getZ() > loc2.getZ()) {
            loc1.add(0, 0, 1);
        } else {
            loc2.add(0, 0, 1);
        }
    }

    private void sizeDisplay(final String playerName) {
        // 如果已经存在，停止
        if (strokeMap.containsKey(playerName)) {
            strokeMap.get(playerName).stop();
        }
        String cornerKey1 = playerName + StateCache.STRUCTURE_CORNER_1;
        String cornerKey2= playerName + StateCache.STRUCTURE_CORNER_2;
        String corner1 = cache.get(cornerKey1);
        String corner2 = cache.get(cornerKey2);
        Location cornerLoc1 = SerializationUtil.deserialize(corner1, Location.class);
        Location cornerLoc2 = SerializationUtil.deserialize(corner2, Location.class);
        // 坐标是方块中心，要放边界去
        if (cornerLoc1.getY() > cornerLoc2.getY()) {
            cornerLoc1.add(0, .5, 0);
            cornerLoc2.add(0, -.5, 0);
        } else {
            cornerLoc1.add(0, -.5, 0);
            cornerLoc2.add(0, .5, 0);
        }
        if (cornerLoc1.getX() > cornerLoc2.getX()) {
            cornerLoc1.add(.5, 0, 0);
            cornerLoc2.add(-.5, 0, 0);
        } else {
            cornerLoc1.add(-.5, 0, 0);
            cornerLoc2.add(.5, 0, 0);
        }
        if (cornerLoc1.getZ() > cornerLoc2.getZ()) {
            cornerLoc1.add(0, 0, .5);
            cornerLoc2.add(0, 0, -.5);
        } else {
            cornerLoc1.add(0, 0, -.5);
            cornerLoc2.add(0, 0, .5);
        }

        CuboidStroke<Material> stroke = new CuboidStroke<>(brush, new DynamicLocation(cornerLoc1), 20, 30, new DynamicLocation(cornerLoc2));
        stroke.setConstant(true);
        strokeMap.put(playerName, stroke);
        particleTasks.putTasks(stroke);
    }
    
}
