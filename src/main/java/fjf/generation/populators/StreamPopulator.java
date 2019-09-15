package fjf.generation.populators;

import fjf.generation.constants.GenerationConstants;
import org.bukkit.*;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class StreamPopulator extends BlockPopulator {
    private World world;
    private Random random;
    private Chunk chunk;

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        this.world = world;
        this.random = random;
        this.chunk = chunk;

        if (!isSnow(0.75f))
            return;

        generate();
    }

    private void generate() {
        Location lowest = findLowest();
        createPool(lowest);
        world.getBlockAt(lowest).setType(Material.WATER);
    }

    private void createPool(Location lowest) {
//        for (int x = -)
    }

    private Location findLowest() {
        int x, z;
        int lowest = Integer.MAX_VALUE;
        Location loc = new Location(world, 0, 0, 0);
        for (int i = 0; i < 16; i++) {
            x = i + chunk.getX() * 16;
            for (int j = 0; j < 16; j++) {
                z = i + chunk.getZ() * 16;
                int y = world.getHighestBlockYAt(x, z);
                if (y < lowest) {
                    lowest = y;
                    // Update lowest position
                    loc.setX(x); loc.setY(y); loc.setZ(z);
                }
            }
        }
        return loc;
    }

    private boolean isSnow(float percent) {
        // TODO: Maybe use percentage instead of forcing all of the chunk to be high enough.
        int startX = chunk.getX()*16;
        int startZ = chunk.getZ()*16;
        int y;
        for (int x = startX; x < startX + 16; x++) {
            for (int z = startZ; z < startZ + 16; z++) {
                y = world.getHighestBlockYAt(x, z);

                if (y < GenerationConstants.levels.MIN_SNOW)
                    return false;
            }
        }
        return true;
    }
}
