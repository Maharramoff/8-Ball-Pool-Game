package pool;

import javax.swing.*;

public class Pool
{
    public static void main(String[] args)
    {
        JFrame ui = new JFrame("Pool game");
        ui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ui.setContentPane(new TableWindow());
        ui.pack();
        ui.setVisible(true);
    }
}