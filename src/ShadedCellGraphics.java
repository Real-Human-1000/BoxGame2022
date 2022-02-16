import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;

public class ShadedCellGraphics extends Path2D.Double {
    public ShadedCellGraphics(int setW, int setH){
        double cellSize = (double)Cell.CELL_SIZE;

        double h = (double)setH;
        double w = (double)setW;

        moveTo(0,h-cellSize/8.0);
        append(new Arc2D.Double(0, h-cellSize/8.0, cellSize/8.0, cellSize/8.0, 180, 45, Arc2D.CHORD), false);
        lineTo(0, cellSize/8.0);
        curveTo(0, 0, 0, 0, cellSize/8.0, 0);
        lineTo(w - cellSize/8.0, 0);
        append(new Arc2D.Double(w-cellSize/8.0, 0, cellSize/8.0, cellSize/8.0, 90, -45, Arc2D.CHORD), false);
        //System.out.println("going");
    }




}
