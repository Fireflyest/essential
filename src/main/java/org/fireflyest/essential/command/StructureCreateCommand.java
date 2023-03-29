package org.fireflyest.essential.command;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;
import org.bukkit.util.RayTraceResult;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.util.ParticleUtils;

public class StructureCreateCommand extends SubCommand {

    private int step = 0;
    private Location[] corners = new Location[2];

    @Override
    protected boolean execute(CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }

        RayTraceResult result = null;
        Block hitBlock = null;
        String point;
        switch (step) {
            case 0: // 开始选择
                corners[0] = null;
                corners[1] = null;
                sender.sendMessage(Language.STRUCTURE_CREATE_TIP);
                step++;
                break;
            case 1: // 第一个点
                result = player.rayTraceBlocks(100);
                if (result != null) {
                    hitBlock = result.getHitBlock();
                    corners[0] = hitBlock.getLocation().add(.5, .5, .5);
                    step++;
                    point = String.format("[%s,%s,%s]", corners[0].getX(), corners[0].getY(), corners[0].getZ());
                    sender.sendMessage(Language.STRUCTURE_CREATE_FIRST.replace("%point%", point));
                } else {
                    sender.sendMessage(Language.STRUCTURE_CREATE_FAIL);
                }
                break;
            case 2: // 第二个点
                result = player.rayTraceBlocks(100);
                if (result != null) {
                    hitBlock = result.getHitBlock();
                    corners[1] = hitBlock.getLocation().add(.5, .5, .5);
                    step = 0;
                    point = String.format("[%s,%s,%s]", corners[1].getX(), corners[1].getY(), corners[1].getZ());
                    sender.sendMessage(Language.STRUCTURE_CREATE_SECOND.replace("%point%", point));

                    this.sizeDisplay();
                } else {
                    sender.sendMessage(Language.STRUCTURE_CREATE_FAIL);
                }
                break;
            default:
                break;
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
        if (corners[0] == null || corners[1] == null) {
            sender.sendMessage(Language.STRUCTURE_CREATE_FAIL);
            return true;
        }
        StructureManager manager = Bukkit.getServer().getStructureManager();
        Structure structure = manager.createStructure();
        this.fixLocation(corners[0], corners[1]);
        structure.fill(corners[0], corners[1], true);
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

        corners[0] = null;
        corners[1] = null;
        
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

    private void sizeDisplay() {
        new BukkitRunnable() {
            // 次数
            int time = 0;
            @Override
            public void run() {
                    // 到次数或清空取消
                    time++;
                    if (time > 100 || corners[0] == null || corners[1] == null) {
                        cancel();
                        return;
                    }
                    ParticleUtils.cuboid(Particle.END_ROD, corners[0].clone(), corners[1].clone());
            } 
        }.runTaskTimer(Essential.getPlugin(), 0, 10);
    }
    
}
