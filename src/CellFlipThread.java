import java.awt.*;

public class CellFlipThread extends Thread{
    int x,y, myCycles,imageWidth,imageHeight,lastImageIndex;
    Cell myCell;
    Graphics g;

    public CellFlipThread(){

    }

    public CellFlipThread(Cell setCell,int lastImageIndex){
        x = setCell.getX();
        y = setCell.getY();
        myCell = setCell;
        myCycles = 1;
    }

    public void setG(Graphics g) {
        this.g = g;
    }

    //@Override
    public void updateAnim() {
        //myCycles = 1;
        //imageWidth = myCell.getMyImage().getWidth(null);
        //imageHeight = myCell.getMyImage().getHeight(null);
        //while(myCycles<20){
            //try{
            /*double scaleFactor = Math.abs(10-myCycles)/10;
            Image scaledImage = myCell.getMyImage().getScaledInstance(imageWidth,
                        (int)(imageHeight*scaleFactor),Image.SCALE_DEFAULT);
            g2.drawImage(scaledImage, x,y, Cell.CELL_SIZE-2, Cell.CELL_SIZE-2, null);

            g2.setColor(new Color(192,192,192));
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(x+1, y+1, Cell.CELL_SIZE-4, Cell.CELL_SIZE-4, 8, 8);

            g2.setColor(new Color(64,64,64));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(x+1, y+1, Cell.CELL_SIZE-4, Cell.CELL_SIZE-4, 8, 8);*/
            drawSelf();
            /*try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            //}catch(){}
            myCycles++;
        System.out.println(myCycles);
        //}
        if(myCycles>=20) {
            System.out.println("done");
            myCell.setColorChanged(false);
            CellFlipThread.currentThread().interrupt();
        }
    }


    public void drawSelf(){
        Graphics2D g2 = (Graphics2D)g;
        int scaleFactor = Math.abs(10-myCycles);
        //System.out.println("draw");
        //if(scaleFactor==0){
        //    scaleFactor = 1/((double)imageHeight);
        //}
        //scaleFactor = 0.5;
        Image scaledImage;
        if(myCycles<=10){
            scaledImage = myCell.getScaledImageAtIndex(lastImageIndex,scaleFactor);
        }else{
            scaledImage = myCell.getMyScaledImage(scaleFactor);//.getScaledInstance(imageWidth,(int)(imageHeight*scaleFactor),Image.SCALE_DEFAULT);
        }
        g2.setColor(Color.BLACK);
        g2.fillRect(x, y, Cell.CELL_SIZE*2, Cell.CELL_SIZE*2);
        g2.drawImage(scaledImage, x,y,  Cell.CELL_SIZE-2,(Cell.CELL_SIZE-2), null);
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
