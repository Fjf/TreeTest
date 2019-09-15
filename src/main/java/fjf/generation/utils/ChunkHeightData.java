package fjf.generation.utils;


import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.HashMap;

public class ChunkHeightData {
    private final SimplexOctaveGenerator generator;
    private final HashMap<Long, Double> heightMap;

    public ChunkHeightData(SimplexOctaveGenerator generator) {
        this.heightMap = new HashMap<>();
        this.generator = generator;
    }

    private long getLongFromInts(int a, int b) {
        return (((long) a) << 32) | (b & 0xffffffffL);
    }

    public double getHeight(int x, int z) {
        long id = getLongFromInts(x, z);
        if (heightMap.containsKey(id))
            return heightMap.get(id);

        // Add new entry
        double noise = generator.noise(x, 0, z,
                0.75D, 30D, true);
        heightMap.put(id, noise);
        return noise;
    }
}
