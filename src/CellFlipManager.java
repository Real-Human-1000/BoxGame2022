import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class CellFlipManager extends Thread{
    int x,y, myCycles,imageWidth,imageHeight,lastImageIndex;
    Cell myCell;
    Graphics g;
    double waterLevel = 0.5;
    Color myColor;

    public CellFlipManager(){

    }

    public CellFlipManager(Cell setCell, Color setColor){
        x = setCell.getX();
        y = setCell.getY();
        myCell = setCell;
        myCycles = 1;
        //this.lastImageIndex = lastImageIndex;
        myColor = setColor;
    }

    public void setG(Graphics g) {
        this.g = g;
    }

    public void setWaterLevel(double wl){waterLevel=wl;}

    //@Override
    public void updateAnim() {
        drawSelf();
        myCycles++;
        if(myCycles>=20) {
            myCell.setColorChanged(false);
            CellFlipManager.currentThread().interrupt();
        }
    }


    public void drawSelf(){
        Graphics2D g2 = (Graphics2D)g;

        g2.setColor(Color.BLACK);
        //g2.fillRect(x, y, Cell.CELL_SIZE, Cell.CELL_SIZE);

        int scaleFactor = Math.abs(10-myCycles);
        Image scaledImage;
        Color drawColor;
        if(myCycles<10){
            drawColor = myColor;
            scaledImage = myCell.getScaledImageAtIndex(lastImageIndex,scaleFactor);
        }else{
            drawColor = myCell.getMyColor();
            scaledImage = myCell.getMyScaledImage(scaleFactor);//.getScaledInstance(imageWidth,(int)(imageHeight*scaleFactor),Image.SCALE_DEFAULT);
        }




        //int scaledHeight = Math.min(scaledImage.getHeight(null),Cell.CELL_SIZE);
        double sh = ((double)Cell.CELL_SIZE * Math.sin((Math.abs(10.0-myCycles)) * Math.PI / 18.0));

        int scaledHeight = (int)((double)Cell.CELL_SIZE * Math.sin((double)(myCycles-1) * Math.PI / 18.0));
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
            //g2.fillRect(x,y,Cell.CELL_SIZE,Cell.CELL_SIZE);
            g2.fillRect(x,y+ Cell.CELL_SIZE / 2 - (int)sh / 2,Cell.CELL_SIZE,(int)sh);


        //}
        /*
        g2.setColor(new Color(192,192,192));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(x+1, y+1, Cell.CELL_SIZE-4, Cell.CELL_SIZE-4, 8, 8);

        g2.setColor(new Color(64,64,64));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x+1, y+1, Cell.CELL_SIZE-4, Cell.CELL_SIZE-4, 8, 8);
        //}catch(){}*/
    }
}
