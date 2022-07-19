import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;

/**
 * Klasa okna lobby serwera
 */
public class NetworkLobbyMenu extends JPanel {

    private Window windowRef;
    private GameServer gameServer;
    private ClientSide hostClient;

    private JTextArea ipInfo;
    private JTextArea numOfPlayersConnected;
    private JTextArea[] playerNamesTA;
    private JButton startButton;
    private JButton returnButton;

    /**
     * Konstruktor klasy
     * @param name nazwa twórcy serwera
     * @param ip adres serwera
     * @param winRef odniesienie do okna głównego programu
     */
    public NetworkLobbyMenu(String name, String ip, Window winRef) {
        windowRef = winRef;
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

        setupGameServer(ip, this);
        setupIpInfo();
        setupNumOfPlayerConnected();
        setupPlayerNamesTA();
        setupStartButton();
        setupReturnButton();
        setupHostClient(name);
        hostClient.joinGame(ip);

        add(ipInfo);
        add(numOfPlayersConnected);
        for(JTextArea ta : playerNamesTA) {
            add(ta);
        }
        add(startButton);
        add(returnButton);

        setVisible(true);
    }

    private void setupGameServer(String ip, NetworkLobbyMenu thisLobby) {
        gameServer = new GameServer(ip, thisLobby);
    }

    private void setupPlayerNamesTA() {
            playerNamesTA = new JTextArea[4];

        for(int i=0;i<4;i++) {
            playerNamesTA[i] = new JTextArea("Gracz nr " + (i+1));
            playerNamesTA[i].setFocusable(false);
        }
    }

    /**
     * Aktualizuj wyświetlane nazwy połączonych klientów
     * @param id id połączonego klienta
     * @param name nazwa połączonego klienta
     */
    public void updatePlayerName(int id, String name) {
        playerNamesTA[id].setText(name);
    }

    private void setupNumOfPlayerConnected() {
        numOfPlayersConnected = new JTextArea("Połączonych graczy: 0");
        numOfPlayersConnected.setFocusable(false);
    }

    /**
     * Metoda aktualizująca ilość połączonych osób
     * @param num ilość połączonych osób
     */
    public void updateNumOfPlayerConnected(int num) {
        numOfPlayersConnected.setText("Połączonych graczy: " + num);

        if(num >= 2) {
            startButton.setEnabled(true);
        }
    }

    private void setupIpInfo() {
        ipInfo = new JTextArea("Ip serwera: 0.0.0.0");
        ipInfo.setFocusable(false);
        InetAddress serverAddress = gameServer.getServerAddress();
        System.out.println("Is null? " + serverAddress == null);
        ipInfo.setText("Ip serwera: " + serverAddress.getHostAddress());
    }

    private void setupStartButton() {
        startButton = new JButton("Zacznij grę");
        startButton.setEnabled(false);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameServer.startNewGame();
                setVisible(false);
            }
        });
    }

    private void setupReturnButton() {
        returnButton = new JButton("Powrót");

        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(gameServer != null && gameServer.isAlive()) {
                    gameServer.stopServer();
                }
                setVisible(false);
                windowRef.showNetworkGameMenu();
            }
        });
    }

    private void setupHostClient(String name) {
        hostClient = new ClientSide(name, windowRef);
    }

}
