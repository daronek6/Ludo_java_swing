import javax.swing.*;
import java.awt.*;

/**
 * Klasa głównego okna
 */
public class Window extends JFrame {

    public static final float DEFAULT_WIDTH = 1280;
    public static final float DEFAULT_HEIGHT = 720;

    public static final float DEFAULT_PANE_WIDTH = DEFAULT_WIDTH - 16;
    public static final float DEFAULT_PANE_HEIGHT = DEFAULT_HEIGHT - 39;

    private JPanel mainMenu;
    private JPanel localGameMenu;
    private JPanel rulesMenu;
    private JPanel networkGameMenu;

    /**
     * Konstruktor klasy
     */
    public Window() {
        super("Projekt JTP Chińczyk");

        mainMenu = new MainMenu(this);
        localGameMenu = new LocalGameMenu(this);
        rulesMenu = new RulesMenu(this);
        networkGameMenu = new NetworkGameMenu(this);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setLocation(200,200);
        setSize((int) DEFAULT_WIDTH,(int) DEFAULT_HEIGHT);
        setMinimumSize(new Dimension((int) DEFAULT_WIDTH, (int) DEFAULT_HEIGHT));
        getContentPane().add(mainMenu);
        getContentPane().add(localGameMenu);
        getContentPane().add(rulesMenu);
        getContentPane().add(networkGameMenu);

        setVisible(true);

        showMainMenu();
    }

    /**
     * Metoda wyświetlająca główne menu
     */
    public void showMainMenu() {
        mainMenu.setVisible(true);
    }

    /**
     * Metoda wyświetlająca menu tworzenia gry lokalnej
     */
    public void showLocalGameMenu() {
        localGameMenu.setVisible(true);
    }

    /**
     * Metoda wyśwtelająca menu opcji gry sieciowej
     */
    public void showNetworkGameMenu() {
        networkGameMenu.setVisible(true);
    }

    /**
     * Metoda wyświetlająca menu zasad gry
     */
    public void showRulesMenu() {
        rulesMenu.setVisible(true);
    }

}
