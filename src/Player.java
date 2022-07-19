import java.awt.*;

/**
 * Klasa reprezentująca gracza
 */
public class Player {

    private String name;
    private int id;
    private Pawn[] pawns;

    public int x = 0;

    /**
     * Konstruktor klasy
     * @param n nazwa gracza
     * @param idd id gracza
     */
    public Player(String n, int idd) {
        name = n;
        id = idd;
        pawns = new Pawn[4];
    }

    /**
     * Metoda tworząca odpowiedni zestaw pionków
     */
    public void setupPawns() {
        Color pawnColor = BaseColors.baseColors[id];

        float alfa = (float)((Math.PI)/2f);
        float xDir = -1;
        float yDir = -1;

        switch (id) {
            case 0:
                xDir = 1;
                yDir = 0;
                break;
            case 1:
                xDir = 1;
                yDir = 1;
                break;
            case 2:
                xDir = 0;
                yDir = 1;
                break;
            case 3:
                xDir = 0;
                yDir = 0;
        }

        float currentX = GameBoard.START_POS_X + 3*GameBoard.BASE_FIELD_GAP + (xDir * (GameBoard.BASE_RECT_SIDE + GameBoard.BASE_GAP ));
        float currentY = GameBoard.START_POS_Y + GameBoard.BASE_FIELD_GAP + (yDir * (GameBoard.BASE_RECT_SIDE + GameBoard.BASE_GAP ));

        float baseCurrentX = currentX;
        float baseCurrentY = currentY;

        for(int i=0;i<4;i++) {

            float baseXDir = (float) Math.sin((-i)*alfa);
            float baseYDir = (float) Math.cos(i*alfa);

            pawns[i] = new Pawn(baseCurrentX,baseCurrentY,GameBoard.FIELD_SIDE ,pawnColor,id,i);

            //System.out.println("Setup : ");
            //System.out.println("X: " + baseCurrentX + "Y: " + baseCurrentY);
            baseCurrentX += baseXDir*2*GameBoard.BASE_FIELD_GAP;
            baseCurrentY += baseYDir*2*GameBoard.BASE_FIELD_GAP;
        }
        //System.out.println(" Setup Pawns: " + pawns);
    }

    public Pawn[] getPawns() {
        return pawns;
    }
    public String getName() {
        return name;
    }
}
