public class WaterSource {
    private int x;
    private int y;
    private double density;
    private double vx;
    private double vy;
    public WaterSource(int x, int y, double density, double vx, double vy){
        this.x = x;
        this.y = y;
        this.density = density;
        this.vx = vx;
        this.vy = vy;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public double getDensity() {
        return density;
    }
    public void setDensity(double density) {
        this.density = density;
    }
    public double getVx() {
        return vx;
    }
    public void setVx(double vx) {
        this.vx = vx;
    }
    public double getVy() {
        return vy;
    }
    public void setVy(double vy) {
        this.vy = vy;
    }
}
