import javax.swing.*;
import java.awt.event.MouseListener;

/**
 * Klasa potrzebna do wykrywania, czy naciśnięto na dany pionek
 */
public class CollisionBox extends JComponent {

    private int unscaledX;
    private int unscaledY;
    private int unscaledWidth;
    private int unscaledHeight;
    private int currentX;
    private int currentY;
    private Pawn pawnRef;

    /**
     * Konstruktor klasy
     * @param x pozycja na osi X bez skalowania
     * @param y pozycja na osi Y bez skalowania
     * @param w dlugosc detekcji naciśnięcia bez skalowania
     * @param h wysokość detekcji naciśnięcia bez skalowania
     * @param pRef odniesienie do pionka, który posiada ten wykrywacz kolizji
     */
    public CollisionBox(float x, float y, float w, float h, Pawn pRef) {
        pawnRef = pRef;
        currentX = unscaledX = (int) x;
        currentY = unscaledY = (int) y;
        unscaledWidth = (int) w;
        unscaledHeight = (int) h;
        setBounds(unscaledX,unscaledY,unscaledWidth,unscaledHeight);
    }

    /**
     *Metoda ustawiająca położenie kolizji
     * @param timesRight ile razy przesunąć w prawo (jeśli minus to lewo) o dlugosc detekcji naciśnięcia bez skalowania
     * @param timesDown ile razy przesunąć w dół (jeśli minus to górę) o wysokość detekcji naciśnięcia bez skalowania
     */
    public void move(int timesRight, int timesDown) {
        System.out.println("CollisionBox Current pos X:" + currentX + " Y: " + currentY);
        currentX += unscaledWidth*timesRight;
        currentY += unscaledHeight*timesDown;
        System.out.println("CollisionBox New pos X:" + currentX + " Y: " + currentY);
        setBounds(currentX,currentY,unscaledWidth, unscaledHeight);
    }

    /**
     * Metoda odpowiedzialna z aktualizację rzeczywistej wielkości kolizji z uwzględnieniem skali okna
     */
    public void resize() {
        setBounds((int)(currentX*GamePanel.xScale),(int)(currentY*GamePanel.yScale),(int)(unscaledWidth*GamePanel.xScale),(int)(unscaledHeight*GamePanel.yScale));
    }

    /**
     * Ustawienie na pole startowe kolizji
     * @param x X startowe dla tego pionka (gracza)
     * @param y Y startowe dla tego pionka (gracza)
     */
    public void setToStartField(float x, float y) {
        currentX = (int) x;
        currentY = (int) y;
    }

    /**
     * Przywrócenie pozycji kolizji do startowego miejsca
     */
    public void setToBase() {
        currentX = unscaledX;
        currentY = unscaledY;
    }

    public String getInfo() {
        return pawnRef.getInfo();
    }

    public Pawn getPawn() {
        return pawnRef;
    }
}
