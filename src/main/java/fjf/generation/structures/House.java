package fjf.generation.structures;

import org.bukkit.Axis;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.*;
import org.bukkit.block.data.type.GlassPane;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.TrapDoor;

import javax.swing.plaf.synth.SynthLabelUI;
import java.util.Random;

public class House {
    private World world;
    private int x;
    private int z;
    private int baseHeight;
    private int stories;
    private Random random;

    private int width = 6; // Outer wall width
    private int depth = 7; // Outer wall depth

    public House(World world, int x, int z) {
        this.world = world;
        this.x = x - width / 2;
        this.z = z - depth / 2;
        this.random = new Random();
        this.stories = random.nextInt(3);
        this.width = random.nextInt(3) + 5;
        this.depth = random.nextInt(3) + 6;
    }

    public void generate() {
        this.baseHeight = getAverageHeight() + 1;
        fillBase();
        generateFrame();
        generateRoof();
        generateWalls();
        generateFloors();
    }

    private void generateFloors() {
        int x, z;
        for (int i = 0; i < stories + 1; i++) {
            int y = i * 4 + baseHeight - 1;
            for (int xx = 0; xx < width; xx++) {
                for (int zz = 0; zz < depth; zz++) {
                    if (xx % (width - 1) == 0 || zz % (depth - 1) == 0) {
                        continue; // Dont fill the edges and corners.
                    }
                    x = this.x + xx;
                    z = this.z + zz;

                    Block b = world.getBlockAt(x, y, z);
                    b.setType(Material.SPRUCE_SLAB);

                    // Only set top slabs.
                    Slab bd = (Slab) b.getBlockData();
                    bd.setType(Slab.Type.TOP);
                    b.setBlockData(bd);
                }
            }
        }
    }

    private void generateWalls() {
        Material mat;
        Material window;
        BlockFace dir;
        for (int i = 0; i < stories; i++) {
            int y = i * 4 + baseHeight;

            // Different stories have different block profiles.
            if (i == 0) {
                mat = Material.COBBLESTONE;
                window = Material.WHITE_STAINED_GLASS_PANE;
            } else {
                mat = Material.SPRUCE_PLANKS;
                window = Material.SPRUCE_TRAPDOOR;
            }

            for (int x = 0; x < width; x++) {
                for (int z = 0; z < depth; z++) {
                    if ((x % (width - 1) != 0 && z % (depth - 1) != 0) ||
                        (x % (width - 1) == 0 && z % (depth - 1) == 0)) {
                        continue; // Dont fill the middle and the corners.
                    }

                    // Set trapdoor direction
                    if (x == 0) {
                        dir = BlockFace.WEST;
                    } else if (x == width - 1) {
                        dir = BlockFace.EAST;
                    } else if (z == 0) {
                        dir = BlockFace.NORTH;
                    } else {
                        dir = BlockFace.SOUTH;
                    }

                    world.getBlockAt(x + this.x, y, z + this.z).setType(mat);
                    world.getBlockAt(x + this.x, y + 2, z + this.z).setType(mat);

                    // Value to determine where to place windows.
                    boolean val = (x == 0 || x == this.width - 1 || (x > 1 && x < this.width - 2))
                               && (z == 0 || z == this.depth - 1 || (z > 1 && z < this.depth - 2));

                    if (val && random.nextBoolean()) { // Approx 25% of the walls are 1 by 1 windows.
                        Block b = world.getBlockAt(x + this.x, y + 1, z + this.z);
                        b.setType(window);
                        BlockData bd = b.getBlockData();
                        if (bd instanceof TrapDoor) {
                            TrapDoor td = (TrapDoor) bd;
                            td.setOpen(true);

                            // Set trapdoor direction inwards
                            if (x == 0) {
                                dir = BlockFace.WEST;
                            } else if (x == width - 1) {
                                dir = BlockFace.EAST;
                            } else if (z == 0) {
                                dir = BlockFace.NORTH;
                            } else {
                                dir = BlockFace.SOUTH;
                            }

                            td.setFacing(dir);
                            b.setBlockData(td);
                        } else if (bd instanceof GlassPane) {
                            GlassPane gp = (GlassPane) bd;

                            // Set glass direction left and right compared to direction.
                            if (x == 0 || x == width - 1) {
                                gp.setFace(BlockFace.NORTH, true);
                                gp.setFace(BlockFace.SOUTH, true);
                            } else {
                                gp.setFace(BlockFace.EAST, true);
                                gp.setFace(BlockFace.WEST, true);
                            }

                            b.setBlockData(gp);
                        }
                    } else {
                        world.getBlockAt(x + this.x, y + 1, z + this.z).setType(mat);
                    }
                }
            }
        }
    }

