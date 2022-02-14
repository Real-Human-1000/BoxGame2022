public class TerrainController {

    int width, height;
    private FluidField ffield;
    private double[][] terrain;

    public TerrainController(int w, int h) {
        this.width = w;
        this.height = h;
        this.ffield = new FluidField(h, w);  // Jack tells me to keep this a square

        // worleyTerrain();  // Good for islands
        polyTerrain();  // Good for one river
        snazzyDisplay();
    }

    public void worleyTerrain() {
        // Fills terrain with random doubles as a starting point for the simulation
        // Uses Worley Noise -- generates lots of random points and assigns random values based on distance to the nearest
        terrain = new double[height][width];

        double[][] points = new double[(int)Math.sqrt(height*height + width*width)][2]; // (int)Math.sqrt(height*width)

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
    }

    public void polyTerrain() {
        // Fills terrain with random doubles as a starting point for the simulation
        // Uses noise generated from Voronoi polygons. Basically Worley noise again but cooler and better for our purposes
        terrain = new double[height][width];

        double[][] points = new double[100][3];

        for (int p = 0; p < points.length; p++) {
            double h = Math.random()*height;
            points[p] = new double[] {Math.random() * width, h, 1 - h/height + (Math.random() - 0.5)/4};
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double distdist = width*width + height*height;
                int closestIndex = 0;

                for (int p = 0; p < points.length; p++) {
                    double thisdist = (Math.pow(x - points[p][0], 2) + Math.pow(y - points[p][1], 2)) * (Math.random()/4 + 0.875);
                    if (thisdist < distdist) {
                        distdist = thisdist;
                        closestIndex = p;
                    }
                }

                terrain[y][x] = Math.max(Math.min(points[closestIndex][2] + (Math.random() - 0.5)/16, 1), 0);
            }
        }

        for (int r = 0; r < 3; r++) {
            int[] pos = {(int)(Math.random()*width), (int)(Math.random()*height/2)};

        }

    }

    public void update() {
        // Updates itself and the fluid field
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Change terrain heights
                double speed = Math.sqrt(Math.pow(ffield.getVx(x, y), 2) + Math.pow(ffield.getVy(x, y), 2));
                // Min speed is 0, max speed is (realistically) 5
                // Sqrt isn't efficient, but it makes the deltaTerrain algorithm simpler

                // double amount = ffield.getDensity(x, y);
                // Maybe I'll use amount to calculate erosion later

                double deltaTerrain = -1 * Math.pow(speed - 2.5, 3) / 500;

                terrain[y][x] = terrain[y][x] + deltaTerrain;

                // Change wall status
                ffield.setWall(x, y, terrain[y][x] > 0.4);  // "sea level"
                ffield.setVx(x, y, ffield.getVx(x, y) - terrain[y][x] * ffield.getVx(x, y));
                ffield.setVy(x, y, ffield.getVy(x, y) - terrain[y][x] * ffield.getVy(x, y));
            }
        }
    }

    public void stepAndUpdate() {
        // Steps fluid field and updates terrain
        ffield.step();
        update();
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
        // Displays terrain in ASCII art
        // Useful for when Ravi is taking forever to get some graphics thing working
        String asciiGrays = " .:-=+*#%@";
        String blockShades = " ░▒▓";

        for (double[] row : terrain) {
            for (double col : row) {
                for (int i = 0; i < 3; i++) {
                    System.out.print(blockShades.charAt(Math.min((int) (col * (blockShades.length())), blockShades.length()-1)));
                }
            }
            System.out.println();
        }
    }
}
