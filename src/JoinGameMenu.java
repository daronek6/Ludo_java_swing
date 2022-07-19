import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Klasa okna dołączania do gry sieciowej
 */
public class JoinGameMenu extends JPanel {

    private Window windowRef;
    private ClientSide clientSide;

    private JTextField serverIpTF;
    private JTextArea infoTA;
    private JButton joinButton;
    private JButton returnButton;

    public JoinGameMenu(String name, Window winRef) {
        windowRef = winRef;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setupServerIpTF();
        setupInfoTA();
        setupJoinButton();
        setupReturnButton();
        setupClientSide(name);

        add(serverIpTF);
        add(infoTA);
        add(joinButton);
        add(returnButton);

        setVisible(true);
    }

    /**
     * Utworzenie instancji klienta
     * @param name
     */
    private void setupClientSide(String name) {
        clientSide = new ClientSide(name, windowRef,this);
    }

    private void setupServerIpTF() {
        serverIpTF = new JTextField("Podaj ip serwera");
    }

    private void setupInfoTA() {
        infoTA = new JTextArea("Informacja o połączeniu:");
        infoTA.setFocusable(false);
    }

    /**
     * Aktualizacja stanu połączenia z serwerem
     * @param info informacja do wyświetlenia
     */
    public void updateInfoTA(String info) {
        infoTA.setText("Informacja o połączeniu: " + info);
    }

    private void setupJoinButton() {
        joinButton = new JButton("Połącz");

        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientSide.joinGame(serverIpTF.getText());
            }
        });
    }

    private void setupReturnButton() {
        returnButton = new JButton("Powrót");

        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                windowRef.showNetworkGameMenu();
            }
        });
    }

    public void disableJoinButton() {
        joinButton.setEnabled(false);
    }

    public void enableJoinButton() {
        joinButton.setEnabled(true);
    }

}
