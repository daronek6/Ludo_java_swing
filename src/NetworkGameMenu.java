import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Klasa okna wyboru opcji gry przez sieć
 */
public class NetworkGameMenu extends JPanel {

    private Window windowRef;
    private JTextField nameTF;
    private JTextField ipAddressTF;
    private JButton createGameButton;
    private JButton joinGameButton;
    private JButton returnButton;

    /**
     * Konstruktor klasy
     * @param winRef odniesienie do okna głównego programu
     */
    public NetworkGameMenu(Window winRef) {
        windowRef = winRef;

        nameTF = new JTextField("Twoja nazwa!");
        ipAddressTF = new JTextField("Podaj lokalny adres swojego komputera (Istotne gdy tworzysz grę)!");
        createGameButton = new JButton("Stwórz grę");
        joinGameButton = new JButton("Dołącz do gry");
        returnButton = new JButton("Powrót");

        createGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                try {
                    windowRef.add(new NetworkLobbyMenu(nameTF.getText(), ipAddressTF.getText(), windowRef));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    windowRef.showNetworkGameMenu();
                }

            }
        });
        joinGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                windowRef.add(new JoinGameMenu(nameTF.getText(), windowRef));
            }
        });
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                windowRef.showMainMenu();
            }
        });

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.CENTER_ALIGNMENT);

        add(nameTF);
        add(ipAddressTF);
        add(createGameButton);
        add(joinGameButton);
        add(returnButton);

        setVisible(false);
    }
}
