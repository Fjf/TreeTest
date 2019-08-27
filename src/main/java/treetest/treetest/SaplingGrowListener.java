package treetest.treetest;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.server.BroadcastMessageEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.util.Vector;
import treetest.treetest.utils.Tree;

import javax.swing.text.Position;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static java.lang.String.format;

public class SaplingGrowListener implements Listener {


    @EventHandler
    // Only works for default Minecraft trees now.
    public void onSaplingGrow(StructureGrowEvent event) {
        // Get sapling placement location
        Location location = event.getLocation();

        // Get world in which block was placed.
        World world = event.getWorld();

        // Get original sapling position
        int origX = location.getBlockX();
        int origY = location.getBlockY();
        int origZ = location.getBlockZ();


        // Only overwrite default oak trees
        if (event.getSpecies() != TreeType.TREE && event.getSpecies() != TreeType.BIG_TREE)
            return;

        int minX, maxX, minZ, maxZ;
        minX = maxX = origX;
        minZ = maxZ = origZ;
        while (true) {
            if (world.getBlockAt(minX - 1, origY, origZ).getType() != Material.OAK_SAPLING)
                break;
            minX--;
        }
        while (true) {
            if (world.getBlockAt(maxX, origY, origZ).getType() != Material.OAK_SAPLING)
                break;
            maxX++;
        }
        while (true) {
            if (world.getBlockAt(origX, origY, minZ - 1).getType() != Material.OAK_SAPLING)
                break;
            minZ--;
        }
        while (true) {
            if (world.getBlockAt(origX, origY, maxZ).getType() != Material.OAK_SAPLING)
                break;
            maxZ++;
        }

        if (event.getSpecies() == TreeType.BIG_TREE) {
            maxX++;
            maxZ++;
        }

        // Sapling area is not square or 1 by 1 in size
        if (maxZ - minZ != maxX - minX || maxX - minX == 1) {
            Bukkit.broadcastMessage("Size matters.");
            return;
        }
//        // Check if the entire area is filled with saplings.
//        for (int x = minX; x < maxX; x++) {
//            for (int z = minZ; z < maxZ; z++) {
//                if (world.getBlockAt(x, origY, z).getType() != Material.OAK_SAPLING) {
//                    Bukkit.broadcastMessage("Fill the entire square.");
//                    return;
//                }
//            }
//        }

        // From here on, the tree will grow.
        // Disable default tree growth event.
        event.setCancelled(true);

        float width = maxX - minX;
        // Remove saplings
        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                world.getBlockAt(x, origY, z).setType(Material.AIR);
            }
        }

        Material treeBlockType = Material.OAK_LOG;

        Tree tree = new Tree(world, treeBlockType, new Vector(minX + (maxX - minX) / 2.0, origY + 0.5, minZ + (maxZ - minZ) / 2.0));
        tree.generate(width);
        tree.fillTree();
    }
}
