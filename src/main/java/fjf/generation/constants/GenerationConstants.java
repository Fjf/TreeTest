package fjf.generation.constants;

public class GenerationConstants {
    public static class levels {
        public static final int WATER = 72;
        public static final int MIN_SAND = 71;
        public static final int MAX_SAND = 73;
        public static final int MIN_SNOW = 105;
        public static final int MIN_MOUNTAINTOP = 85;
    }

    public static class world {
        // How aggressively the generator will average towards water level.
        // Minimum value of 1.
        public static final float SKEW = 7f;
    }
}

