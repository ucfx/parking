package parking;

import javax.swing.JFrame;

import component.PanelGame;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 *
 * @author SasUke UtChiHa
 */
public class Parking extends JFrame {
    public Parking() {
        init();
    }

    public void init() {
        setTitle("Parking");
        setSize(1000, 730);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        PanelGame panelGame = new PanelGame();
        add(panelGame, BorderLayout.CENTER);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent evt) {
                panelGame.start();
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Parking().setVisible(true);
    }

}
