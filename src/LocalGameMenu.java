import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Klasa okna tworzenia gry lokalnej
 */
public class LocalGameMenu extends JPanel {

    private final String[] numOfPlayersStrings = {"2 graczy", "3 graczy", "4 graczy"};
    private final String labelInfoString = "Wybierz liczbę graczy!";
    private final String playerNamesInfoString = "Gracze podajcie swoje pseudonimy!";

    private Window windowRef;
    private JTextArea numOfPlayersInfoTA;
    private JComboBox numOfPlayersComboBox;
    private JTextArea playerNamesInfoTA;
    private JTextField[] playerNameTF;
    private JButton startGameButton;
    private JButton returnButton;

    private int numOfPlayersSelected;

    /**
     * Konstruktor klasy
     * @param ref odniesienie do głównego okna programu
     */
    public LocalGameMenu(Window ref) {
        windowRef = ref;

        numOfPlayersInfoTA = new JTextArea(labelInfoString);
        numOfPlayersInfoTA.setFocusable(false);

        numOfPlayersComboBox = new JComboBox(numOfPlayersStrings);
        numOfPlayersComboBox.setSelectedIndex(numOfPlayersStrings.length-1);
        numOfPlayersSelected = 4;
        numOfPlayersComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedItem = (String) numOfPlayersComboBox.getSelectedItem();
                numOfPlayersSelected = Integer.parseInt(selectedItem.substring(0,1));
                setPlayerNamesTFVisible();
                windowRef.repaint();
            }
        });

        playerNamesInfoTA = new JTextArea(playerNamesInfoString);
        playerNamesInfoTA.setFocusable(false);

        initializePlayerNamesTF();
        setPlayerNamesTFVisible();

        startGameButton = new JButton("Start");
        returnButton = new JButton("Powrót");

        startGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNewGame();
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

        add(numOfPlayersInfoTA);
        add(numOfPlayersComboBox);
        add(playerNamesInfoTA);
        addPlayerNamesTFToPanel();
        add(startGameButton);
        add(returnButton);

        setVisible(false);
    }

    private void initializePlayerNamesTF() {
        playerNameTF = new JTextField[4];
        for(int i=0;i<4;i++) {
            playerNameTF[i] = new JTextField("Gracz nr " + (i+1));
        }
    }

    private void addPlayerNamesTFToPanel() {
        for(JTextField tf : playerNameTF) {
            add(tf);
        }
    }

    private void setPlayerNamesTFVisible() {

        for(JTextField tf : playerNameTF) {
            tf.setVisible(false);
        }

        for(int i=0;i<numOfPlayersSelected;i++) {
            playerNameTF[i].setVisible(true);
        }
    }

    /**
     * Metoda tworząca instancję nowej gry lokalnej
     */
    private void createNewGame() {
        setVisible(false);
        String[] names = new String[numOfPlayersSelected];
        for(int i=0;i<numOfPlayersSelected;i++) {
            names[i] = playerNameTF[i].getText();
        }

        new GameCreator(numOfPlayersSelected, names, windowRef);
    }

}
