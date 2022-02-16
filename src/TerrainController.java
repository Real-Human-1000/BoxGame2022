public class TerrainController {

    int width, height;
    private FluidField ffield;
    private double[][] terrain;

    public TerrainController(int w, int h) {
        this.width = w;
        this.height = h;
        this.ffield = new FluidField(h, w);  // Jack tells me to keep this a square

        // worleyTerrain();  // Good for islands
        polyTerrain();  // Good for rivers
        // snazzyDisplay();
    }

    public void worleyTerrain() {
        // Fills terrain with random doubles as a starting point for the simulation
        // Uses Worley Noise -- generates lots of random points and assigns random values based on distance to the nearest
        // Worley noise algorithm taken from an older project I did, which was inspired by the worley noise wikipedia page
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
        // Inspired by (but not taken from) http://www-cs-students.stanford.edu/~amitp/game-programming/polygon-map-generation/
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

        // Create some starting rivers
        for (int r = 0; r < 3; r++) {
            // Starting point of river channel
            double[] pos = {Math.random()*width, Math.random()*height/2};
            ffield.addSource((int)pos[0], (int)pos[1], 0.5, 0, 5); // Is positive going down?

            while (terrain[(int)pos[1]][(int)pos[0]] > 0.15) {
                if (terrain[(int)pos[1]][(int)pos[0]] > 0.2)
                    terrain[(int)pos[1]][(int)pos[0]] *= 0.75;

                // Position of river mouth moves randomly, with a downward bias
                pos[0] += 2 * (Math.random() - 0.5);
                pos[1] += Math.random()/5 + 0.15;

                if (pos[0] < 0) { pos[0] = 0; }
                if (pos[0] > width-1) { pos[0] = width-1; }
                if (pos[1] < 0) { pos[1] = 0; }
                if (pos[1] > height-1) { pos[1] = height-1; }
            }
        }
    }

    public void update() {
        // Updates itself and the fluid field
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Change terrain heights
                double speed = Math.sqrt(Math.pow(ffield.getVx(x, y), 2) + Math.pow(ffield.getVy(x, y), 2));
                // Min speed is 0, max speed is realistically 5, so we'll map to that range
                speed = Math.min(speed, 5);

                // Total amount of sediment to deposit
                double deltaTerrain = Math.min(-1 * Math.pow(speed - 2.5, 3) / 500, ffield.getEarthDensity(x, y));
                ffield.setEarthDensity(x, y, ffield.getEarthDensity(x, y) - deltaTerrain);

                // Split up sediment among neighboring tiles
                int numTiles = 5;
                if (x == 0 || x == width - 1) { numTiles -= 1; }
                if (y == 0 || y == height - 1) { numTiles -= 1; }

                terrain[y][x] += deltaTerrain/numTiles;
                if (x > 0) { terrain[y][x-1] += deltaTerrain/numTiles; }
                if (x < width-1) { terrain[y][x+1] += deltaTerrain/numTiles; }
                if (y > 0) { terrain[y-1][x] += deltaTerrain/numTiles; }
                if (y < height+1) { terrain[y+1][x] += deltaTerrain/numTiles; }


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
