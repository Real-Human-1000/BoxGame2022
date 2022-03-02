import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class CellFlipManager{
    int x,y,imageWidth,imageHeight,lastImageIndex;
    Cell myCell;
    Color myColor;
    double myCycles;

    public CellFlipManager(){

    }

    public CellFlipManager(Cell setCell, Color setColor){
        x = setCell.getX();
        y = setCell.getY();
        myCell = setCell;
        myCycles = 0;
        //this.lastImageIndex = lastImageIndex;
        myColor = setColor;
    }


    //@Override
    public void updateAnim(Graphics g) {
        drawSelf(g);
        myCycles += GridDemoPanel.deltaTime/4.0;
        if(myCycles>20) {
            myCell.setColorChanged(false);
            if(!GridDemoPanel.performanceMode)
            myCell.createDrip();


            //System.out.println("done");
        }
    }


    public void drawSelf(Graphics g){
        Graphics2D g2 = (Graphics2D)g;

        //g2.setColor(Color.BLACK);
        //g2.fillRect(x, y, Cell.CELL_SIZE, Cell.CELL_SIZE);

        Color drawColor = myCell.getMyColor();
        if(myCycles<10)
            drawColor = myColor;



        //int scaledHeight = Math.min(scaledImage.getHeight(null),Cell.CELL_SIZE);
        double scaleFac = Math.sin((Math.abs(10.0-myCycles) * Math.PI) / 20.0);

        int scaledHeight = (int)(Cell.CELL_SIZE*scaleFac);

            g2.setColor(drawColor);

            if (!GridDemoPanel.performanceMode) {
                g2.fillRoundRect(x,y + Cell.CELL_SIZE/2 - scaledHeight/2,Cell.CELL_SIZE-1,scaledHeight-1,1,1);
            }else{
                g2.fillRect(x,y + Cell.CELL_SIZE/2 - scaledHeight/2,Cell.CELL_SIZE-1,scaledHeight-1);
            }




        //}
    }
}
