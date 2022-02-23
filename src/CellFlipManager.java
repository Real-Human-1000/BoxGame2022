import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class CellFlipManager{
    int x,y,imageWidth,imageHeight,lastImageIndex;
    Cell myCell;
    Graphics g;
    double waterLevel = 0.5;
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

    public void setG(Graphics g) {
        this.g = g;
    }

    public void setWaterLevel(double wl){waterLevel=wl;}

    //@Override
    public void updateAnim(double dt,boolean pMode) {
        drawSelf(pMode);
        myCycles += dt/4.0;
        if(myCycles>20) {
            myCell.setColorChanged(false);
            //System.out.println("done");
        }
    }


    public void drawSelf(boolean pMode){
        Graphics2D g2 = (Graphics2D)g;

        //g2.setColor(Color.BLACK);
        //g2.fillRect(x, y, Cell.CELL_SIZE, Cell.CELL_SIZE);

        //int scaleFactor = Math.abs(10-myCycles);
        Image scaledImage;
        Color drawColor = myCell.getMyColor();
        if(myCycles<10){
            drawColor = myColor;
            //scaledImage = myCell.getScaledImageAtIndex(lastImageIndex,scaleFactor);
        }else{
            //drawColor = myCell.getMyColor();
            //scaledImage = myCell.getMyScaledImage(scaleFactor);//.getScaledInstance(imageWidth,(int)(imageHeight*scaleFactor),Image.SCALE_DEFAULT);
        }

        //AffineTransform graphicsTransform = g2.getTransform();



        //int scaledHeight = Math.min(scaledImage.getHeight(null),Cell.CELL_SIZE);
        double scaleFac = Math.sin((Math.abs(10.0-myCycles) * Math.PI) / 20.0);

        int scaledHeight = (int)(Cell.CELL_SIZE*scaleFac);
       // System.out.println(sh);

        //if(myCycles!=10) {
//            BufferedImage brightAdjust = new BufferedImage(Cell.CELL_SIZE,scaledHeight,BufferedImage.TYPE_INT_RGB);
//
//            Graphics2D brightAdjustGraphics = brightAdjust.createGraphics();
//            brightAdjustGraphics.drawImage(scaledImage,x,y,Cell.CELL_SIZE,scaledHeight,null);
//            brightAdjustGraphics.dispose();
//
//            RescaleOp op = new RescaleOp((float)waterLevel*2.0f, 0, null);
//            brightAdjust = op.filter(brightAdjust,null);
//
//            g2.drawImage(brightAdjust, x, y + Cell.CELL_SIZE / 2 - scaledHeight / 2,
//                    Cell.CELL_SIZE, scaledHeight, null);

            //g2.drawImage(scaledImage, x, y + Cell.CELL_SIZE / 2 - scaledHeight / 2,
              //      Cell.CELL_SIZE, scaledHeight, null);

            g2.setColor(drawColor);

            if (pMode==false) {
                g2.fillRoundRect(x,y + Cell.CELL_SIZE/2 - scaledHeight/2,Cell.CELL_SIZE-1,scaledHeight-1,1,1);
                /*g2.translate(x, y + Cell.CELL_SIZE / 2 - scaledHeight / 2);
                Shape scg = new ShadedCellGraphics(Cell.CELL_SIZE, scaledHeight);
                g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(drawColor.brighter());
                g2.draw(scg);
                g2.setColor(drawColor.darker());
                g2.transform(AffineTransform.getRotateInstance(Math.toRadians(180),
                        ((double) Cell.CELL_SIZE - 1) / 2.0, ((double) Cell.CELL_SIZE - 1) / 2.0));
                g2.draw(scg);
                g2.setTransform(graphicsTransform);*/
            }else{
                g2.fillRect(x,y + Cell.CELL_SIZE/2 - scaledHeight/2,Cell.CELL_SIZE-1,scaledHeight-1);
            }




        //}
    }
}
