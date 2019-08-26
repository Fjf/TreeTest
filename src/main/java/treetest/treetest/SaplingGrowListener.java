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
    public void onSaplingGrow(StructureGrowEvent event) {
        // Only works for default Minecraft trees now.
        if (event.getSpecies() != TreeType.TREE && event.getSpecies() != TreeType.BIG_TREE)
            return;

        // Disable default tree growth event.
        event.setCancelled(true);

        // Get world in which block was placed.
        World world = event.getWorld();

        Location location = event.getLocation();

        // Get original sapling position
        int origX = location.getBlockX();
        int origY = location.getBlockY();
        int origZ = location.getBlockZ();

        Material treeBlockType = Material.OAK_LOG;

        Tree tree = new Tree(world, treeBlockType, new Vector(origX + 0.5, origY + 0.5, origZ + 0.5));
        tree.generate(2.0f);
        tree.fillTree();
    }
}
