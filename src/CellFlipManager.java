import java.awt.*;

public class CellFlipManager extends Thread{
    int x,y, myCycles,imageWidth,imageHeight,lastImageIndex;
    Cell myCell;
    Graphics g;

    public CellFlipManager(){

    }

    public CellFlipManager(Cell setCell, int lastImageIndex){
        x = setCell.getX();
        y = setCell.getY();
        myCell = setCell;
        myCycles = 1;
        this.lastImageIndex = lastImageIndex;
    }

    public void setG(Graphics g) {
        this.g = g;
    }

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
        g2.fillRect(x, y, Cell.CELL_SIZE, Cell.CELL_SIZE);

        int scaleFactor = Math.abs(10-myCycles);
        Image scaledImage;
        if(myCycles<10){
            scaledImage = myCell.getScaledImageAtIndex(lastImageIndex,scaleFactor);
        }else{
            scaledImage = myCell.getMyScaledImage(scaleFactor);//.getScaledInstance(imageWidth,(int)(imageHeight*scaleFactor),Image.SCALE_DEFAULT);
        }

        int scaledHeight = Math.min(scaledImage.getHeight(null),Cell.CELL_SIZE);

        if(myCycles!=10) {
            g2.drawImage(scaledImage, x, y + Cell.CELL_SIZE / 2 - scaledHeight / 2,
                    Cell.CELL_SIZE, scaledHeight, null);
        }
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
