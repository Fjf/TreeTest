package fjf.generation.populators;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class TreePopulator extends BlockPopulator {
    @Override
    public void populate(World world, Random random, Chunk chunk) {
//            for (int i = 0; i < 10; i++) {
//                int x = random.nextInt(16);
//                int z = random.nextInt(16);
//                int y = world.getHighestBlockYAt(x + chunk.getX() * 16, z + chunk.getZ() * 16);
//
//                if (y > 43) {
//                    world.generateTree(chunk.getBlock(x, y, z).getLocation(), TreeType.TREE); // The tree type can be changed if you want.
//                }
//            }

//            Material treeBlockType = Material.OAK_LOG;
//
//            Tree tree = new Tree(world, treeBlockType, new Vector(x + 0.5, y - 1 + 0.5, z + 0.5));
//            tree.generate(2f);
//            tree.fillTree();
//        }
    }
}
