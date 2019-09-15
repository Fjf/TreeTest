package fjf.generation;

import fjf.generation.constants.GenerationConstants;
import fjf.generation.populators.CityPopulator;
import fjf.generation.populators.StreamPopulator;
import fjf.generation.populators.TreePopulator;
import fjf.generation.utils.ChunkHeightData;
import fjf.generation.utils.DiamondSquare;
import jdk.nashorn.internal.ir.Block;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CustomChunkGenerator extends ChunkGenerator {
    private final ChunkHeightData chunkHeightData;
    private int currentHeight;

    public CustomChunkGenerator(ChunkHeightData chunkHeightData) {
        this.chunkHeightData = chunkHeightData;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Arrays.asList(
                new TreePopulator(),
                new CityPopulator(),
                new StreamPopulator()
        );
    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        ChunkData chunk = createChunkData(world);
        int averageHeight = 65; // Also amplitude
        int biasHeight = GenerationConstants.levels.WATER + 1;

//        checkChunk(chunkX, chunkY);

        DiamondSquare d = new DiamondSquare(chunkHeightData, 17, chunkX, chunkZ, averageHeight, biasHeight);

        d.smoothen(3);
        double[][] hm = d.getHeightMap(chunkX, chunkZ);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                currentHeight = (int) hm[x][z];

                // Add water
                if (currentHeight < GenerationConstants.levels.WATER) {
                    int steps = GenerationConstants.levels.WATER - currentHeight;
                    currentHeight = GenerationConstants.levels.WATER;
                    addBlocks(chunk, x, z, Material.WATER, steps);

                    if (currentHeight > GenerationConstants.levels.MIN_SAND)
                        addBlocks(chunk, x, z, Material.SAND, 2);
                    else
                        addBlocks(chunk, x, z, Material.DIRT, 2);
                }

                // Random snow on top of the top blocks
                if (currentHeight >= GenerationConstants.levels.MIN_SNOW) {
                    int choice = random.nextInt(10) + currentHeight - GenerationConstants.levels.MIN_SNOW;
                    if (choice >= 7) {
                        currentHeight += 1;
                        addBlocks(chunk, x, z, Material.SNOW, 1);
                    }
                }


                // Mountain top gravel or stone
                if (currentHeight >= GenerationConstants.levels.MIN_MOUNTAINTOP) {
                    int choice = random.nextInt(10) + currentHeight - GenerationConstants.levels.MIN_MOUNTAINTOP;
                    if (choice >= 9) {
                        if (random.nextBoolean())
                            addBlocks(chunk, x, z, Material.GRAVEL, 1);
                        else
                            addBlocks(chunk, x, z, Material.STONE, 1);
                    } else {
                        addBlocks(chunk, x, z, Material.GRASS_BLOCK, 1);
                    }
                }
                else if (currentHeight > GenerationConstants.levels.MIN_SAND &&
                         currentHeight <= GenerationConstants.levels.MAX_SAND) {
                    addBlocks(chunk, x, z, Material.SAND, 2);

                } // Default grass with dirt
                else  {
                    addBlocks(chunk, x, z, Material.GRASS_BLOCK, 1);
                    addBlocks(chunk, x, z, Material.DIRT, 3);
                } // Sand near water elevation


                // Finish every column with stone until bedrock.
                while (currentHeight > 1)
                    chunk.setBlock(x, currentHeight--, z, Material.STONE);
                chunk.setBlock(x, currentHeight, z, Material.BEDROCK);
            }
        }
        return chunk;
    }

    private boolean addBlocks(ChunkData chunk, int x, int z, Material mat, int n) {
        if (currentHeight - n <= 1) {
            return false;
        }

        for (int i = 0; i < n; i++) {
            chunk.setBlock(x, currentHeight, z, mat);
            currentHeight -= 1;
        }

        return true;
    }

}
