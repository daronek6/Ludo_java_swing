import com.sun.org.apache.bcel.internal.generic.NOP;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

/**
 * Klasa odpowiedzialna za logikę gry (sterowanie i kontrola)
 */
public class GameLogic implements MouseListener, ActionListener {

    protected GamePanel gamePanelRef;
    public ControlBoard controlBoardRef;

    protected Step[] nextFieldDirections = new Step[56];
    protected boolean[][] pawnInHouse;
    protected int[] numOfPawnsInHouse;
    protected int[][] pawnSteps;
    protected int[] startPos = {0,14,28,42};
    protected final int maxStepsOnFields = 56;
    protected final int maxSteps = 60;

    protected Player[] players;
    protected int numOfPlayers;
    protected int currentPlayerTurn;

    protected Random random;
    protected int rolledNum;

    protected static boolean rolled;
    protected static boolean moved;
    protected Object rolledMsg;
    protected Object movedMsg;

    protected boolean gameOver;
    protected GameLoopThread gameLoopThread;

    protected Timer timer;

    /**
     * Konstruktor klasy
     * @param nOP ilość graczy
     * @param ps odnośniek do graczy biorących udział w tej grze
     * @param gP odnośnik do okna na którym znajdują się komponenty odpowiedzialne za tą rozgrywkę
     */
    public GameLogic(int nOP, Player[] ps, GamePanel gP) {
        rolledMsg = new Object();
        movedMsg = new Object();

        pawnSteps = new int[nOP][4];
        pawnInHouse = new boolean[nOP][4];
        numOfPawnsInHouse = new int[nOP];

        random = new Random();
        gamePanelRef = gP;
        controlBoardRef = gamePanelRef.getControlBoard();
        numOfPlayers = nOP;
        players = ps;

        for(int i=0;i<numOfPlayers;i++) {
            numOfPawnsInHouse[i] = 0;

            for(int j=0;j<4;j++) {
                pawnSteps[i][j] = -1;
                pawnInHouse[i][j] = false;
            }
        }

        gameOver = false;
        rolled = false;
        moved = false;

        setupNextFieldDirections();
        gamePanelRef.addBoxColliersToGameBoard(this);
        controlBoardRef.setRollButtonActionListener(this);

        startingPlayer();

        gameLoopThread = new GameLoopThread(this);
        timer = new Timer(this, 20);

        gameLoopThread.start();
        //timer.start();
    }

    /**
     * Metoda losująca zaczynającego gracza
     */
    protected void startingPlayer() {
        currentPlayerTurn = random.nextInt(numOfPlayers);
    }

