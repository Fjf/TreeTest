package fjf.generation.populators;

import fjf.generation.constants.GenerationConstants;
import fjf.generation.structures.House;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class CityPopulator extends BlockPopulator {
    @Override
    public void populate(World world, Random random, Chunk chunk) {
        // 1/50 chance of a city to spawn in a chunk
        if (random.nextInt(100) != 0)
            return;

        // Check if chunk is not submerged in water.
        if (!isLand(world, chunk))
            return;

        int x = 5 + chunk.getX() * 16;
        int z = 5 + chunk.getZ() * 16;

        addHouse(world, x, z);
    }

    private void addHouse(World world, int x, int z) {
        House house = new House(world, x, z);
        house.generate();
    }

    private boolean isLand(World world, Chunk chunk) {
        int startX = chunk.getX()*16;
        int startZ = chunk.getZ()*16;
        int y;
        for (int x = startX; x < startX + 16; x++) {
            for (int z = startZ; z < startZ + 16; z++) {
                y = world.getHighestBlockYAt(x, z);

                // Dont allow buildings on water.
                if (world.getBlockAt(x, y - 1, z).getType() == Material.WATER)
                    return false;

                if (y < GenerationConstants.levels.WATER)
                    return false;
            }
        }
        return true;
    }
}
