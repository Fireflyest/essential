package org.fireflyest.essential.command;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.RayTraceResult;
import org.fireflyest.craftcommand.command.SubCommand;
import org.fireflyest.essential.data.Language;

public class StructurePlaceCommand extends SubCommand {

    @Override
    protected boolean execute(CommandSender sender) {
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1) {
        return execute(sender, arg1, "NONE");
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1, String arg2) {
        return execute(sender, arg1, arg2, "NONE");
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1, String arg2, String arg3) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        
        StructureManager manager = Bukkit.getServer().getStructureManager();

        RayTraceResult result = player.rayTraceBlocks(100);
        if (result == null) {
            sender.sendMessage(Language.STRUCTURE_PLACE_FAIL);
            return true;
        }

        Block hitBlock = result.getHitBlock();
        Location loc = hitBlock.getLocation().add(0, 1, 0);

        String structureName = arg1;
        float intergrity = 1;
        if (arg1.contains(":")) {
            structureName = arg1.split(":")[0];
            intergrity = NumberConversions.toFloat(arg1.split(":")[1]);
        }
        Structure structure = manager.getStructure(NamespacedKey.fromString(structureName));
        if (structure == null) {
            sender.sendMessage(Language.STRUCTURE_PLACE_NULL);
            return true;
        }

        Random random = new Random(hitBlock.getWorld().getSeed());
        structure.place(loc, true, StructureRotation.valueOf(arg2), Mirror.valueOf(arg3), 0, intergrity, random);

        return true;
    }
    
}
