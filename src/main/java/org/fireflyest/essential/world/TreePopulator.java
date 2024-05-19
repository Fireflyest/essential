package org.fireflyest.essential.world;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class TreePopulator extends BlockPopulator {

    private final HashMap<Biome, List<TreeType>> biomeTrees = new HashMap<>();

    public TreePopulator() {
        biomeTrees.put(Biome.PLAINS, Arrays.asList());
        biomeTrees.put(Biome.FOREST, Arrays.asList(TreeType.BIRCH));
        biomeTrees.put(Biome.DARK_FOREST, Arrays.asList(TreeType.DARK_OAK));
    }

    @Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion limitedRegion) {
        int x = random.nextInt(16) + chunkX * 16;
        int z = random.nextInt(16) + chunkZ * 16;
        int y = 70;
        while(limitedRegion.getType(x, y, z).isAir() && y > 60) y--;

        Location location = new Location(Bukkit.getWorld(worldInfo.getUID()), x, y, z);
        List<TreeType> trees = biomeTrees.getOrDefault(limitedRegion.getBiome(location), Arrays.asList(TreeType.TREE, TreeType.BIRCH));

        if (!trees.isEmpty() && (limitedRegion.getType(x, y - 1, z).equals(Material.GRASS_BLOCK) || limitedRegion.getType(x, y - 1, z).equals(Material.SAND))) {
            limitedRegion.generateTree(location, random, trees.get(random.nextInt(trees.size())));
        }
    }
}