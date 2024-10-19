package io.github.maharramoff.game.pool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class GameMenu extends JFrame implements ActionListener
{

    private Game game;

    GameMenu()
    {
        super();
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        game = new Game();
        setTitle("Pool game");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        JMenuBar menu = new JMenuBar();

        JMenu file = new JMenu("Game"), opts = new JMenu("Options");

        menu.add(file);
        menu.add(opts);

        addMenuItems(file, "New game-Quit the game-About");
        addMenuItems(opts, "@Friction on/off-Holes on/off");

        setJMenuBar(menu);
        add(game, BorderLayout.CENTER);
        pack();
        setVisible(true);
        requestFocus();
    }

    private void addMenuItems(JMenu menu, String items)
    {
        JMenuItem i;

        for (String str : items.split("-"))
        {
            i = str.substring(0, 1).equals("@") ? new JCheckBoxMenuItem(str.substring(1), true) : new JMenuItem(str);
            i.addActionListener(this);
            menu.add(i);
        }
    }

    public void actionPerformed(ActionEvent e)
    {

        switch (e.getActionCommand())
        {
            case "Quit the game":
                System.exit(0);
            case "New game":
                System.out.println("New game started");
                game.createNewGame();
                break;
            case "Friction on/off":
                System.out.println("Friction turned " + (Helper.FR ? "Off" : "On") + "");
                Helper.FR = !Helper.FR;
                break;
            case "Holes on/off":
                System.out.println("Holes turned " + (Helper.HA ? "Off" : "On") + "");
                Helper.HA = !Helper.HA;
                break;
            case "About":
                JOptionPane.showMessageDialog(this, "Ugly Pool game by Maharramoff Shamkhal\nDedicated to my 8 years old son Elay Maharramli who is actively learning programming.", "About", JOptionPane.PLAIN_MESSAGE);
                break;
        }
    }

}