    /**
     * Metoda ustawiająca kierunki następnego pola
     */
    protected void setupNextFieldDirections() {
        int pos = 0;
        for(int i=0;i<6;i++) {
            nextFieldDirections[pos] = GamePanel.down;
            pos++;
        }
        for(int i=0;i<6;i++) {
            nextFieldDirections[pos] = GamePanel.right;
            pos++;
        }
        for(int i=0;i<2;i++) {
            nextFieldDirections[pos] = GamePanel.down;
            pos++;
        }

        for(int i=0;i<6;i++) {
            nextFieldDirections[pos] = GamePanel.left;
            pos++;
        }
        for(int i=0;i<6;i++) {
            nextFieldDirections[pos] = GamePanel.down;
            pos++;
        }
        for(int i=0;i<2;i++) {
            nextFieldDirections[pos] = GamePanel.left;
            pos++;
        }

        for(int i=0;i<6;i++) {
            nextFieldDirections[pos] = GamePanel.up;
            pos++;
        }
        for(int i=0;i<6;i++) {
            nextFieldDirections[pos] = GamePanel.left;
            pos++;
        }
        for(int i=0;i<2;i++) {
            nextFieldDirections[pos] = GamePanel.up;
            pos++;
        }

        for(int i=0;i<6;i++) {
            nextFieldDirections[pos] = GamePanel.right;
            pos++;
        }
        for(int i=0;i<6;i++) {
            nextFieldDirections[pos] = GamePanel.up;
            pos++;
        }
        for(int i=0;i<2;i++) {
            nextFieldDirections[pos] = GamePanel.right;
            pos++;
        }

    }
    @Override // Pawn interaction -------------------------
    /**
     * Metoda kontrolująca zdarzenie naciśnięcia na pionek
     */
    public void mouseClicked(MouseEvent e) {
        CollisionBox cb = (CollisionBox) e.getSource();
        System.out.println(cb.getInfo());
        if(rolled && cb.getPawn().getOwnerId() == currentPlayerTurn) {
            move(currentPlayerTurn,cb.getPawn().getId(),cb.getPawn());
        }
        gamePanelRef.updateInterface();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Metoda kontrolująca zdarzenie najechania myszką na pionek
     * @param e akcja myszki
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        if(rolled) {
            CollisionBox cb = (CollisionBox) e.getSource();
            Pawn thisPawn = cb.getPawn();
            if (currentPlayerTurn == thisPawn.getOwnerId()) {
                if (checkIfCanMove(thisPawn.getOwnerId(), thisPawn.getId())) {
                    gamePanelRef.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
            }
        }
    }
    /**
     * Metoda kontrolująca zdarzenie najechania myszką poza pionek
     * @param e akcja myszki
     */
    @Override
    public void mouseExited(MouseEvent e) {
        gamePanelRef.setCursor(Cursor.getDefaultCursor());
    }
    //------------------------------------------------------
    @Override //roll button action
    /**
     * Metoda odpowiedzialna za rzucenie kostką po naciśnięciu przycisku
     * @see ControlBoard
     */
    public void actionPerformed(ActionEvent e) {
        rollDice();
        synchronized (rolledMsg) {
            rolledMsg.notify();
        }
    }

    /**
     * Metoda losująca wartość rzuconej kostki
     */
    protected void rollDice() {
        controlBoardRef.disableRollButton();
        rolledNum = random.nextInt(6) + 1;
        controlBoardRef.updateDicePanel(rolledNum);
        rolled = true;
        System.out.println("Can move ANY of my pawns?:" + checkIfCanMoveAnyPawn());
        gamePanelRef.updateInterface();
    }

    /**
     * Metoda odpowiedzialna za przemieszczenie pionka zgodnie z regułami
     * @param ownerId id gracza tego pionka
     * @param id id tego pionka
     * @param pawn odniesienie do tego pionka
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
        }
        else {
            controlBoardRef.updateInfoTA("Rusz innym pionkiem!");
        }

    }

    /**
     * Metoda sprawdzająca czy naciśnięty pionek zbije inny pionek
     * @param pos pozycja globalna na planszy
     */
    protected void checkIfAnyPawnHit(int pos) {
        int difOwners = currentPlayerTurn;

        for(int i=0;i<(numOfPlayers-1);i++) {
            difOwners = (difOwners + 1) % numOfPlayers;
            for(int j=0;j<4;j++) {
                if(pawnSteps[difOwners][j] != -1 && (pawnSteps[difOwners][j]) < maxStepsOnFields && ((pawnSteps[difOwners][j] + startPos[difOwners]) % (maxStepsOnFields) == (pos + startPos[currentPlayerTurn]) % (maxStepsOnFields))) {
                    pawnSteps[difOwners][j] = -1;
                    players[difOwners].getPawns()[j].setToBase();
                    System.out.println("Hit!");
                }
            }
        }
    }

    /**
     * Metoda odpowiedzialna za wizualne przemieszczenie pionka
     * @param ownerId id właściciela pionka
     * @param id id pionka
     * @param pawn odniesienie do pionka
     */
    protected void moveAnimation(int ownerId, int id, Pawn pawn) {
        int currentPos = (startPos[ownerId] + pawnSteps[ownerId][id] - rolledNum) % (maxStepsOnFields);
        for(int i=0;i<rolledNum;i++) {
            if(pawnSteps[ownerId][id]-(rolledNum-i) < maxStepsOnFields-1) {
                pawn.move(nextFieldDirections[currentPos].xDir, nextFieldDirections[currentPos].yDir);
            }
            else {
                Step step = null;
                switch (currentPlayerTurn) {
                    case 0:
                        step = GamePanel.down;
                        break;
                    case 1:
                        step = GamePanel.left;
                        break;
                    case 2:
                        step = GamePanel.up;
                        break;
                    case 3:
                        step = GamePanel.right;
                        break;
                }
                pawn.move(step.xDir,step.yDir);
            }
            currentPos = (currentPos + 1) % (maxStepsOnFields);
        }
    }

    /**
     * Metoda sprawdzająca, czy dany gracz może ruszyć jakimkoliwek ze swoich pionków
     * @return czy może ruszyć jakikolwiek ze swoich pionkó
     */
    protected boolean checkIfCanMoveAnyPawn() {
        boolean canMove = false;
        int i = 0;
        while (canMove == false && i < 4) {
            canMove = checkIfCanMove(currentPlayerTurn,i);
            i++;
        }
        return canMove;
    }

    /**
     * Metoda sprawdzająca czy można ruszyć danym pionkiem
     * @param ownerId id właściciela pionka
     * @param id id pionka
     * @return czy można ruszyć
     */
    protected boolean checkIfCanMove(int ownerId, int id) {

        if((pawnSteps[ownerId][id] == -1) && !(rolledNum == 1 || rolledNum == 6)) {
            //System.out.println("Base AND NOT(1|6)");
            return false;
        }
        else if(pawnSteps[ownerId][id] == -1 && checkIfAnyAlliePawnOnStart(ownerId,id)) {
            //System.out.println("Base AND ANY on start pos");
            return false;
        }
        else if(checkIfAnyAlliePawnAhead(ownerId,id)){
            //System.out.println("Cant move allie pawn ahead");
            return false;
        }
        else if(checkIfReachEndOfBoard(ownerId,id)) {
            //System.out.println("Cant move field out of bounds");
            return false;
        }
        //System.out.println("Can move :)");
        return true;
    }

    /**
     * Metoda sprawdzająca, czy jakikolwiek pionek gracza jest na polu startowym
     * @param ownerId id właściciela pionka
     * @param id id pionka
     * @return czy jakikolwiek pionek gracza jest na polu startowym
     */
    protected boolean checkIfAnyAlliePawnOnStart(int ownerId, int id) {
        boolean anyOnStart = false;
        for(int i=(id+1)%4;(i!=id)&&(anyOnStart==false);i=(i+1)%4) {
            if(pawnSteps[ownerId][i] == 0) {
                anyOnStart = true;
            }
        }
        return anyOnStart;
    }

    /**
     * Metoda sprawdzająca czy jakikolwiek pionek tego samego gracza blokuje ruch wybranym pionkiem
     * @param ownerId id właściciela pionka
     * @param id id pionka
     * @return czy może ruszyć tym pionkiem
     */
    protected boolean checkIfAnyAlliePawnAhead(int ownerId, int id) {
        boolean anyAhead = false;
        for(int i=(id+1)%4;(i!=id)&&(anyAhead==false);i=(i+1)%4) {
            if(pawnSteps[ownerId][id] != -1 && (pawnSteps[ownerId][id]+rolledNum) == pawnSteps[ownerId][i]) {
                anyAhead = true;
            }
        }
        return anyAhead;
    }

    /**
     * Metoda sprawdzająca, czy ten pionek nie wyszedł by poza plansze
     * @param ownerId id właściciela pionka
     * @param id id pionka
     * @return czy ten pionek nie wyszedł by poza plansze
     */
    protected boolean checkIfReachEndOfBoard(int ownerId, int id) {
        boolean reach = false;
        if(pawnSteps[ownerId][id]+rolledNum > maxSteps) {
            reach = true;
        }
        return reach;
    }

    protected void forceRollAndMove() {
        rollDice();
        forceMove();

    }

    protected void forceMove() {
        boolean anyMovePossible = false;
        for(int i=0;i<4 && anyMovePossible==false;i++) {
            if(checkIfCanMove(currentPlayerTurn, i)) {
                anyMovePossible = true;
                move(currentPlayerTurn,i,players[currentPlayerTurn].getPawns()[i]);
            }
        }
        if(anyMovePossible==false) {

        }
        moved = true;
    }

    public void timeOutAction() {
        if(rolled==false) {
            forceRollAndMove();
            synchronized (rolledMsg) {
                rolledMsg.notify();
            }
        }
        else if(moved==false) {
            forceMove();
            synchronized (movedMsg) {
                movedMsg.notify();
            }
        }
        finishTurn();

    }

    /**
     * Metoda odpowiedzialna za zakończenie tury
     */
    protected void finishTurn() {
        checkIfGameOver();
        if(gameOver==false) {
            currentPlayerTurn = (currentPlayerTurn + 1) % numOfPlayers;
            rolled = false;
            moved = false;
        }
    }

    /**
     * Metoda odpowiedzialna za rozpoczęcie tury
     */
    protected void startTurn() {
        timer.resetTimer();
        rolled = false;
        moved = false;

        controlBoardRef.updateDicePanel(0);
        controlBoardRef.updatePlayersTurnNameTA(players[currentPlayerTurn].getName());
        controlBoardRef.enableRollButton();
        controlBoardRef.updateInfoTA("Rzuć kością!");
        gamePanelRef.updateInterface();
    }

    /**
     * Metoda kontrolująca stan gry
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
                controlBoardRef.updateInfoTA("Nie moesz ruszyć żadnym pionkiem!");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                moved = true;
            } else {
                controlBoardRef.updateInfoTA("Rusz swoim pionkiem!");

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

    /**
     * Metoda służąca do sprawdzenia, czy ktoś nie wygrał
     */
    protected void checkIfGameOver() {
        for(int i=0;i<numOfPlayers;i++) {
            if(numOfPawnsInHouse[i] == 4){
                gameOver = true;
            }
        }
    }

    /**
     * Metoda odpowiedzialna za obsługę gry, jeżeli ktoś wygrał
     */
    public void gameOverAction() {
        gamePanelRef.gameOverAction(players[currentPlayerTurn].getName());
    }

    public boolean getGameOver() {
        return gameOver;
    }

    public void updateTimeLeft(String tL) {
        controlBoardRef.updateTimer(tL);
    }


}
