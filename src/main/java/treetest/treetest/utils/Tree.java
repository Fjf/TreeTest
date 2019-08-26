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
    }

    public void generate(float thickness) {
        // Root node has 0 length for easy latching for other branches.
        addBranch(this.root, thickness);
    }

    private void addBranch(Branch parent, float thickness) {
        if (thickness < 0) return;

        // Amount of branches from current branch
        float branchChance = rand.nextFloat();

        if (branchChance < 0.5) {
            double angle =   (rand.nextFloat() - 0.5)*0.6 + 0.25 +        parent.getData().getTheta(); // Theta
            double rotation = rand.nextFloat() * 2 * Math.PI + parent.getData().getPhi(); // Phi

            int length = 3;

            float x, y, z;
            x = (float) (Math.sin(angle) * Math.cos(rotation));
            z = (float) (Math.sin(angle) * Math.sin(rotation));
            y = (float) (Math.cos(angle));
            Vector direction = new Vector(x, y, z);
            VectorDirectionData data = new VectorDirectionData(angle, rotation, length);

            parent.createChild(direction, data, length, thickness);
        } else if (branchChance < 1) {
            double angle =   (rand.nextFloat() - 2.0)*0.8 +    parent.getData().getTheta(); // Theta
            double rotation = rand.nextFloat() * 2 * Math.PI + parent.getData().getPhi(); // Phi

            int length = 3;

            // Branch one
            float x, y, z;
            Vector direction;
            VectorDirectionData data;

            // Dont grow down
            if (Math.abs(angle) < Math.PI / 2) {
                x = (float) (Math.sin(angle) * Math.cos(rotation));
                z = (float) (Math.sin(angle) * Math.sin(rotation));
                y = (float) (Math.cos(angle));
                direction = new Vector(x, y, z);
                data = new VectorDirectionData(angle, rotation, length);

                parent.createChild(direction, data, length, thickness);
            }

            // Branch two
            angle =    (rand.nextFloat())*0.8 +    parent.getData().getTheta(); // Theta
            rotation += rand.nextFloat() * Math.PI + 0.5 * Math.PI;

            // Dont grow down
            if (Math.abs(angle) < Math.PI / 2) {
                x = (float) (Math.sin(angle) * Math.cos(rotation));
                z = (float) (Math.sin(angle) * Math.sin(rotation));
                y = (float) (Math.cos(angle));
                direction = new Vector(x, y, z);
                data = new VectorDirectionData(angle, rotation, length);

                parent.createChild(direction, data, length, thickness);
            }
        } else {
            // Wont happen
        }

        for (Branch child : parent.getChildren()) {
            addBranch(child, thickness - 0.5f);
        }
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

        top = top.multiply(root.getRadius() - 0.25);
        top = top.add(root.getDirection());

        bottom = bottom.multiply(root.getRadius());

        Vector direction = new Vector(0, 0,0);
        direction.copy(top);
        direction.subtract(bottom);

        int steps = 20;
        for (int i = 0; i < steps; i++) {
            bottom.rotateAroundAxis(root.getDirection(), (2 * Math.PI / steps) * i);
            direction.rotateAroundAxis(root.getDirection(), (2 * Math.PI / steps) * i);

            bottom.add(root.getStart());

            BlockIterator bi = new BlockIterator(world, bottom, direction, 0, root.getLength());
            while (bi.hasNext()) {
                Block b = bi.next();
                b.setType(material);
            }

            bottom.subtract(root.getStart());
        }

        for (Branch child : root.getChildren()) {
            fillBranch(child);
        }
    }
}

