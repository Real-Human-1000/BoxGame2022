public class TerrainController {

    int width, height;
    private FluidField ffield;
    private double[][] terrain;
    private double seaLevel;

    public TerrainController(int w, int h) {
        this.width = w;
        this.height = h;
        this.seaLevel = 0.3;
        this.ffield = new FluidField(h*2, w*2);  // Jack tells me to keep this a square

        //worleyTerrain();  // For islands. Not great, but it's what we got
        polyTerrain();  // Good for rivers
        for(int x=0; x<w; x++){
            for(int y=0; y<h; y++){
                ffield.setEarthDensity(x,y,terrain[y][x]);
            }
        }
        //snazzyDisplay();
    }

    public void worleyTerrain() {
        // Fills terrain with random doubles as a starting point for the simulation
        // Uses Worley Noise -- generates lots of random points and assigns random values based on distance to the nearest
        // Worley noise algorithm taken from an older project I did, which was inspired by the worley noise wikipedia page
        terrain = new double[height][width];

        // Create a bunch of points to define the terrain
        double[][] points = new double[(int)Math.sqrt(height*height + width*width)][2]; // (int)Math.sqrt(height*width)

        // Randomize the points' location
        for (double[] point : points) {
            point[0] = Math.random() * width;
            point[1] = Math.random() * height;
        }

        // For each location in the array
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Each spot gets a value dependent on the distance to the earest point

                double distdist = Double.MAX_VALUE;

                for (double[] point : points) {
                    double thisdist = Math.pow(x - point[0], 2) + Math.pow(y - point[1], 2);
                    if (thisdist < distdist)
                        distdist = thisdist;
                }

                if (distdist < 1)
                    distdist = distdist + 1;

                terrain[y][x] = Math.min(Math.pow(1 / distdist, 0.35), 1);
            }
        }

        for (int w = 0; w < 3; w++) {
            int x = (int)(Math.random()*width);
            int y = (int)(Math.random()*height);

            //ffield.addSource(x, y, 0.1, Math.random()*10-5, Math.random()*10-5);
        }
    }

    public void polyTerrain() {
        // Fills terrain with random doubles as a starting point for the simulation
        // Uses noise generated from Voronoi polygons. Basically Worley noise again but cooler and better for our purposes
        // Inspired by (but not taken from) http://www-cs-students.stanford.edu/~amitp/game-programming/polygon-map-generation/
        terrain = new double[height][width];

        // Make a bunch of points that will define the terrain
        double[][] points = new double[100][3];

        // Randomize said points
        for (int p = 0; p < points.length; p++) {
            double h = Math.random()*height;
            points[p] = new double[] {Math.random() * width, h, 1 - h/height + (Math.random() - 0.5)/4};
        }

        // For each position in the terrain array,
        // assign it a value based on the closest point (plus some random flair)
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
        for (int r = 0; r < 1; r++) {
            // Set starting point of river channel and add water source
            double[] pos = {Math.random()*width, Math.random()*height/2};
            ffield.addSource((int)pos[0], (int)pos[1], 0.1, 0.001, 1, 0);

            while (terrain[(int)pos[1]][(int)pos[0]] > seaLevel/2) {
                if (terrain[(int)pos[1]][(int)pos[0]] > seaLevel*0.75)
                    terrain[(int)pos[1]][(int)pos[0]] = seaLevel*0.75;

                // Position of river mouth moves randomly, with a downward bias
                // Vertical movement changes depending on terrain height
                // We can assume that the terrain flattens out as it gets closer to sea near the bottom of the screen
                // I mean we can't actually assume that, but it makes it look nicer
                pos[1] += (Math.random() + 1) * 0.5 * terrain[(int)pos[1]][(int)pos[0]];
                pos[0] += 1.1 * (Math.random() - 0.5);
                // pos[1] += Math.random()/5 + 0.15;

                // Make sure that the river doesn't go outside the terrain array
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
                // Min speed is 0, max speed is realistically 10, normal highest speed is 2

                // Total amount of sediment to deposit
                double deltaTerrain = 0;

                if (speed >= 1) {
                    // Consume
                    // dT ~ s
                    // dT ~ f
                    // dT ~ 1 - e
                    deltaTerrain = -1 * Math.min(speed * ffield.getDensity(x, y) * (1 - ffield.getEarthDensity(x, y)) / 100, terrain[y][x]);
                }
                if (speed < 1) {
                    // Release
                    // dT ~ 1 - s
                    // dT ~ f
                    // dT ~ e
                    deltaTerrain = Math.min((1 - speed) * ffield.getDensity(x, y) * ffield.getEarthDensity(x, y) / 100, ffield.getEarthDensity(x, y));
                }

                ffield.setEarthDensity(x, y, ffield.getEarthDensity(x, y) + deltaTerrain);

                // Split up sediment among neighboring tiles
                int numTiles = 5;
                if (x == 0 || x == width - 1) { numTiles -= 1; } // Left or right side
                if (y == 0 || y == height - 1) { numTiles -= 1; } // Top or bottom

//                terrain[y][x] += deltaTerrain/numTiles;
//                if (x > 0) { terrain[y][x-1] += deltaTerrain/numTiles; }
//                if (x < width-1) { terrain[y][x+1] += deltaTerrain/numTiles; }
//                if (y > 0) { terrain[y-1][x] += deltaTerrain/numTiles; }
//                if (y < height-1) { terrain[y+1][x] += deltaTerrain/numTiles; }
                terrain[y][x]=ffield.getEarthDensity(x,y);

                // Change wall status
//                ffield.setWall(x, y, terrain[y][x] > this.seaLevel);
//                ffield.setVx(x, y, ffield.getVx(x, y) - Math.min(terrain[y][x]*2,1) * ffield.getVx(x, y));
//                ffield.setVy(x, y, ffield.getVy(x, y) - Math.min(terrain[y][x]*2,1) * ffield.getVy(x, y));
            }
        }
    }

    public void stepAndUpdate() {
        // Steps fluid field and updates terrain
        ffield.step();
        //update();
    }

    public double getTerrainAt(int x, int y) {
        // Get the height of terrain at certain coordinates
        return terrain[x][y];
    }

    public double[][] getTerrain() {
        // Get the entire terrain 2D array
        return terrain;
    }

    public double getFluidAt(int x, int y) {
        // Get the density of fluid at certain coordinates
        return ffield.getDensity(x, y);
    }

    public double[][] getFluid() {
        // Get the entire density 1D array
        return ffield.getDensityArr();
    }

    public double getFluidEarthAt(int x, int y) {
        // Get the amount of suspended sediment at certain coordinates
        return ffield.getEarthDensity(x, y);
    }

    public double getSeaLevel() {
        // Get sea level
        return this.seaLevel;
    }

    public double[] getSlope(int x, int y) {
        // Estimate the slope at a tile
        double[] slope = new double[2];
        if (x == 0) {
            slope[0] = terrain[y][x+1] - terrain[y][x];
        } else if (x == width-1) {
            slope[0] = terrain[y][x] - terrain[y][x-1];
        } else {
            slope[0] = (terrain[y][x+1] - terrain[y][x-1]) / 2;
        }

        if (y == 0) {
            slope[1] = terrain[y+1][x] - terrain[y][x];
        } else if (y == height-1) {
            slope[1] = terrain[y][x] - terrain[y-1][x];
        } else {
            slope[1] = (terrain[y+1][x] - terrain[y-1][x]) / 2;
        }

        return slope;
    }

    public void addSource(int x, int y, double density, double earthDensity, double vx, double vy) {
        // Add water source
        ffield.addSource(x, y, density, earthDensity, vx, vy);
    }

    public void addTerrain(int x, int y, double amount) {
        // Add some terrain somewhere
        terrain[y][x] = Math.max(0, Math.min(terrain[y][x] + amount, 1));
    }

    public void addWater(int x, int y, double amount) {
        // Add some water -- I would not recommend using this, but whatevs
        ffield.setDensity(x, y, Math.max(0, Math.min(ffield.getDensity(x, y) + amount, 1)));
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