    private void fillBase() {
        int x, z;
        for (int i = 0; i < width; i++) {
            x = i + this.x;
            for (int j = 0; j < depth; j++) {
                z = j + this.z;

                int y = world.getHighestBlockYAt(x, z);

                // Clear the blocks to make room for the house.
                while (y >= baseHeight) {
                    world.getBlockAt(x, y, z).setType(Material.AIR);
                    y--;
                }

                // Fill with cobble below the house to make an even house base.
                while (y < baseHeight) {
                    world.getBlockAt(x, y, z).setType(Material.COBBLESTONE);
                    y++;
                }
            }
        }
    }

    private void generateFrame() {
        int poleHeight = stories * 4 + 2;
        // The vertical poles.
        for (int y = baseHeight; y < baseHeight + poleHeight; y++) {
            world.getBlockAt(x, y, z).setType(Material.SPRUCE_LOG);
            world.getBlockAt(x + width - 1, y, z).setType(Material.SPRUCE_LOG);
            world.getBlockAt(x, y, z + depth - 1).setType(Material.SPRUCE_LOG);
            world.getBlockAt(x + width - 1, y, z + depth - 1).setType(Material.SPRUCE_LOG);
        }

        // The horizontal poles along X axis.
        for (int x = this.x - 1; x < this.x + this.width + 1; x++) {
            for (int yy = 1; yy < stories + 1; yy++) {
                int y = baseHeight - 1 + yy * 4;

                Block b = world.getBlockAt(x, y, z);

                // Set rotation
                b.setType(Material.SPRUCE_LOG);
                Orientable bd = (Orientable) b.getBlockData();
                bd.setAxis(Axis.X);

                b.setBlockData(bd);

                world.getBlockAt(x, y, z + depth - 1).setType(Material.SPRUCE_LOG);
                world.getBlockAt(x, y, z + depth - 1).setBlockData(bd);
            }
        }
        // The horizontal poles along X axis.
        for (int z = this.z - 1; z < this.z + this.depth + 1; z++) {
            for (int yy = 1; yy < stories + 1; yy++) {
                int y = baseHeight - 1 + yy * 4;

                Block b = world.getBlockAt(x, y, z);

                // Set rotation
                b.setType(Material.SPRUCE_LOG);
                Orientable bd = (Orientable) b.getBlockData();
                bd.setAxis(Axis.Z);

                b.setBlockData(bd);

                world.getBlockAt(x + width - 1, y, z).setType(Material.SPRUCE_LOG);
                world.getBlockAt(x + width - 1, y, z).setBlockData(bd);
            }
        }
    }


    private void generateRoof() {
        int baseY = stories * 4 + baseHeight - 1; // Roof starts 1 lower than the highest frame block
        // Generate roof along the longest part.
        for (int x = -1; x < this.width + 1; x++) { // Roof extends one further than the walls
            for (int z = -1; z < this.depth + 1; z++) {
                if (width < depth) { // Along X axis

                    // Get Y for current X value.
                    int y = baseY + ((this.width + 2) - Math.abs((int)((x + 1.0D) - (this.width + 1.0D) / 2.0d))) - this.width / 2;
                    Block b = world.getBlockAt(this.x + x, y, this.z + z);
                    b.setType(Material.DARK_OAK_STAIRS);

                    Stairs bd = (Stairs) b.getBlockData();
                    if (x < width / 2) {
                        bd.setFacing(BlockFace.EAST);
                    } else if (x >= width - width / 2) {
                        bd.setFacing(BlockFace.WEST);
                    } else {
                        if (z < depth / 2) {
                            bd.setFacing(BlockFace.NORTH);
                        } else if (z > depth - depth / 2) {
                            bd.setFacing(BlockFace.SOUTH);
                        }
                    }
                    b.setBlockData(bd);
                } else { // Along Z axis.
                    // Get Y for current X value.
                    int y = baseY + (this.depth - Math.abs((int)((z + 1.0D) - (this.depth + 1.0D) / 2.0d))) - this.depth / 2;
                    Block b = world.getBlockAt(this.x + x, y, this.z + z);
                    b.setType(Material.DARK_OAK_STAIRS);

                    Stairs bd = (Stairs) b.getBlockData();
                    if (z < depth / 2) {
                        bd.setFacing(BlockFace.SOUTH);
                    } else if (z > depth - depth / 2) {
                        bd.setFacing(BlockFace.NORTH);
                    } else {
                        if (x < width / 2) {
                            bd.setFacing(BlockFace.WEST);
                        } else if (x > width - width / 2) {
                            bd.setFacing(BlockFace.EAST);
                        }
                    }
                    b.setBlockData(bd);
                }
            }
        }
    }

    private int getAverageHeight() {
        int ii, jj;
        int sum = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < depth; j++) {
                // Get average around the base of the house.
                if (i % (width - 1) == 0 || j % (depth - 1) == 0) {
                    sum += world.getHighestBlockYAt(i + x, j + z);
                }
            }
        }
        return sum / (width * 2 + depth * 2 - 4);
    }
}
