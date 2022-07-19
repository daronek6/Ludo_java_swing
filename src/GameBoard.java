import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 * Klasa okna na którym rysowana jest plansza oraz pionki w trakie gry
 */
public class GameBoard extends JPanel {

    public static final float BASE_RECT_SIDE = 240f; //x*30
    public static final float FIELD_SIDE = BASE_RECT_SIDE /6f;
    public static final float BASE_FIELD_GAP = BASE_RECT_SIDE/5f;
    public static final float BASE_GAP = 3f* FIELD_SIDE;
    public static final float START_POS_X = 128f;
    public static final float START_POS_Y = 10f;
    public static final float NAME_START_POS = 20f;
    public static final float FONT_SIZE = 14f;
    private GamePanel gamePanelRef;
    private Player[] playersRef;

    /**
     * Konstruktor tworzący okno planszy
     * @param pRef odniesienie do graczy grających na tej planszy
     * @param gPanel odniesienie do okna przechowującego obiekt tej klasy
     */
    public GameBoard(Player[] pRef, GamePanel gPanel) {
        gamePanelRef = gPanel;
        playersRef = pRef;

        setMinimumSize(new Dimension((int)(Window.DEFAULT_PANE_WIDTH *(3f/4f)*gamePanelRef.getxScale()),(int)(Window.DEFAULT_PANE_HEIGHT*gamePanelRef.getyScale())));
        setPreferredSize(new Dimension((int)(Window.DEFAULT_PANE_WIDTH *(3f/4f)*gamePanelRef.getxScale()),(int)(Window.DEFAULT_PANE_HEIGHT*gamePanelRef.getyScale())));

        setLayout(null);

        setBackground(Color.ORANGE);
        setupPawns();
        setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        drawBoard(g2d);
        drawPawns(g2d);
        drawPlayerNames(g2d);
    }

    /**
     * Metoda służąca do rysowania planszy
     * @param g2d komponent potrzeby do rysowania na oknie
     */
    private void drawBoard(Graphics2D g2d) {
        float bSideX = BASE_RECT_SIDE *gamePanelRef.getxScale();
        float bSideY = BASE_RECT_SIDE *gamePanelRef.getyScale();
        float bGapX = BASE_GAP *gamePanelRef.getxScale();
        float bGapY = BASE_GAP *gamePanelRef.getyScale();
        float fSideX = FIELD_SIDE *gamePanelRef.getxScale();
        float fSideY = FIELD_SIDE *gamePanelRef.getyScale();
        float sX = START_POS_X * gamePanelRef.getxScale();
        float sY = START_POS_Y * gamePanelRef.getyScale();
        float bFGX = BASE_FIELD_GAP * gamePanelRef.getxScale();
        float bFGY = BASE_FIELD_GAP * gamePanelRef.getyScale();

        float alfa = (float)((Math.PI)/2f);
        float xDir;
        float yDir;
        float currentX;
        float currentY;

        Color endColor = Color.BLACK;

        Rectangle2D.Float baseRect = new Rectangle2D.Float(0,0,0,0);
        Rectangle2D.Float fieldRect = new Rectangle2D.Float(0,0,0,0);
        Ellipse2D.Float baseField = new Ellipse2D.Float(0,0,0,0);
        //G->R->B->Y baza
        currentX = sX + bSideX + bGapX;
        currentY = sY;

        for(int i=0;i<4;i++) {

            xDir = (float) Math.sin((-i)*alfa);
            yDir = (float) Math.cos(i*alfa);

            endColor = BaseColors.baseColors[i];

            g2d.setPaint(new GradientPaint(currentX,currentY,Color.WHITE,currentX+bSideX,currentY+bSideY,endColor));
            baseRect.setRect(currentX,currentY,bSideX,bSideY);
            g2d.fill(baseRect);

            g2d.setColor(Color.BLACK);
            g2d.draw(baseRect);

            //base fields ------------------------------
            float currentBaseFieldX = currentX + 3*bFGX;
            float currentBaseFieldY = currentY + bFGY;

            g2d.setColor(Color.WHITE);
            for(int j=0;j<4;j++) {
                baseField.setFrame(currentBaseFieldX, currentBaseFieldY, bFGX, bFGY);
                g2d.fill(baseField);
                xDir = (float) Math.sin((-j)*alfa);
                yDir = (float) Math.cos(j*alfa);

                currentBaseFieldX += xDir * 2*bFGX;
                currentBaseFieldY += yDir * 2*bFGY;

            }
            xDir = (float) Math.sin((-i)*alfa);
            yDir = (float) Math.cos(i*alfa);

            currentX += xDir * (bSideX + bGapX);
            currentY += yDir * (bSideY + bGapY);

        }
//----------------------------------------------------------------------
        currentX = sX + bSideX + 2*fSideX;
        currentY = sY;

        for(int i=0;i<4;i++) {
            g2d.setColor(Color.BLACK);

            xDir = (float) Math.sin((-i)*alfa);
            yDir = (float) Math.cos(i*alfa);

            for(int j=0;j<6;j++) {
                if(j == 0) {

                    endColor = BaseColors.baseColors[i];

                    g2d.setPaint(new GradientPaint(currentX,currentY,Color.WHITE,currentX+fSideX,currentY+fSideY,endColor));
                    fieldRect.setRect(currentX,currentY,fSideX,fSideY);
                    g2d.fill(fieldRect);

                    g2d.setColor(Color.BLACK);

                }

                fieldRect.setRect(currentX, currentY, fSideX, fSideY);
                g2d.draw(fieldRect);

                currentX += xDir * fSideX;
                currentY += yDir * fSideY;
            }

            xDir = (float) Math.sin((-i+1)*alfa);
            yDir = (float) Math.cos((i-1)*alfa);

            for(int j=0;j<6;j++) {
                fieldRect.setRect(currentX,currentY,fSideX,fSideY);
                g2d.draw(fieldRect);

                currentX += xDir*fSideX;
                currentY += yDir*fSideY;
            }

            xDir = (float) Math.sin((-i)*alfa);
            yDir = (float) Math.cos(i*alfa);

            for(int j=0;j<2;j++) {
                fieldRect.setRect(currentX,currentY,fSideX,fSideY);
                g2d.draw(fieldRect);
                //Home fields -----------------------------
                if(j==1) {
                    endColor = BaseColors.baseColors[(i+1) % 4];

                    float currentHomeFieldX = currentX;
                    float currentHomeFieldY = currentY;
                    float homeDirX = (float) Math.sin((-(i+1))*alfa);
                    float homeDirY = (float) Math.cos((i+1)*alfa);

                    for(int k=0;k<5;k++) {
                        currentHomeFieldX += homeDirX*fSideX;
                        currentHomeFieldY += homeDirY*fSideY;
                        g2d.setPaint(new GradientPaint(currentHomeFieldX,currentHomeFieldY,Color.WHITE,currentHomeFieldX+fSideX,currentHomeFieldY+fSideY,endColor));
                        fieldRect.setRect(currentHomeFieldX,currentHomeFieldY,fSideX,fSideY);
                        g2d.fill(fieldRect);

                        g2d.setColor(Color.BLACK);
                        g2d.draw(fieldRect);
                    }
                }
                //----------------------------------------------

                currentX += xDir*fSideX;
                currentY += yDir*fSideY;
            }

        }
    }

