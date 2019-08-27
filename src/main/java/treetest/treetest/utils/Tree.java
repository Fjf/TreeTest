package treetest.treetest.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tree {
    private final Material material;
    private Branch root;
    private Random rand;
    private World world;
    private List<Branch> branches;
    private int baseLength;
    private final int minLeafRadius = 2;
    private final int maxLeafRadius = 3;

    public Branch getRoot() {
        return root;
    }

    public void setRoot(Branch root) {
        this.root = root;
    }

    public Tree(World world, Material material, Vector root) {
        this.world = world;
        this.material = material;
        this.rand = new Random();
        this.root = new Branch(
                new Vector(root.getX(), root.getY(), root.getZ()),
                new Vector(root.getX(), root.getY(), root.getZ()),
                new VectorDirectionData(0, 0, 0),
                0, null, 0);

        this.branches = new ArrayList<>();
        this.baseLength = 2;
    }

    public void generate(float thickness) {
        // Root node has 0 length for easy latching for other branches.
        generateBranch(this.root, thickness);
    }

    private void generateBranch(Branch parent, float thickness) {
        if (thickness < 0.5) return;

        double angle;
        double rotation;
        int length = (int) (thickness * 2 + baseLength);

        // Amount of branches from current branch
        float branchChance = rand.nextFloat();

        int tries = 0;
        // Try 10 times to add a branch
        while (tries++ < 10) {
            angle =   (rand.nextFloat() - 0.5)*0.6 + 0.25 + parent.getData().getTheta(); // Theta
            rotation = rand.nextFloat() * 2 * Math.PI + parent.getData().getPhi(); // Phi

            if (addBranch(parent, thickness, angle, rotation, length))
                break;
        }

        // Dont add branches when parent too low
        if (parent.getEnd().getY() > parent.getLength() * 1.4 && parent.getLength() != 0) {
            if (branchChance > 1 - Math.pow(0.5, thickness)) { // 2 branches
                tries = 0;
                // Try 10 times to add a branch
                while (tries++ < 10) {
                    angle = (rand.nextFloat() - 0.5) * 0.6 + 0.25 + // Randomized angle
                            (0.5 - rand.nextInt(2)) * Math.PI + // 90 degree angle
                            parent.getData().getTheta(); // Theta

                    rotation = rand.nextFloat() * 2 * Math.PI + parent.getData().getPhi(); // Phi

                    if (addBranch(parent, thickness * (rand.nextFloat() * 0.2f + 0.8f), angle, rotation, length))
                        break;
                }
            }
            if (branchChance > 1 - Math.pow(0.6, thickness)) { // 3 branches
                tries = 0;
                // Try 10 times to add a branch
                while (tries++ < 10) {
                    angle = (rand.nextFloat() - 0.5) * 0.6 + 0.25 + // Randomized angle
                            (0.5 - rand.nextInt(2)) * Math.PI + // 90 degree angle
                            parent.getData().getTheta(); // Theta

                    rotation = rand.nextFloat() * 2 * Math.PI + parent.getData().getPhi(); // Phi

                    if (addBranch(parent, thickness * (rand.nextFloat() * 0.2f + 0.8f), angle, rotation, length))
                        break;
                }
            }
            if (branchChance > 1 - Math.pow(0.7, 1 / thickness + 1)) { // 3 branches
                tries = 0;
                // Try 10 times to add a branch
                while (tries++ < 10) {
                    angle = (rand.nextFloat() - 0.5) * 0.6 + 0.25 + // Randomized angle
                            (0.5 - rand.nextInt(2)) * Math.PI + // 90 degree angle
                            parent.getData().getTheta(); // Theta

                    rotation = rand.nextFloat() * 2 * Math.PI + parent.getData().getPhi(); // Phi

                    if (addBranch(parent, thickness * (rand.nextFloat() * 0.2f + 0.8f), angle, rotation, length))
                        break;
                }
            }
        }

        if (parent.getChildren().size() == 0)
            parent.setRadius(0.4);

        // Add all branches to central list.
        this.branches.addAll(parent.getChildren());

        for (Branch child : parent.getChildren()) {
            generateBranch(child, thickness * 0.9f - 0.3f);
        }
    }

    private boolean addBranch(Branch parent, float thickness, double angle, double rotation, int length) {
        float x, y, z;
        Vector direction;
        VectorDirectionData data;

        x = (float) (Math.sin(angle) * Math.cos(rotation));
        z = (float) (Math.sin(angle) * Math.sin(rotation));
        y = (float) (Math.cos(angle));
        direction = new Vector(x, y, z);
        data = new VectorDirectionData(angle, rotation, length);

        Branch child = new Branch(parent.getEnd(), direction, data, length, parent, thickness / 2);

        // Dont allow branches to grow below original y + 2 when they are not angled upwards
        if (child.getEnd().getY() < root.getStart().getY() + 4 && Math.abs(angle) > Math.PI / 4) {
            return false;
        }
        // Dont angle branches sharply downwards
        if (Math.abs(angle) > 4 * Math.PI / 5)
            return false;
        // Dont allow branches to touch
        if (this.isTouching(child))
            return false;

        parent.createChild(child);
        return true;
    }

    private boolean isTouching(Branch child) {
        for (Branch branch : this.branches) {
            if (distance(child.getEnd(), branch.getStart(), branch.getEnd()) < branch.getRadius() + child.getRadius() + 1) {
                return true;
            }
        }
        return false;
    }

    public void fillTree() {
        for (Branch child : root.getChildren()) {
            fillBranch(child);
        }
    }

    private void fillBranch(Branch root) {
        // Orthogonal vector to original vector.
        Vector bottom = new Vector(-root.getDirection().getY(), root.getDirection().getX(), 0).normalize();
        Vector top = new Vector(bottom.getX(), bottom.getY(), bottom.getZ());

        top = top.multiply(root.getRadius());
        top = top.add(root.getDirection());

        bottom = bottom.multiply(root.getRadius());

        Vector direction = new Vector(0, 0,0);
        direction.copy(top);
        direction.subtract(bottom);

        int leafRadius = rand.nextInt(maxLeafRadius - minLeafRadius) + minLeafRadius;

        if (root.getRadius() < 1) { // Add leaves
            for (double i = 0; i <= Math.PI; i += Math.PI / 20) {
                double radius = leafRadius * Math.sin(i); // we get the current radius
                double y = leafRadius * Math.cos(i);
                for (double a = 0; a < 2 * Math.PI; a += Math.PI / 10) {
                    double z = Math.sin(a) * radius; // z-coordinate

                    double minX = Math.cos(a) * radius;
                    for (double x = minX, maxX = -minX; x < maxX; x++) {
                        Vector end = root.getEnd();
                        Block b = world.getBlockAt((int) (x + end.getX()), (int) (y + end.getY()), (int) (z + end.getZ()));
                        if (b.getType() == Material.AIR) {
                            b.setType(Material.OAK_LEAVES);

                        }
                    }
                }
            }
        }

        int steps = (int) Math.pow(root.getRadius() * 4, 2);
        for (int i = 0; i < steps; i++) {
            bottom.rotateAroundAxis(root.getDirection(), (2 * Math.PI / steps) * i);
            direction.rotateAroundAxis(root.getDirection(), (2 * Math.PI / steps) * i);

            bottom.add(root.getStart());

            BlockIterator bi = new BlockIterator(world, bottom, direction, 0, root.getLength());
            while (bi.hasNext()) {
                Block b = bi.next();
                b.setType(material);
            }

            if (root.getRadius() < 0.5) {
                break;
            }

            bottom.subtract(root.getStart());
        }

        for (Branch child : root.getChildren()) {
            fillBranch(child);
        }
    }
    
    private double distance(Vector point,
                           Vector linePointA,
                           Vector linePointB) {
        double[] pointTemp = new double[3];
        double[] totalTemp = new double[3];
        pointTemp[0] = linePointA.getX() - point.getX();
        pointTemp[1] = linePointA.getY() - point.getY();
        pointTemp[2] = linePointA.getZ() - point.getZ();

        totalTemp[0] =  (pointTemp[1]*linePointB.getZ() - pointTemp[2]*linePointB.getY());
        totalTemp[1] = -(pointTemp[0]*linePointB.getZ() - pointTemp[2]*linePointB.getX());
        totalTemp[2] =  (pointTemp[0]*linePointB.getY() - pointTemp[1]*linePointB.getX());

        return (float) (Math.sqrt(totalTemp[0]*totalTemp[0] + totalTemp[1]*totalTemp[1] + totalTemp[2]*totalTemp[2]) /
                Math.sqrt(linePointB.getX() * linePointB.getX() + linePointB.getY() * linePointB.getY() + linePointB.getZ() * linePointB.getZ()));
    }


}

