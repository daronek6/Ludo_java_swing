import java.io.IOException;
import java.net.*;

/**
 * Klasa kontorlująca grę po stronie klienta
 */
public class ClientSide extends Thread {

    private Window windowRef;
    private JoinGameMenu joinGameMenuRef;
    private NetworkGameLogic networkGameLogicRef;

    private DatagramSocket socket;
    private DatagramPacket packet;
    private InetAddress serverAddress;
    private byte[] buf;
    private String name;
    private int id;
    private boolean gameOver;

    //public Object freshStartMsg;

    /**
     * Konstruktor klasy dla klientów dołączających do gry z menu dołączania do gry
     * @param nme nazwa gracza
     * @param winRef odniesienie do głównego okna programu
     * @param jGMRef odniesienie do menu lobby gry, z którego została utworzona ta sieciowa rozgrywka
     */
    public ClientSide(String nme, Window winRef, JoinGameMenu jGMRef) {
        this(nme, winRef);
        joinGameMenuRef = jGMRef;
    }

    /**
     * Konstruktor klasy dla klienta, który tworzy serwer
     * @param nme nazwa gracza
     * @param winRef odniesienie do głównego okna programu
     */
    public ClientSide(String nme, Window winRef) {
        System.out.println("New client : " + nme);
        windowRef = winRef;
        serverAddress = null;

        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            System.out.println("Client got new socket? " + !(socket == null));
            e.printStackTrace();
        }

        buf = new byte[512];
        packet = new DatagramPacket(buf,buf.length);

