import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class FluidField {
    private double[][] density;
    private double[][] vx;
    private double[][] vy;

    private double[][] density0;
    private double[][] vx0;
    private double[][] vy0;

    private double[][] earthDensity;
    private double[][] earthDensity0;

    private Boolean[][] walls;

    private int h;
    private int w;
    private int size = 2;

    private double dt = 0.5;
    private double diff = 0.99;
    private double visc = 0.1;
    private double diffE = 0.5;
    private int N;

    private double t;

    private TerrainController terrainController;

    private ArrayList<WaterSource> sources;

    public FluidField(int w, int h, TerrainController terrainController) {
        density = new double[h][w];
        vx = new double[h][w];
        vy = new double[h][w];
        earthDensity = new double[h][w];

        density0 = new double[h][w];
        vx0 = new double[h][w];
        vy0 = new double[h][w];
        earthDensity0 = new double[h][w];

        walls = new Boolean[h][w];

        this.terrainController = terrainController;

        this.h = h;
        this.w = w;
        N = h;

        t = 0;
        sources = new ArrayList<>();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                density[j][i] = 0.0;
                vx[j][i] = 0;
                vy[j][i] = 0;
                walls[j][i] = false;
                earthDensity[j][i] = 0.0;
            }
        }
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                density0[j][i] = density[j][i];
                vx0[j][i] = vx[j][i];
                vy0[j][i] = vy[j][i];
                earthDensity0[j][i] = earthDensity[j][i];
            }
        }

    }

    public void step() {
        for (int i = 0; i < sources.size(); i++) {
            int x = sources.get(i).getX();
            int y = sources.get(i).getY();
            setAVG(density, sources.get(i).getDensity(), x, y);
            setAVG(earthDensity, sources.get(i).getEarthDensity(), x, y);
            setAVG(vx, sources.get(i).getVx(), x, y);
            setAVG(vy, sources.get(i).getVy(), x, y);
            //System.out.println(sources.get(i).getX()+"\t"+sources.get(i).getY());
        }

        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                vx[j][i] = terrainController.getSlope(i / 2, j / 2)[0];
                vy[j][i] = terrainController.getSlope(i / 2, j / 2)[1];
            }
        }
        dens_step(N, density, density0, vx, vy, diff, dt);
        dens_step(N, earthDensity, earthDensity0, vx, vy, diffE, dt);
    }

    //diffuses some density from the 8 cells around to one cell
    public void diffuse(int N, int b, double[][] x, double[][] x0, double diff, double dt) {
        int i, j, k;
        double a = dt * diff * N * N;
        for (k = 0; k < 20; k++) {
            for (i = 0; i < N - 1; i++) {
                for (j = 0; j < N - 1; j++) {
                    if (!walls[j][i]) {
                        double add = 0;
                        int numAdd = 0;
                        if (israel(i - 1, j)) {
                            if (!walls[j][i - 1]) {
                                numAdd++;
                                add += x0[j][i - 1];
                            }
                        }
                        if (israel(i + 1, j)) {
                            if (!walls[j][i + 1]) {
                                numAdd++;
                                add += x0[j][i + 1];
                            }
                        }
                        if (israel(i, j - 1)) {
                            if (!walls[j - 1][i]) {
                                numAdd++;
                                add += x0[j - 1][i];
                            }
                        }
                        if (israel(i, j + 1)) {
                            if (!walls[j + 1][i]) {
                                numAdd++;
                                add += x0[j + 1][i];
                            }
                        }

                        if (israel(i + 1, j + 1)) {
                            if (!walls[j + 1][i + 1]) {
                                numAdd++;
                                add += x0[j + 1][i + 1];
                            }
                        }
                        if (israel(i - 1, j + 1)) {
                            if (!walls[j + 1][i - 1]) {
                                numAdd++;
                                add += x0[j + 1][i - 1];
                            }
                        }
                        if (israel(i - 1, j - 1)) {
                            if (!walls[j - 1][i - 1]) {
                                numAdd++;
                                add += x0[j - 1][i - 1];
                            }
                        }
                        if (israel(i + 1, j - 1)) {
                            if (!walls[j - 1][i + 1]) {
                                numAdd++;
                                add += x0[j - 1][i + 1];
                            }
                        }
                        x[j][i] = (x0[j][i] + a * 1 * add) / (1 + numAdd * a);
                    }
                }
            }
            //set_bnd ( N, b, x );
            //set_wall(N, b, x);
        }
    }

    //    public void advect(int N, int b, double[][] d, double[][] d0, double[][] vx, double[][] vy, double dt){
