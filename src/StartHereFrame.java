import javax.swing.*;
import java.awt.*;

public class StartHereFrame extends JFrame
{
    private StartHerePanel shp;
    public StartHereFrame()
    {
        super("Start Here - Cell");

        getContentPane().setLayout(new GridLayout(1,1));

        shp = new StartHerePanel();
        getContentPane().add(shp);
        shp.repaint();
        setSize(600,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

    }


}
