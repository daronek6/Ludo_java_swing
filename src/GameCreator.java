/**
 * Klasa służąca za stworzenie gry lokalnej (na tym komputerze)
 */
public class GameCreator {

    public GameCreator(int nOP,String[] ns, Window ref) {
        Player[] players = new Player[nOP];
        System.out.println("N of players: " + nOP);
        for(int i=0;i<nOP;i++) {
            players[i] = new Player(ns[i],i);
        }
        GamePanel gP = new GamePanel(players,ref);
        new GameLogic(nOP, players, gP);

        ref.add(gP);
    }
}
