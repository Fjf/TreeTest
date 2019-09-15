package fjf.generation;

import com.sun.javaws.exceptions.InvalidArgumentException;
import fjf.generation.utils.ChunkHeightData;
import fjf.generation.utils.DiamondSquare;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import static fjf.generation.constants.PluginSettings.STORAGE_FOLDER;


public final class Generation extends JavaPlugin {

    @Override
    public void onEnable() {
        // Create data storage folder if it does not exist.
        File directory = new File(STORAGE_FOLDER);
        if (!directory.exists()){
            if (!directory.mkdirs());
                System.out.println("Cannot create folder to store data. (Insufficient rights?)");
        }

        getServer().getPluginManager().registerEvents(new SaplingGrowListener(), this);
    }



    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
//        System.out.println(worldName);
//        World world = Bukkit.getWorld(worldName);
//        if (world == null) {
//            System.out.println("This world does not exist, and no generator will be created.");
//            return null;
//        }

        // Set generator in chunkheightdata data storage.
        SimplexOctaveGenerator generator = new SimplexOctaveGenerator(new Random(0), 8);
        generator.setScale(0.8);
        ChunkHeightData chunkHeightData = new ChunkHeightData(generator);

        return new CustomChunkGenerator(chunkHeightData);
    }
}
