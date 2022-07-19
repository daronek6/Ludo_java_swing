import java.util.concurrent.Semaphore;

/**
 * Klasa wykonująca kontrolę stanu gry na oddzielnym wątku
 */
public class GameLoopThread extends Thread {

    private GameLogic gameLogicRef;

    public GameLoopThread(GameLogic gL) {
        gameLogicRef = gL;
    }

    @Override
    public void run() {
        gameLogicRef.gameLoop();
    }
}
