import javax.swing.*;
import java.awt.*;

public class StartHerePanel extends JPanel
{

    private Cell myCell;

    public StartHerePanel()
    {
        super();
        myCell = new Cell(1,2,3,"X",true);
        // TODO: put some of your own code here to test the effects of the various Cell methods on myCell. You might
        //    also tinker with some of the parameters of the constructor we just used.



    }


    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        myCell.drawSelf(g);
    }


}
