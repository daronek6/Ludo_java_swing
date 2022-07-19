import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 * Klasa sterująca grą sieciową od strony klienta
 */
public class NetworkGameLogic extends GameLogic {

    private ClientSide clientSide;
    private boolean freshStart;

    /**
     * Konstruktor klasy
     * @param nOP ilość graczy
     * @param ps odniesienie do graczy
     * @param gP odniesienie do okna przechowującego daną rozgrywkę klienta
     * @param cSRef odniesienie do obiektu odpowiedzialnego za komunikację z serwerem
     */
    public NetworkGameLogic(int nOP, Player[] ps, GamePanel gP, ClientSide cSRef) {
        super(nOP, ps, gP);
        clientSide = cSRef;
        freshStart = true;
        currentPlayerTurn = 0;
    }

    public Pawn[] getPawns(int ownerId) {
        return players[ownerId].getPawns();
    }

    /*
    public void setTurn(int whoStarts) {
        currentPlayerTurn = whoStarts;
    }
    */

    /**
     * Metoda ustawiająca wartość wylosowanej kostki, gdy inny klient losował
     * @param roll wylosowana wartość
     */
    public void setDiceRoll(int roll) {
        rolledNum = roll;
        controlBoardRef.updateDicePanel(roll);
        gamePanelRef.updateInterface();
        synchronized (rolledMsg) {
            rolledMsg.notify();
        }
    }

    @Override
    /**
     * @see GameLogic
     */
    public void mouseClicked(MouseEvent e) {
        if(currentPlayerTurn == clientSide.getPlayerId()) {
            super.mouseClicked(e);
        }
    }

    @Override
    /**
     * @see GameLogic
     */
    public void mousePressed(MouseEvent e) {
        if(currentPlayerTurn == clientSide.getPlayerId()) {
            super.mousePressed(e);
        }
    }

    @Override
    /**
     * @see GameLogic
     */
    public void actionPerformed(ActionEvent e) {
        if(currentPlayerTurn == clientSide.getPlayerId()) {
            super.actionPerformed(e);
        }
    }

    @Override
    /**
     * Metoda losująca kostką przez tego klienta i informująca innych klientów
     */
    protected void rollDice() {
        super.rollDice();
        String rollInfo = "roll| " + rolledNum + " " + currentPlayerTurn;
        clientSide.informServerAboutAction(rollInfo);
    }

    @Override
    /**
     * Metoda poruszająca pionkami klienta z zachowanymi regułami
     */
    protected void move(int ownerId, int id, Pawn pawn) {
        if(checkIfCanMove(ownerId, id) == true) {
            if(pawnSteps[ownerId][id] == -1) {
                pawnSteps[ownerId][id] = 0;
                pawn.setToStartField();
            }
            else {
                pawnSteps[ownerId][id] += rolledNum;
                if(pawnInHouse[ownerId][id] == false && pawnSteps[ownerId][id] >= maxStepsOnFields) {
                    pawnInHouse[ownerId][id] = true;
                    numOfPawnsInHouse[currentPlayerTurn]++;
                }
                moveAnimation(ownerId,id,pawn);
            }
            checkIfAnyPawnHit(pawnSteps[ownerId][id]);
            moved = true;

            synchronized (movedMsg) {
                movedMsg.notify();
            }

            if(currentPlayerTurn == clientSide.getPlayerId()) {
                String moveInfo = "move| " + ownerId + " " + id;
                clientSide.informServerAboutAction(moveInfo);
            }

        }
        else {
            controlBoardRef.updateInfoTA("Rusz innym pionkiem!");
        }
        gamePanelRef.updateInterface();
    }

    @Override
    /**
     * Metoda wykonująca się na odzielnym wątku kontrolująca stan gry klienta
     */
    public void gameLoop() {
        while(gameOver == false) {
            startTurn();
                synchronized (rolledMsg) {
                    try {
                        rolledMsg.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("After rolled");

                if (checkIfCanMoveAnyPawn() == false) {
                    controlBoardRef.updateInfoTA("Nie możesz ruszyć żadnym pionkiem!");

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    moved = true;
                    clientSide.informServerAboutAction("nomove| " + currentPlayerTurn);
                } else {
                    if(currentPlayerTurn == clientSide.getPlayerId()) {
                        controlBoardRef.updateInfoTA("Rusz swoim pionkiem!");
                    }

                    synchronized (movedMsg) {
                        try {
                            movedMsg.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                System.out.println("After moved");

                finishTurn();
            }
        gameOverAction();
    }

    @Override
    protected void startTurn() {
        if(freshStart) {
            freshStart = false;
            String info = "loaded| " + clientSide.getPlayerId();
            //clientSide.informServerAboutAction(info);
        }
        if(currentPlayerTurn == clientSide.getPlayerId()) {
            controlBoardRef.updateInfoTA("Twoja kolej!");
            controlBoardRef.enableRollButton();;
        }
        else {
            controlBoardRef.updateInfoTA("Czekaj na swoją kolej!");
        }
        controlBoardRef.updateDicePanel(0);
        gamePanelRef.updateInterface();
    }

    @Override
    public void gameOverAction() {
        clientSide.informServerAboutAction("gameover| " + currentPlayerTurn);
        super.gameOverAction();
    }

    public int getCurrentPlayerTurn() {
        return currentPlayerTurn;
    }

    /**
     * Metoda wywoływana tylko dla klientów, którzy nie mogą się poruszać, bo nie jest ich tura
     * @param ownerId id właściciela pionka
     * @param id id pionka
     * @param pawn odniesienie pionka
     */
    public void moveOtherPlayer(int ownerId, int id, Pawn pawn) {
        super.move(ownerId, id, pawn);
    }
}
