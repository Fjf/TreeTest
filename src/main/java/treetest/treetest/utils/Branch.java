package treetest.treetest.utils;

import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class Branch {
    private int length;

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    private double radius;

    private Vector start;
    private Vector direction;
    private VectorDirectionData data;

    private Branch parent;
    private List<Branch> children;


    public Vector getDirection() {
        return direction;
    }

    public void setDirection(Vector direction) {
        this.direction = direction;
    }


    public Branch getParent() {
        return parent;
    }

    public List<Branch> getChildren() {
        return children;
    }

    public Vector getStart() {
        return start;
    }

    public void setStart(Vector start) {
        this.start = start;
    }

    public Vector getEnd() {
        Vector end = new Vector(start.getX(), start.getY(), start.getZ());
        Vector dir = new Vector(direction.getX(), direction.getY(), direction.getZ());
        return end.add(dir.multiply(length));
    }

    public Branch(Vector start, Vector direction, VectorDirectionData data, int length, Branch parent, double radius) {
        this.start = start;
        this.direction = direction;
        this.length = length;
        this.data = data;
        this.radius = radius;

        this.parent = parent;
        this.children = new ArrayList<Branch>();
    }

    public void createChild(Vector direction, VectorDirectionData data, int length, double width) {
        Branch child = new Branch(this.getEnd(), direction, data, length,this, width / 2);
        this.children.add(child);
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return format("At: (%.2f, %.2f, %.2f) | End: (%.2f, %.2f, %.2f) | Direction: (%.2f, %.2f, %.2f) | Length: %d",
                this.start.getX(), this.start.getY(), this.start.getZ(),
                this.getEnd().getX(), this.getEnd().getY(), this.getEnd().getZ(),
                this.direction.getX(), this.direction.getY(), this.direction.getZ(),
                this.length);
    }

    public VectorDirectionData getData() {
        return data;
    }
}
