import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Klasa odpowiedzialna za wyświetlanie informacji o turze oraz za wyświetlanie i kontrolę losowanej kostki
 */
public class ControlBoard extends JPanel {

    public static final float PLAYER_NAME_HEIGHT = 1000f;

    private GamePanel gamePanelRef;
    private JTextArea playersTurnNameTA;
    private JTextArea timerTA;
    private JTextArea infoTA;
    private DicePanel dicePanel;
    private JButton rollButton;

    /**
     * Konstruktor klasy
     * @param gPanel Odniesienie do panelu gry przechowującego obiekt tej klasy
     */
    public ControlBoard(GamePanel gPanel) {
        gamePanelRef = gPanel;

        FlowLayout fl = new FlowLayout();
        fl.setVgap(20);
        setLayout(fl);

        playersTurnNameTA = new JTextArea("Tura: Nazwa gracza");
        playersTurnNameTA.setFocusable(false);
        playersTurnNameTA.setMaximumSize(new Dimension((int)((Window.DEFAULT_PANE_WIDTH)*(1f/4f)*gamePanelRef.getxScale()),(int)(PLAYER_NAME_HEIGHT*gamePanelRef.getyScale())));

        timerTA = new JTextArea("00:00");
        timerTA.setFocusable(false);
        timerTA.setMaximumSize(new Dimension((int)((Window.DEFAULT_PANE_WIDTH)*(1f/4f)*gamePanelRef.getxScale()),(int)(PLAYER_NAME_HEIGHT*gamePanelRef.getyScale())));

        infoTA = new JTextArea("Rzuć kością!");
        infoTA.setFocusable(false);
        infoTA.setPreferredSize(new Dimension((int)((Window.DEFAULT_PANE_WIDTH)*(1f/4f)*gamePanelRef.getxScale()),(int)(30f*gamePanelRef.getyScale())));
        infoTA.setMaximumSize(new Dimension((int)((Window.DEFAULT_PANE_WIDTH)*(1f/4f)*gamePanelRef.getxScale()),(int)(PLAYER_NAME_HEIGHT*gamePanelRef.getyScale())));

        dicePanel = new DicePanel();
        rollButton = new JButton("Rzut kością!");

        setMinimumSize(new Dimension((int)((Window.DEFAULT_PANE_WIDTH-10) *(1f/4f)*gamePanelRef.getxScale()),(int)(Window.DEFAULT_PANE_HEIGHT*gamePanelRef.getyScale())));
        setPreferredSize(new Dimension((int)((Window.DEFAULT_PANE_WIDTH-10) *(1f/4f)*gamePanelRef.getxScale()),(int)(Window.DEFAULT_PANE_HEIGHT*gamePanelRef.getyScale())));

        setBackground(Color.DARK_GRAY);
        add(playersTurnNameTA);
        add(timerTA);
        add(infoTA);
        add(dicePanel);
        add(rollButton);

        setVisible(true);
    }

    /**
     * Ustawienie wielkości okna
     */
    public void resize() {
        setPreferredSize(new Dimension((int)((Window.DEFAULT_PANE_WIDTH-10) *(1f/4f)*gamePanelRef.getxScale()),(int)(Window.DEFAULT_PANE_HEIGHT*gamePanelRef.getyScale())));
        playersTurnNameTA.setMaximumSize(new Dimension((int)((Window.DEFAULT_PANE_WIDTH)*(1f/4f)*gamePanelRef.getxScale()),(int)(PLAYER_NAME_HEIGHT*gamePanelRef.getyScale())));
        timerTA.setMaximumSize(new Dimension((int)((Window.DEFAULT_PANE_WIDTH)*(1f/4f)*gamePanelRef.getxScale()),(int)(PLAYER_NAME_HEIGHT*gamePanelRef.getyScale())));
        infoTA.setPreferredSize(new Dimension((int)((Window.DEFAULT_PANE_WIDTH)*(1f/4f)*gamePanelRef.getxScale()),(int)(30f*gamePanelRef.getyScale())));
        dicePanel.resize();
    }

    /**
     * Aktualizaja wyświetlanego gracza (nazwy gracza, którego jest teraz tóra)
     * @param name nazwa tego gracza
     */
    public void updatePlayersTurnNameTA(String name) {
        playersTurnNameTA.setText("Tura: " + name);
    }

    /**
     * Aktualizacja wyświetlanej kostki do gry
     * @param rolledNum jaka wartość ma być pokazywana
     */
    public void updateDicePanel(int rolledNum) {
        dicePanel.updateRolledNum(rolledNum);
    }

    public void updateTimer(String timeLeft) {
        timerTA.setText(timeLeft);
        repaint();
    }

    /**
     * Aktualizacja informacji dla gracza
     * @param newInfo nowa informacja
     */
    public void updateInfoTA(String newInfo) {
        infoTA.setText(newInfo);
    }

    public void disableRollButton() {
        rollButton.setEnabled(false);
        rollButton.setBackground(Color.LIGHT_GRAY);
    }

    public void enableRollButton() {
        rollButton.setEnabled(true);
        rollButton.setBackground(Color.WHITE);
    }

    public void setRollButtonActionListener(ActionListener aL) {
        rollButton.addActionListener(aL);
    }
}
