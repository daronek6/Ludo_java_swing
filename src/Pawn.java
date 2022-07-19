import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Klasa opisująca pionek
 */
public class Pawn  {

    private Color color;
    private int ownerId;
    private int id;
    private CollisionBox collisionBox;

    private float unscaledWidth;
    private float unscaledHeight;
    private float unscaledX;
    private float unscaledY;

    private float currentX;
    private float currentY;

    /**
     * Konstruktor klasy
     * @param xx początkowa pozycja na osi X
     * @param yy początkowa pozycja na osi Y
     * @param rad promień pionka
     * @param col kolor pionka
     * @param oId id właściciela pionka
     * @param idd id pionka
     */
    public Pawn(float xx, float yy, float rad, Color col, int oId, int idd) {
        currentX = xx;
        currentY = yy;
        unscaledWidth = rad;
        unscaledHeight = rad;
        unscaledX = xx;
        unscaledY = yy;
        color = col;
        ownerId = oId;
        id = idd;

        System.out.println("Creating : ");
        System.out.println("X: " + unscaledX + "Y: " + unscaledY);
        collisionBox = new CollisionBox(unscaledX,unscaledY,unscaledWidth,unscaledHeight,this);
    }

    /**
     * Metoda przemieszczająca pionek
     * @param timesR ile razy przesunąć w prawo (jeśli minus to lewo) o dlugosc detekcji naciśnięcia bez skalowania
     * @param timesD ile razy przesunąć w dół (jeśli minus to górę) o wysokość detekcji naciśnięcia bez skalowania
     */
    public void move(int timesR, int timesD) {
        System.out.println(" Pawn Current pos X:" + currentX + " Y: " + currentY);
        currentX += GameBoard.FIELD_SIDE *(float)timesR;
        currentY += GameBoard.FIELD_SIDE *(float)timesD;
        System.out.println("Pawn New pos X:" + currentX + " Y: " + currentY);
        collisionBox.move(timesR, timesD);

    }

    /**
     * Ustawienie pionka na polu startowym
     */
    public void setToStartField() {
        currentX = GameBoard.START_POS_X + GameBoard.BASE_RECT_SIDE + 2*GameBoard.FIELD_SIDE;
        currentY = GameBoard.START_POS_Y;

        int localTimesX = 8;
        int localTimesY = 6;
        int tmp;

        for(int i=0;i<ownerId;i++) {
            float xDir;
            float yDir;

            tmp = localTimesX;
            localTimesX = localTimesY;
            localTimesY = tmp;

            if(i==0) {
                xDir = GamePanel.downRight.xDir;
                yDir = GamePanel.downRight.yDir;
            }
            else if(i==1) {
                xDir = GamePanel.downLeft.xDir;
                yDir = GamePanel.downLeft.yDir;
            }
            else if(i==2) {
                xDir = GamePanel.upLeft.xDir;
                yDir = GamePanel.upLeft.yDir;
            }
            else {
                xDir = GamePanel.upRight.xDir;
                yDir = GamePanel.upRight.yDir;
            }
            currentX += xDir*localTimesX*GameBoard.FIELD_SIDE;
            currentY += yDir*localTimesY*GameBoard.FIELD_SIDE;

        }
        collisionBox.setToStartField(currentX, currentY);

    }

    /**
     * Wrócenie pionka do pozycji startowej
     */
    public void setToBase() {
        currentX = unscaledX;
        currentY = unscaledY;

        collisionBox.setToBase();
    }

    /**
     * Zmiana wielkości kolizji pionka
     */
    public void resize() {
        collisionBox.resize();
    }

    /**
     * Stworzenie obiektu możliwego do rysowania pionka
     * @return obiekt reprezentujący pionek
     */
    public Ellipse2D.Float createEllipse() {
        return new Ellipse2D.Float(currentX*GamePanel.xScale,currentY*GamePanel.yScale,unscaledWidth*GamePanel.xScale,unscaledHeight*GamePanel.yScale);
    }

    public Color getColor() {
        return color;
    }

    public CollisionBox getCollisionBox() {
        return collisionBox;
    }

    public String getInfo() {
        return "Player Id: " + ownerId + " Pawn Id: " + id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getId() {
        return id;
    }

}
