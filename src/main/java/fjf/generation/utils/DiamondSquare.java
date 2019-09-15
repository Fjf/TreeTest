package fjf.generation.utils;

import com.sun.javaws.exceptions.InvalidArgumentException;
import com.sun.org.apache.xerces.internal.impl.xpath.XPath;
import fjf.generation.Generation;
import fjf.generation.constants.GenerationConstants;
import org.bukkit.World;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.Random;

public class DiamondSquare {
    private final int averageHeight;
    private final int biasHeight;
    private double[][] heightMap;
    private final float randomHeightOffset;
    private final int chunkX;
    private final int chunkY;

    private final int originalDimension;
    private final int totalAreaMultiplication;


    public DiamondSquare(ChunkHeightData chunkHeightData, int dimension, int x, int y, int averageHeight, int biasHeight) {
        this.totalAreaMultiplication = 2;
        this.randomHeightOffset = 22f;
        this.averageHeight = averageHeight;
        this.biasHeight = biasHeight;

        int dim = (dimension - 1);
        this.heightMap = new double[dim * totalAreaMultiplication * 3 + 1][dim * totalAreaMultiplication * 3 + 1];
        this.originalDimension = dimension;
        x = (x < 0) ? (x - 1) : x;
        y = (y < 0) ? (y - 1) : y;
        this.chunkX = x;
        this.chunkY = y;

        dim *= totalAreaMultiplication;

        // Initialize the required corners.
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                double noise = chunkHeightData.getHeight(
                        (x / totalAreaMultiplication) + i - 1,
                        (y / totalAreaMultiplication) + j - 1);
                System.out.println(noise);
                heightMap[i * dim][j * dim] = noise * averageHeight + averageHeight;
            }
        }

        DSDiamond(dim);
    }

    public double[][] getHeightMap(int chunkX, int chunkY) {
        double[][] data = new double[16][16];

        if (chunkX < 0) {
            chunkX = Math.abs(chunkX);
        }
        if (chunkY < 0) {
            chunkY = Math.abs(chunkY);
        }

        int xOffset = (this.originalDimension) * totalAreaMultiplication + (chunkX % totalAreaMultiplication) * 16;
        int yOffset = (this.originalDimension) * totalAreaMultiplication + (chunkY % totalAreaMultiplication) * 16;


        for (int i = 0; i < 16; i++) {
            System.arraycopy(heightMap[i + xOffset], yOffset, data[i], 0, 16);
        }
        return data;
    }

    public void smoothen(int radius) {
        int startValue = - radius / 2;
        int endValue = radius + startValue;
        int offset = 16 * totalAreaMultiplication;

        // To store all values for next iteration.
        double[][] mapCopy = new double[heightMap.length][heightMap.length];

        for (int i = - startValue; i < heightMap.length + startValue - 1; i++) {
            for (int j = - startValue; j < heightMap.length + startValue - 1; j++) {
                float sum = 0;
                int n = 0;
                for (int ii = startValue; ii < endValue; ii++) {
                    for (int jj = startValue; jj < endValue; jj++) {
                        if (heightMap[i + ii][j + jj] == 0)
                            continue;
                        sum += heightMap[i + ii][j + jj];
                        n++;
                    }
                }
                mapCopy[i][j] = sum / n;
            }
        }
        this.heightMap = mapCopy;
    }

    private void DSDiamond(int dimension) {
        /*
         *   O - - - O
         *   - - - - -
         *   - - X - -
         *   - - - - -
         *   O - - - O
         */
        if (dimension < 2) {
            return;
        }
        for (int i = 0; i < heightMap.length - 1; i += dimension) {
            for (int j = 0; j < heightMap.length - 1; j += dimension) {
                if (i + dimension > heightMap.length - 1 || j + dimension > heightMap.length - 1)
                    continue; // Skip all array values which are out of range.

                double p1 = heightMap[i][j];
                double p2 = heightMap[i + dimension][j];
                double p3 = heightMap[i][j + dimension];
                double p4 = heightMap[i + dimension][j + dimension];

                // Dont allow values to average with a zero value.
                if (p1 == 0 || p2 == 0 || p3 == 0 || p4 == 0)
                    continue;

                float randomOffset = generateRandom(dimension, i + dimension / 2, j + dimension / 2);

                double biasedAvg = calcBiasedAvg(p1, p2, p3, p4, randomOffset);

                heightMap[dimension / 2 + i][dimension / 2 + j] = biasedAvg;
            }
        }

        DSSquare(dimension / 2);
    }

    private float generateRandom(int dimension, int x, int y) {
        float currentHeightOffset = randomHeightOffset / ((float) heightMap.length / (2 * dimension));
        float randomOffset = 0.0f;

        int trueX = x + (this.chunkX / totalAreaMultiplication) * 16 * totalAreaMultiplication - (this.originalDimension - 1) * totalAreaMultiplication;
        int trueY = y + (this.chunkY / totalAreaMultiplication) * 16 * totalAreaMultiplication - (this.originalDimension - 1) * totalAreaMultiplication;

        if (randomHeightOffset > 0.5) {
//            long seed = (long)(trueX) << 32 |
//                    (trueY) & 0xFFFFFFFFL;

            randomOffset = currentHeightOffset * (new Random(trueX * trueY).nextFloat() - 0.5f);
        }
        return randomOffset;
    }

    private void DSSquare(int dimension) {
        /*
         *   O - X - O
         *   - - - - -
         *   X - O - X
         *   - - - - -
         *   O - X - O
         */

        for (int i = 0; i < heightMap.length; i += dimension) {
            for (int j = 0; j < heightMap.length; j += dimension) {
                int ii = i / dimension;
                int jj = j / dimension;

                if (i + dimension > heightMap.length - 1 || i - dimension < 0 || j + dimension > heightMap.length - 1 || j - dimension < 0)
                    continue; // Skip all array values which are out of range.

                if ((ii % 2 == 0 && jj % 2 == 1) || (ii % 2 == 1 && jj % 2 == 0)) { // Uneven amount of values to set

                    double p1 = heightMap[i + dimension][j];
                    double p2 = heightMap[i - dimension][j];
                    double p3 = heightMap[i][j + dimension];
                    double p4 = heightMap[i][j - dimension];

                    // Dont allow values to average with a zero value.
                    if (p1 == 0 || p2 == 0 || p3 == 0 || p4 == 0)
                        continue;

                    float randomOffset = generateRandom(dimension, i, j);

                    double biasedAvg = calcBiasedAvg(p1, p2, p3, p4, randomOffset);

                    heightMap[i][j] = biasedAvg;
                }
            }
        }

        DSDiamond(dimension);
    }

    private double calcAvg(double p1, double p2, double p3, double p4, double randomOffset) {
        return (p1 + p2 + p3 + p4) / 4 + randomOffset;
    }

    private double calcBiasedAvg(double p1, double p2, double p3, double p4, double randomOffset) {
        float skew = GenerationConstants.world.SKEW;
        double[] weights = new double[]{
                1.0 / (Math.abs(p1 - biasHeight) + skew),
                1.0 / (Math.abs(p2 - biasHeight) + skew),
                1.0 / (Math.abs(p3 - biasHeight) + skew),
                1.0 / (Math.abs(p4 - biasHeight) + skew)};
        return (weights[0] * p1 + weights[1] * p2 + weights[2] * p3 + weights[3] * p4) /
                (weights[0] + weights[1] + weights[2] + weights[3]) +
                randomOffset;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (double[] doubles : this.heightMap) {
            for (int j = 0; j < this.heightMap.length; j++) {
                out.append(String.format("%03d ", (int) doubles[j]));
            }
            out.append("\n");
        }
        return out.toString();
    }
}
