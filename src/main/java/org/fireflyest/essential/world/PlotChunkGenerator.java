package org.fireflyest.essential.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.HeightMap;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

public class PlotChunkGenerator extends ChunkGenerator {

    private final int BASE_HIGHT = 60;

    private final FastNoiseLite terrainNoise = new FastNoiseLite();
    private final FastNoiseLite detailNoise = new FastNoiseLite();

    private final List<Material> layers = Arrays.asList(Material.GRASS_BLOCK, Material.SAND, Material.SAND, Material.SAND);

    private final List<BlockPopulator> populators = new ArrayList<>();

    public PlotChunkGenerator() {
        // Set frequencies, lower frequency = slower change.
        terrainNoise.SetFrequency(0.00001f);
        detailNoise.SetFrequency(0.03f);

        // Fractal pattern (optional).
        terrainNoise.SetFractalType(FastNoiseLite.FractalType.FBm);
        terrainNoise.SetFractalOctaves(5);

        populators.add(new TreePopulator());
        populators.add(new GrassPopulator());
    }

    @Override
    public boolean canSpawn(World world, int x, int z) {
        return super.canSpawn(world, x, z);
    }

    @Override
    public void generateBedrock(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                chunkData.setBlock(i, chunkData.getMinHeight(), j, Material.BEDROCK);
                chunkData.setBlock(i, chunkData.getMinHeight() + 1, j, Material.BEDROCK);
            }
        }
    }

    @Override
    public void generateCaves(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        super.generateCaves(worldInfo, random, chunkX, chunkZ, chunkData);
    }

    @Override
    public void generateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        if (chunkX % 4 != 0 && chunkZ % 4 != 0) {
            for(int i = 0; i < 16; i++) {
                for(int j = 0; j < 16; j++) {
                    for (int k = 2; k < BASE_HIGHT + 60; k++) {
                        chunkData.setBlock(i, chunkData.getMinHeight() + k, j, Material.STONE);
                    }
                    chunkData.setBlock(i, chunkData.getMinHeight() + BASE_HIGHT + 60, j, Material.DIRT);
                    chunkData.setBlock(i, chunkData.getMinHeight() + BASE_HIGHT + 61, j, Material.DIRT);
                    chunkData.setBlock(i, chunkData.getMinHeight() + BASE_HIGHT + 62, j, Material.DIRT);
                    chunkData.setBlock(i, chunkData.getMinHeight() + BASE_HIGHT + 63, j, Material.DIRT);
                    chunkData.setBlock(i, chunkData.getMinHeight() + BASE_HIGHT + 64, j, Material.GRASS_BLOCK);
                }
            }
        } else {
            for(int y = chunkData.getMinHeight(); y < 130 && y < chunkData.getMaxHeight(); y++) {
                for(int x = 0; x < 16; x++) {
                    for(int z = 0; z < 16; z++) {
                        float noise2 = (terrainNoise.GetNoise(x + (chunkX * 16), z + (chunkZ * 16)) * 2) + (detailNoise.GetNoise(x + (chunkX * 16), z + (chunkZ * 16)) / 10);
                        float currentY = (BASE_HIGHT - 3 + (noise2 * 8));
                        
                        if (chunkX % 4 == 0 && chunkZ % 4 != 0) {
                            currentY += (Math.pow(x - 8.0, 2) / 16) + 1;
                            if ((x == 0 || x == 15) && y == BASE_HIGHT + 1) {
                                chunkData.setBlock(x, y, z, Material.SMOOTH_STONE_SLAB);
                            }
                        } else if (chunkX % 4 != 0 && chunkZ % 4 == 0) {
                            currentY += (Math.pow(z - 8.0, 2) / 16) + 1;
                            if ((z == 0 || z == 15) && y == BASE_HIGHT + 1) {
                                chunkData.setBlock(x, y, z, Material.SMOOTH_STONE_SLAB);
                            }
                        } else {
                            currentY += ((Math.pow(x - 8.0, 2) + Math.pow(z - 8.0, 2)) / 20) - 2;
                        }

                        if(y < currentY && y < BASE_HIGHT + 1) {
                            float distanceToSurface = Math.abs(y - currentY); // The absolute y distance to the world surface.
                            if (y == BASE_HIGHT) {
                                chunkData.setBlock(x, y, z, layers.get(random.nextInt(layers.size())));
                            } else if (distanceToSurface > 2) {
                                chunkData.setBlock(x, y, z, Material.STONE);
                            } else {
                                chunkData.setBlock(x, y, z, Material.SAND);
                            }
                        } else if(y < BASE_HIGHT) {
                            chunkData.setBlock(x, y, z, Material.WATER);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void generateSurface(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        
    }

    @Override
    public int getBaseHeight(WorldInfo worldInfo, Random random, int x, int z, HeightMap heightMap) {
        return BASE_HIGHT;
    }

    @Override
    public BiomeProvider getDefaultBiomeProvider(WorldInfo worldInfo) {
        return super.getDefaultBiomeProvider(worldInfo);
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return populators;
    }
    
}