//        int i, j, i0, j0, i1, j1;
//        double x, y, s0, t0, s1, t1, dt0;
//        dt0 = dt*N;
//        for ( i=1 ; i<N ; i++ ) {
//            for ( j=1 ; j<N ; j++ ) {
//                x = i-dt0*vx[j][i]; y = j-dt0*vy[j][i];
//                i0=(int)x; i1=i0+1;
//                j0=(int)y; j1=j0+1;
//                s1 = x-i0; s0 = 1-s1; t1 = y-j0; t0 = 1-t1;
//                double add1 = 0;
//                if(israel(i0,j0)) add1+=t0*d0[j0][i0];
//                if(israel(i0,j1)) add1+=t1*d0[j1][i0];
//                add1*=s0;
//                double add2 = 0;
//                if(israel(i1,j0)) add2+=t0*d0[j0][i1];
//                if(israel(i1,j1)) add2+=t1*d0[j1][i1];
//                add2*=s1;
//                d[j][i] = add1 + add2;
//            }
//        }
//        set_bnd ( N, b, d );
//        set_wall(N, b, d);
//    }

    //moves part of the density of a cell to another cell based on velocity at the cell
    public void move(double[][] x, double[][] x0, double[][] vx, double[][] vy, double dt) {
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                x[j][i] = 0;
                int nx = i;
                if (vx[j][i] > 0) nx += 1;
                else nx -= 1;
                int ny = j;
                if (vy[j][i] > 0) ny += 1;
                else ny -= 1;
                if (israel(nx, ny) && israel(i, j)) {
                    if (!walls[ny][nx]) {
                        x[ny][nx] += x0[j][i] * dt * visc;
                        //x0[j][i] -= x0[j][i] * dt;
                    }
                }
                x[j][i] += x0[j][i];
                x[j][i] = Math.min(1, x[j][i]);
            }
        }
    }


    public void dens_step(int N, double[][] x, double[][] x0, double[][] u, double[][] v, double diff, double dt) {
        swap(x0, x);
        diffuse(N, 0, x, x0, diff, dt);
        swap(x0, x);
        move(x, x0, vx, vy, dt);
    }

    public void set_bnd(int N, int b, double[][] x) {
        int i;
        //N=N-2;
        for (i = 1; i <= N; i++) {
            if ((b == 1)) {
                if (israel(0, i) && israel(1, i))
                    x[i][0] = -x[i][1];
            } else {
                if (israel(0, i) && israel(1, i))
                    x[i][0] = x[i][1];
            }
            if ((b == 1)) {
                if (israel(i, N + 1) && israel(i, N))
                    x[i][N + 1] = -x[i][N];
            } else {
                if (israel(i, N + 1) && israel(i, N))
                    x[i][N + 1] = x[i][N];
            }
            if ((b == 2)) {
                if (israel(i, 0) && israel(i, 1))
                    x[0][i] = -x[1][i];
            } else {
                if (israel(i, 0) && israel(i, 1))
                    x[0][i] = x[1][i];
            }
            if ((b == 2)) {
                if (israel(i, 0) && israel(i, N))
                    x[0][i] = -x[N][i];
            } else {
                if (israel(i, 0) && israel(i, N))
                    x[0][i] = x[N][i];
            }

        }
        if (israel(0, 0) && israel(1, 0) && israel(0, 1))
            x[0][0] = 0.5 * (x[0][1] + x[1][0]);
        if (israel(0, N + 1) && israel(1, N + 1) && israel(0, N))
            x[N + 1][0] = 0.5 * (x[N + 1][1] + x[N][0]);
        if (israel(N + 1, 0) && israel(N, 0) && israel(N + 1, 1))
            x[0][N + 1] = 0.5 * (x[0][N] + x[1][N + 1]);
        if (israel(N + 1, N + 1) && israel(N, N + 1) && israel(N + 1, N))
            x[N + 1][N + 1] = 0.5 * (x[N + 1][N] + x[N][N + 1]);
    }

    public void set_wall(int N, int b, double[][] x) {
        int i, j;
        //N=N-4;
        for (i = 1; i < N - 1; i++) {
            for (j = 1; j < N - 1; j++) {
                if ((b == 1) && walls[j][i]) {
                    if (israel(i, j) && israel(i + 1, j))
                        x[j][i] = -x[j][i + 1];
                } else {
                    if (israel(i, j) && israel(i, j))
                        x[j][i] = x[j][i];
                }
                if ((b == 1) && walls[j][i]) {
                    if (israel(i + 1, j) && israel(i, j))
                        x[j][i + 1] = -x[j][i];
                } else {
                    if (israel(i, j) && israel(i, j))
                        x[j][i] = x[j][i];
                }
                if ((b == 2) && walls[j][i]) {
                    if (israel(i, j) && israel(i, j + 1))
                        x[j][i] = -x[j + 1][i];
                } else {
                    if (israel(i, j) && israel(i, j))
                        x[j][i] = x[j][i];
                }
                if ((b == 2) && walls[j][i]) {
                    if (israel(i, j) && israel(i, j - 1))
                        x[j][i] = -x[j - 1][i];
                } else {
                    if (israel(i, j) && israel(i, j))
                        x[j][i] = x[j][i];
                }
            }
        }
    }

    public void swap(double[][] x, double[][] x0) {
        double hold;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                hold = x[j][i];
                x[j][i] = x0[j][i];
                x0[j][i] = hold;
            }
        }
    }

    //checks if the given coordinates exist in the arrays
    public boolean israel(int i, int j) {
        if (i < w && i >= 0 && j < h && j >= 0) {
            return true;
        }
        return false;
    }

    //gets the average value of a tile for converting from FluidField's grid to the outside world
    public double IXAVG(double[][] d, int x, int y) {
        int numTiles = 4;
        if (x * size == w - 1) {
            numTiles -= 1;
        } // Left or right side
        if (y * size == h - 1) {
            numTiles -= 1;
        } // Top or bottom

        double v = 0;
        v += d[y * size][x * size];
        if (x * size < w - 2) {
            v += d[y * size][x * size + 1];
        }
        if (x * size < w - 2 && y * size < h - 2) {
            v += d[y * size + 1][x * size + 1];
        }
        if (y * size < h - 2) {
            v += d[y * size + 1][x * size];
        }
        return v / numTiles;
    }

    //sets the FluidField tiles in one cell
    public void setAVG(double[][] d, double set, int x, int y) {
        if (israel(x * size, y * size))
            d[y * size][x * size] = set;
        if (x * size < w - 2) {
            d[y * size][x * size + 1] = set;
        }
        if (x * size < w - 2 && y * size < h - 2) {
            d[y * size + 1][x * size] = set;
        }
        if (y * size < h - 2) {
            d[y * size + 1][x * size] = set;
        }
    }

    //same as setAVG but for Boolean values
    public void setAVGB(Boolean[][] d, boolean set, int x, int y) {
        if (israel(x * size, y * size))
            d[y * size][x * size] = set;
        if (x * size < w - 2) {
            d[y * size][x * size + 1] = set;
        }
        if (x * size < w - 2 && y * size < h - 2) {
            d[y * size + 1][x * size + 1] = set;
        }
        if (y * size < h - 2) {
            d[y * size + 1][x * size] = set;
        }
    }

    public int getH() {
        return h;
    }

    public int getW() {
        return w;
    }

    public double[][] getDensityArr() {
        return density;
    }

    public double[][] getVxArr() {
        return vx;
    }

    public double[][] getVyArr() {
        return vy;
    }

    public Boolean[][] getWalls() {
        return walls;
    }

    public Boolean getWall(int i, int j) {
        return walls[j][i];
    }

    public double getDensity(int i, int j) {
        return IXAVG(density, i, j);
    }

    public double getVx(int i, int j) {
        return IXAVG(vx, i, j);
    }

    public double getVy(int i, int j) {
        return IXAVG(vy, i, j);
    }

    public void setWall(int i, int j, boolean b) {
        setAVGB(walls, b, i, j);
    }

    public void setDensity(int i, int j, double d) {
        setAVG(density, d, i, j);
    }

    public void setVx(int i, int j, double v) {
        setAVG(vx, v, i, j);
    }

    public void setVy(int i, int j, double v) {
        setAVG(vy, v, i, j);
    }

    public void setEarthDensity(int i, int j, double d) {
        setAVG(earthDensity, d, i, j);
    }

    public double getEarthDensity(int i, int j) {
        return IXAVG(earthDensity, i, j);
    }

    //adds a source or edits a source if one already exists at given coords
    public void addSource(int x, int y, double density, double earthDensity, double vx, double vy) {
        boolean found = false;
        for (int i = 0; i < sources.size(); i++) {
            if (sources.get(i).getX() == x && sources.get(i).getY() == y) {
                sources.get(i).setDensity(density);
                sources.get(i).setVx(vx);
                sources.get(i).setVy(vy);
                sources.get(i).setEarthDensity(earthDensity);
                found = true;
            }
        }
        if (!found) {
            sources.add(new WaterSource(x, y, density, earthDensity, vx, vy));
        }
    }

    public WaterSource getSource(int i) {
        return sources.get(i);
    }

    public ArrayList<WaterSource> getSources() {
        return sources;
    }

    //removes the WaterSource at give coords if there is one
    public void removeSource(int x, int y) {
        for (int i = 0; i < sources.size(); i++) {
            if (sources.get(i).getX() == x && sources.get(i).getY() == y) {
                sources.remove(i);
                break;
            }
        }
    }
}
