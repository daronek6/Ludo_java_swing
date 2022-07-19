import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Klasa okna głównego menu
 */
public class MainMenu extends JPanel {

    private Window windowRef;
    private JButton localGameButton;
    private JButton networkGameButton;
    private JButton rulesButton;
    private JButton exitButton;

    /**
     * Konstruktor klasy
     * @param ref odniesienie do głównego okna programu
     */
    public MainMenu(Window ref) {
        windowRef = ref;

        localGameButton = new JButton("Gra na tym komputerze");
        networkGameButton = new JButton("Gra na LAN");
        rulesButton = new JButton("Zasady gry");
        exitButton = new JButton("Wyjdź z gry");

        localGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                windowRef.showLocalGameMenu();
            }
        });
        networkGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                windowRef.showNetworkGameMenu();
            }
        });
        rulesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                windowRef.showRulesMenu();
            }
        });
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                windowRef.dispose();
            }
        });

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.CENTER_ALIGNMENT);
        
        add(localGameButton);
        add(networkGameButton);
        add(rulesButton);
        add(exitButton);

        setVisible(false);
    }

}
