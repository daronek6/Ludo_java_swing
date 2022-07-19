/**
 * Klasa służąca do tworzenia gry sieciowej
 */
public class NetworkGameCreator {

    private NetworkGameLogic networkGameLogic;

    public NetworkGameCreator(int nOP, String[] names, ClientSide cS, Window winRef) {
        Player[] players = new Player[nOP];

        for(int i=0;i<nOP;i++) {
            players[i] = new Player(names[i], i);
            System.out.println("Network game creator: " + names[i]);
        }
        GamePanel gP = new GamePanel(players, winRef);
        networkGameLogic = new NetworkGameLogic(nOP,players,gP,cS);

        winRef.add(gP);
    }

    public NetworkGameLogic getNetworkGameLogic() {
        return networkGameLogic;
    }
}
