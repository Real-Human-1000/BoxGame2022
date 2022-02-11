public class TerrainController {

    int width, height;
    private FluidField ffield;
    private double[][] terrain;
    private String asciiGrays = " .:-=+*#%@";
    private String blockShades = " ░▒▓";

    public TerrainController(int w, int h) {
        this.width = w;
        this.height = h;
        this.ffield = new FluidField(h, w);  // Jack tells me to keep this a square

        // You could call generateTerrain() in here, but I thought I'd keep it separate for now
    }

    public void generateTerrain() {
        // Fills terrain with random doubles as a starting point for the simulation
        // Uses Worley Noise for now
        terrain = new double[height][width];

        double[][] points = new double[10][2];

        for (double[] point : points) {
            point[0] = Math.random() * width;
            point[1] = Math.random() * height;
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                double distdist = Double.MAX_VALUE;

                for (double[] point : points) {
                    double thisdist = Math.pow(x - point[0], 2) + Math.pow(y - point[1], 2);
                    if (thisdist < distdist)
                        distdist = thisdist;
                }

                if (distdist < 1)
                    distdist = distdist + 1;

                terrain[y][x] = 1 / distdist;
            }
        }

        printArray(terrain);
        snazzyDisplay();
    }

    public double getTerrainAt(int x, int y) {
        return terrain[x][y];
    }

    public double[][] getTerrain() {
        return terrain;
    }

    public double getFluidAt(int x, int y) {
        return ffield.getDensity(x, y);
    }

    public double[] getFluid() {
        return ffield.getDensityArr();
    }

    public void printArray(double[][] arr) {
        // Prints a 2D array of doubles
        for (double[] row : arr) {
            for (double col : row) {
                System.out.print(Math.floor(col*10)/10 + ", ");
            }
            System.out.println();
        }
    }

    public void snazzyDisplay() {
        for (double[] row : terrain) {
            for (double col : row) {
                for (int i = 0; i < 3; i++) {
                    System.out.print(asciiGrays.charAt((int) (col * (asciiGrays.length()))));
                }
            }
            System.out.println();
        }
    }
}
