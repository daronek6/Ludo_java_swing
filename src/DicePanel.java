import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Klasa odpowiadająca za okno, w którym wyświetlana jest kostka do gry
 */
public class DicePanel extends JPanel {

    public static final float HEIGHT = (Window.DEFAULT_PANE_WIDTH-10)*(1f/4f); //x*5
    public static final float DOT_RADIUS = HEIGHT/7f;

    private int rolledNum;

    public DicePanel() {
        rolledNum = 0;
        setMinimumSize(new Dimension((int)((Window.DEFAULT_PANE_WIDTH-10) *(1f/4f)*GamePanel.xScale),(int)(HEIGHT*GamePanel.yScale)));
        setPreferredSize(new Dimension((int)((Window.DEFAULT_PANE_WIDTH-10) *(1f/4f)*GamePanel.xScale),(int)(HEIGHT*GamePanel.yScale)));

        setBackground(Color.WHITE);
        setVisible(true);

    }

    /**
     * Aktualizacja wielkości okna
     */
    public void resize() {
        setMinimumSize(new Dimension((int)((Window.DEFAULT_PANE_WIDTH-10) *(1f/4f)*GamePanel.xScale),(int)(HEIGHT*GamePanel.yScale)));
        setPreferredSize(new Dimension((int)((Window.DEFAULT_PANE_WIDTH-10) *(1f/4f)*GamePanel.xScale),(int)(HEIGHT*GamePanel.yScale)));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        float dRadX = DOT_RADIUS *GamePanel.xScale;
        float dRadY = DOT_RADIUS *GamePanel.yScale;

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);

        Ellipse2D.Float spot1 = new Ellipse2D.Float(dRadX,dRadY,dRadX,dRadY);
        Ellipse2D.Float spot2 = new Ellipse2D.Float(3*dRadX,dRadY,dRadX,dRadY);
        Ellipse2D.Float spot3 = new Ellipse2D.Float(5*dRadX,dRadY,dRadX,dRadY);
        Ellipse2D.Float spot4 = new Ellipse2D.Float(3*dRadX,3*dRadY,dRadX,dRadY);
        Ellipse2D.Float spot5 = new Ellipse2D.Float(dRadX,5*dRadY,dRadX,dRadY);
        Ellipse2D.Float spot6 = new Ellipse2D.Float(3*dRadX,5*dRadY,dRadX,dRadY);
        Ellipse2D.Float spot7 = new Ellipse2D.Float(5*dRadX,5*dRadY,dRadX,dRadY);


        switch (rolledNum) {
            case 1:
                g2d.fill(spot4);
                break;
            case 2:
                g2d.fill(spot1);
                g2d.fill(spot7);
                break;
            case 3:
                g2d.fill(spot1);
                g2d.fill(spot4);
                g2d.fill(spot7);
                break;
            case 4:
                g2d.fill(spot1);
                g2d.fill(spot3);
                g2d.fill(spot5);
                g2d.fill(spot7);
                break;
            case 5:
                g2d.fill(spot1);
                g2d.fill(spot3);
                g2d.fill(spot4);
                g2d.fill(spot5);
                g2d.fill(spot7);
                break;
            case 6:
                g2d.fill(spot1);
                g2d.fill(spot2);
                g2d.fill(spot3);
                g2d.fill(spot5);
                g2d.fill(spot6);
                g2d.fill(spot7);
                break;
        }

    }

    /**
     * Aktualizacja wyświetlanej kostki
     * @param newNum wartość, która ma być wyświetlana
     */
    public void updateRolledNum(int newNum) {
        rolledNum = newNum;
        repaint();
    }

}