    /**
     * Metoda służąca do rysowania pionków tam, gdzie aktualnie się znajdują
     * @param g2d komponent potrzeby do rysowania na oknie
     */
    private void drawPawns(Graphics2D g2d) {
        for(Player player : playersRef) {
           // System.out.println("Draw Pawns: " + player.getPawns());
            for(Pawn pawn : player.getPawns()) {
                g2d.setPaint(pawn.getColor());
                g2d.fill(pawn.createEllipse());
            }
        }
    }

    /**
     * Metoda służca do rysowania nazw graczy na polach bazowych ich pionków
     * @param g2d komponent potrzeby do rysowania na oknie
     */
    private void drawPlayerNames(Graphics2D g2d) {
        float xDir;
        float yDir;
        float currentX = START_POS_X + NAME_START_POS + BASE_RECT_SIDE + BASE_GAP;
        float currentY = START_POS_Y + NAME_START_POS;
        float alfa = (float)((Math.PI)/2f);

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int) (FONT_SIZE * Math.min(GamePanel.xScale, GamePanel.yScale))));
        for(int i=0;i<playersRef.length;i++) {
            xDir = (float) Math.sin((-i)*alfa);
            yDir = (float) Math.cos(i*alfa);

            g2d.drawString(playersRef[i].getName(),currentX*GamePanel.xScale, currentY*GamePanel.yScale);

            currentX += xDir*(BASE_RECT_SIDE + BASE_GAP);
            currentY += yDir*(BASE_RECT_SIDE + BASE_GAP);
        }
    }

    /**
     * Metoda ustawiająca pionki w bazie na początku gry
     */
    private void setupPawns() {
        for (Player player : playersRef) {
            //System.out.println(" Game Board Pawns: " + player.getPawns());
            player.setupPawns();
        }
    }

    /**
     * Metoda usstawiająca kolizję każdemu pionkowi w grze
     * @param mL obiekt, który opisuje co ma się stać po naciśnięciu na daną kolizję
     */
    public void setupPawnBoxColliers(MouseListener mL) {
        for (Player player : playersRef) {
            for (Pawn pawn : player.getPawns()) {
                pawn.getCollisionBox().addMouseListener(mL);
                add(pawn.getCollisionBox());
            }
        }
    }

    /**
     * Aktualizacja wielkości okna i komponentów w nim się znajdujących
     */
    public void resize() {
        setPreferredSize(new Dimension((int)(Window.DEFAULT_PANE_WIDTH *(3f/4f)*gamePanelRef.getxScale()),(int)(Window.DEFAULT_PANE_HEIGHT*gamePanelRef.getyScale())));

        for (Player player : playersRef) {
            for (Pawn pawn : player.getPawns()) {
                pawn.resize();
            }
        }
    }

}
