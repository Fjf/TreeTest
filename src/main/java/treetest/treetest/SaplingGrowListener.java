package treetest.treetest;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.server.BroadcastMessageEvent;
import org.bukkit.event.world.StructureGrowEvent;

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

        Random rand = new Random();

        int treeCurrentWidth = 3;
        float treeNarrowChance = 0.35f;
        int treeNarrowGoal = 3;
        int treeLastNarrow = 0;

        float treeOffsetChance = 0.1f;


        int x = 0;
        int y = 0;
        int z = 0;

        while (true) {
            // Build layer
            for (int i = 0; i < treeCurrentWidth; i++) {
                for (int j = 0; j < treeCurrentWidth; j++) {
                    world.getBlockAt(origX + x + i, origY + y, origZ + z + j).setType(treeBlockType);
                }
            }

            // Make tree less wide the higher you go.
            float r = rand.nextFloat();
            Bukkit.broadcastMessage(format("%f -> %f", r, (treeLastNarrow - treeNarrowGoal) * treeNarrowChance));
            if (r < (treeLastNarrow - treeNarrowGoal) * treeNarrowChance) {
                treeCurrentWidth -= 1;
                treeLastNarrow = 0;

                // To pick random part on the top of the tree
                switch (rand.nextInt(4)) {
                    case 0:
                        x += 1;
                        break;
                    case 1:
                        z += 1;
                        break;
                    case 2:
                        z += 1;
                        x += 1;
                        break;
                }
            } else {
                // Make tree slightly slant randomly in a direction.
                if (rand.nextFloat() < treeOffsetChance) {
                    switch (rand.nextInt(4)) {
                        case 0:
                            x -= 1;
                            break;
                        case 1:
                            x += 1;
                            break;
                        case 2:
                            z -= 1;
                            break;
                        case 3:
                            z += 1;
                            break;
                    }
                }
            }
            treeLastNarrow += 1;

            if (treeCurrentWidth <= 0)
                break;

            y += 1;
        }
    }
}