        name = nme;
        //freshStartMsg = new Object();
    }

    /**
     * Metoda umożliwiająca połączenie się klienta z serwerem w fazie lobby
     * @param ip adres komputera na którym jest serwer (adres w sieci lokalnej)
     */
    public void joinGame(String ip)  {
        try {
            System.out.println("Client got new socket? " + !(socket == null));
            socket.setSoTimeout(4000);
            serverAddress = InetAddress.getByName(ip);
            buf = name.getBytes();
            DatagramPacket connectPacket = new DatagramPacket(buf,buf.length,serverAddress,GameServer.GAME_PORT);

            socket.send(connectPacket);
            socket.receive(packet);
            decodePacket(packet);

            if(joinGameMenuRef != null) {
                joinGameMenuRef.disableJoinButton();
            }

            gameOver = false;
            socket.setSoTimeout(0);

            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    /**
     * Metoda obsługująca zdarzenia związane z komunikacją z serwerem za pomocą oddzielnego wątku
     */
    public void run() {
        super.run();

        while (gameOver == false) {
            try {
                socket.receive(packet);
                decodePacket(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(networkGameLogicRef != null) {
                gameOver = networkGameLogicRef.getGameOver();
            }
        }
    }

    /**
     * Metoda odkodowująca zdarzenie otrzymane od serwera
     * @param pckt pakiet (UDP) otrzymany od serwera
     */
    private void decodePacket(DatagramPacket pckt) {
        boolean myTurn = false;
        if(networkGameLogicRef != null && networkGameLogicRef.getCurrentPlayerTurn() == id) {
            myTurn = true;
        }
        String data = new String(pckt.getData(),0,pckt.getLength());
        System.out.println(name + " Data: "+ data);
        String prefix;
        int i=0;
        while (i<data.length() && data.charAt(i) != '|') {
            i++;
        }
        prefix = data.substring(0,i);
        System.out.println(prefix);

        switch (prefix) {
            case "id":
                setId(data);
                System.out.println(name + " Set my id: " + data);
                break;
            case "lobbyinfo":
                updateLobbyInfo(data);
                break;
            case "start":
                startGame(data);
                break;
            case "stop":
                if(joinGameMenuRef != null) {
                    joinGameMenuRef.enableJoinButton();
                }
                break;
            case "startingplayer":
                //setTurn(data);
                break;
            case "move":
                if(!myTurn) {
                    move(data);
                }
                break;
            case "roll":
                rollDice(data);
                break;
            case "gameover":
                System.out.println("Client side gameOver!");
                    break;
            case "nomove":
                noMove(data);
                System.out.println("Noone could move!");
                break;
        }
    }

    /**
     * Ustawienie id dla tego klienta
     * @param data sformatowana informacja od serwera
     */
    private void setId(String data) {
        try {
            id = Integer.parseInt(data.substring(4,5));
            System.out.println(name + " id: " + id);
        }catch (Exception e) {
            System.out.println("Bad Id");
            e.printStackTrace();
        }
    }

    /**
     * Metoda opisująca zachowanie, gdy żaden pionek nie mógł być ruszony
     * @param data sformatowana informacja od serwera
     */
    private void noMove(String data) {
        int ownerId = Integer.parseInt(data.substring(8,9));
        if(networkGameLogicRef != null && ownerId != id) {
            synchronized (networkGameLogicRef.movedMsg) {
                networkGameLogicRef.movedMsg.notify();
            }
        }
    }

    /**
     * Metoda aktualizująca informacje o połączeniu z lobby serwera
     * @param data sformatowana informacja od serwera
     */
    private void updateLobbyInfo(String data) {
        try {
            if(joinGameMenuRef != null) {
                String info = data.substring(11,data.length());
                joinGameMenuRef.updateInfoTA(info);
            }
        }
        catch (Exception e) {
            System.out.println("Bad info");
            e.printStackTrace();
        }
    }

    /**
     * Metoda rozpoczynająca grę dla klienta
     * @param data sformatowana informacja od serwera
     */
    private void startGame(String data) {
        System.out.println(name + " Start game! ");
        int numOfPlrs = 0;

        numOfPlrs = Integer.parseInt(data.substring(7,8));
        String[] names = new String[numOfPlrs];

        int startNameIndx = 8;
        int endNameIndx = 8;
        boolean end;

        for(int i=0;i<numOfPlrs;i++) {
            end = false;
            while (end == false) {
                if(data.charAt(endNameIndx) == '|') {
                    end = true;
                }
                else {
                    endNameIndx++;
                }
            }
            names[i] = data.substring(startNameIndx,endNameIndx);
            startNameIndx = endNameIndx+1;
            endNameIndx++;
        }

        if(joinGameMenuRef != null) {
            joinGameMenuRef.setVisible(false);
        }

        NetworkGameCreator nGC = new NetworkGameCreator(numOfPlrs,names,this,windowRef);
        networkGameLogicRef = nGC.getNetworkGameLogic();
    }

    public int getPlayerId() {
        return id;
    }

    private void setTurn(String data) {
        System.out.println(name + " setTurn");
        int whoStarts = -1;

        whoStarts = Integer.parseInt(data.substring(16,17));
        while (networkGameLogicRef == null) {
            System.out.flush();
        }

        System.out.println("Client set turn id: " + id);
       // networkGameLogicRef.setTurn(whoStarts);

    }

    /**
     * Metoda odpowiadająca za poruszenie otrzymanego od serwera pionka
     * @param data sformatowana informacja od serwera
     */
    private void move(String data) {
        System.out.println(name + " move");
            int ownerId = -1;
            int pawnId = -1;

            ownerId = Integer.parseInt(data.substring(6, 7));
            pawnId = Integer.parseInt(data.substring(8, 9));

            if(id != ownerId) {
                networkGameLogicRef.moveOtherPlayer(ownerId, pawnId, networkGameLogicRef.getPawns(ownerId)[pawnId]);
            }

    }

    /**
     * Metoda ustawiająca wylosowaną wartość przez innego klienta
     * @param data sformatowana informacja od serwera
     */
    private void rollDice(String data) {
        System.out.println(name + " rollDice");
            int roll = -1;
            int ownerId = -1;
            roll = Integer.parseInt(data.substring(6, 7));
            ownerId = Integer.parseInt(data.substring(8,9));

            if(ownerId != id) {
                networkGameLogicRef.setDiceRoll(roll);
            }
    }

    /**
     * Metoda wysyłająca informację o akcji do serwera. Serwer potem rozsyła informację do pozostałych klientów
     * @param data sformatowana informacja dla serwera
     */
    public void informServerAboutAction(String data) {
        System.out.println(name + " action " + data);
        buf = data.getBytes();
        DatagramPacket infoPacket = new DatagramPacket(buf,buf.length,serverAddress,GameServer.GAME_PORT);
        try {
            socket.send(infoPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
