import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseListener;

/**
 * Klasa okna przechowująca wszystkie okna związane z rozgrywką
 */
public class GamePanel extends JPanel  {

    private Window windowRef;
    private GameBoard gameBoard;
    private ControlBoard controlBoard;

    public static float xScale;
    public static float yScale;

    public static Step up;
    public static Step upRight;
    public static Step right;
    public static Step downRight;
    public static Step down;
    public static Step downLeft;
    public static Step left;
    public static Step upLeft;

    /**
     * Konstruktor klasy
     * @param ps odniesienie do graczy biorących udział w rozgrywce
     * @param ref odniesienie do głównego okna programu
     */
    public GamePanel(Player[] ps, Window ref) {
        windowRef = ref;
        updateScale();

        up = new Step(0,-1);
        upRight = new Step(1,-1);
        right = new Step(1,0);
        downRight = new Step(1,1);
        down = new Step(0,1);
        downLeft = new Step(-1,1);
        left = new Step(-1,0);
        upLeft = new Step(-1,-1);

        setBackground(Color.BLACK);

        FlowLayout fl = new FlowLayout();
        fl.setHgap(0);
        fl.setVgap(0);
        setLayout(fl);

        setMinimumSize(new Dimension((int)Window.DEFAULT_PANE_WIDTH, (int)Window.DEFAULT_PANE_HEIGHT));
        setPreferredSize(new Dimension((int)Window.DEFAULT_PANE_WIDTH, (int)Window.DEFAULT_PANE_HEIGHT));

        gameBoard = new GameBoard(ps,this);
        controlBoard = new ControlBoard(this);

        add(gameBoard);
        add(controlBoard);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                updateInterface();
            }
        });

        setVisible(true);
    }

    public float getxScale() {
        return xScale;
    }

    public float getyScale() {
        return yScale;
    }

    /**
     * Metoda aktualizująca wartości skali powiększenia okna
     */
    public void updateScale() {
        xScale = ((float)windowRef.getGlassPane().getWidth())/Window.DEFAULT_PANE_WIDTH;
        yScale = ((float)windowRef.getGlassPane().getHeight())/Window.DEFAULT_PANE_HEIGHT;
    }

    /**
     * Metoda aktualizująca interfejs
     */
    public void updateInterface() {
        updateScale();

        gameBoard.resize();
        controlBoard.resize();
        gameBoard.repaint();
        controlBoard.repaint();

    }

    public void updateGameBoard() {
        gameBoard.repaint();
    }

    /**
     * Metoda dodająca obsługę naciśnięca na pionki
     * @param mL obiekt kontrolujący zdarzenie
     */
    public void addBoxColliersToGameBoard(MouseListener mL) {
        gameBoard.setupPawnBoxColliers(mL);
    }

    /**
     * Metoda kontrolująca główne okno po zakończonej rozgrywce
     * @param winnerName nazwa zwyciężcy
     */
    public void gameOverAction(String winnerName) {
        setVisible(false);
        windowRef.showMainMenu();
        JOptionPane.showMessageDialog(windowRef,"Wygrał: " + winnerName + ". Gratulacje wygranemu!");
    }

    public ControlBoard getControlBoard() {
        return controlBoard;
    }
}
