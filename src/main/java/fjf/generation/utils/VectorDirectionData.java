package fjf.generation.utils;

import java.util.Vector;

public class VectorDirectionData {
    private double theta;
    private double phi;
    private int length;

    public double getTheta() {
        return theta;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }

    public double getPhi() {
        return phi;
    }

    public void setPhi(double phi) {
        this.phi = phi;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public VectorDirectionData(double theta, double phi, int length) {
        this.theta = theta;
        this.phi = phi;
        this.length = length;
    }
}
