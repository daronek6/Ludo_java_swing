import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Klasa serwera synchronizującego grę klientów w tej samej grze sieciowej
 */
public class GameServer extends Thread {
    public static final int GAME_PORT = 10001;

    private DatagramSocket serverSocket;
    private NetworkLobbyMenu networkLobbyMenuRef;
    private InetAddress address;
    private ArrayList<NetworkPlayer> networkPlayers;
    private DatagramPacket packet;
    private byte[] buf;

    private boolean startGame;
    private boolean gameOver;
    private int numOfPlayers;

    /**
     * Konstruktor klasy
     * @param ip adres hosta serwera
     * @param nLRef odniesienie do okna lobby gry sieciowej
     */
    public GameServer(String ip, NetworkLobbyMenu nLRef) {
        networkLobbyMenuRef = nLRef;
        try {
            address = InetAddress.getByName(ip);
            serverSocket = new DatagramSocket(GAME_PORT, address);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        buf = new byte[512];

        packet = new DatagramPacket(buf,buf.length);

        startGame = false;
        gameOver = false;

        numOfPlayers = 0;

        networkPlayers = new ArrayList<>();

        start();
    }

    @Override
    /**
     * Metoda kontrolująca działanie serwera na innym wątku
     */
    public void run() {
        super.run();

        try {
            serverSocket.setSoTimeout(1500);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        while (startGame == false) {
            waitForPlayers();
        }

        try {
            serverSocket.setSoTimeout(0);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        //System.out.println("Waiting for all players to Load!");
        //waitForAllPlayersToLoad();
        //ystem.out.println("roll starting player!");
        //rollStartingPlayerAndInform();

        while (gameOver == false) {
            System.out.println("waiting for player action!");
            receiveGameLogicInfo();
            System.out.println("resending player action!");
            resendToOtherPlayers();
        }
        System.out.println("Server closed!");
    }

    /**
     * Metoda oczekująca na połączenie klientów z serwerem
     */
    private void waitForPlayers() {
        String info = "lobbyinfo| Brak miejsc!";

        try {
            System.out.println("Wait For Players");
            if(startGame == false) {
                serverSocket.receive(packet);

                if (numOfPlayers < 4) {
                    String name = new String(packet.getData(), 0, packet.getLength());
                    System.out.println("Wait for players: " + name);
                    InetAddress address = packet.getAddress();

                    int port = packet.getPort();
                    NetworkPlayer newPlayer = new NetworkPlayer(name, port, address);

                    String idString = "id| " + numOfPlayers;
                    networkPlayers.add(newPlayer);

                    networkLobbyMenuRef.updatePlayerName(numOfPlayers, name);
                    buf = idString.getBytes();
                    DatagramPacket idPacket = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());
                    serverSocket.send(idPacket);
                    numOfPlayers++;

                    networkLobbyMenuRef.updateNumOfPlayerConnected(numOfPlayers);
                    if(startGame == false) {
                        informWaitingPlayers();
                    }
                } else {
                    buf = info.getBytes();
                    DatagramPacket infoPacket = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());
                    if(startGame == false) {
                        serverSocket.send(infoPacket);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda aktualizująca infromacje klienta o jego połączeniu
     */
    private void informWaitingPlayers() {
        String info = "lobbyinfo| Dołączono " + numOfPlayers + "/4";
        for(NetworkPlayer player : networkPlayers) {
            if(player != null) {
                buf = info.getBytes();
                DatagramPacket infoPacket = new DatagramPacket(buf,buf.length,player.getNetworkAddress(),player.getPort());
                try {
                    serverSocket.send(infoPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Metoda rozpoczynająca nową grę sieciową
     */
    public void startNewGame() {
        startGame = true;
        try {
            sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String startGameInfo = "start| " + numOfPlayers;
        for(NetworkPlayer player : networkPlayers) {
            if(player != null) {
                startGameInfo += player.getName()+"|";
            }
        }
        for(NetworkPlayer player : networkPlayers) {
            buf = startGameInfo.getBytes();
            DatagramPacket startGamePacket = new DatagramPacket(buf,buf.length,player.getNetworkAddress(),player.getPort());
            try {
                serverSocket.send(startGamePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Metoda informująca klientów, jeżeli zamknięto serwer, gdy był w stanie lobby
     */
    public void informAboutDisconnect() {
        String disconnectInfo1 = "lobbyinfo| Serwer został zamknięty!";
        String disconnectInfo2 = "stop|";

        buf = disconnectInfo1.getBytes();
        for(NetworkPlayer player : networkPlayers) {
            DatagramPacket infoPacket = new DatagramPacket(buf,buf.length,player.getNetworkAddress(),player.getPort());
            try {
                serverSocket.send(infoPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        buf = disconnectInfo2.getBytes();

        try {
            sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(NetworkPlayer player : networkPlayers) {
            DatagramPacket infoPacket = new DatagramPacket(buf,buf.length,player.getNetworkAddress(),player.getPort());
            try {
                serverSocket.send(infoPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Metoda zatrzymująca serwer
     */
    public void stopServer() {
        informAboutDisconnect();
        this.stop();
        if(serverSocket != null) {
            serverSocket.close();
        }
    }

    public InetAddress getServerAddress() {
        return address;
    }

    private void waitForAllPlayersToLoad() {
        System.out.println("server waitForAllPlayersToLoad");
        boolean[] ready = new boolean[numOfPlayers];
        boolean allReady = true;

        for (boolean rdy: ready) {
            rdy = false;
        }
        String data;
        String prefix;
        int id;

        do {
            allReady = true;
            DatagramPacket info = new DatagramPacket(buf, buf.length);
            try {
                serverSocket.receive(info);

                data = new String(info.getData(),0,info.getLength());
                System.out.println("Waiting for all players to load: " + data);
                prefix = getPrefix(data);
                id = -1;

                if(prefix == "loaded") {
                    id = Integer.parseInt(data.substring(8,data.length()));

                    ready[id] = true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            for(boolean rdy : ready) {
                if(rdy == false) {
                    allReady = false;
                }
            }
        } while (allReady == false);
        System.out.println("All loaded!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!?");

    }

    private void rollStartingPlayerAndInform() {
        System.out.println("server rollStartingPlayerAndInform");
        Random random = new Random();
        int roll = random.nextInt(numOfPlayers);

        String info = "startingplayer| " + roll;
        buf = info.getBytes();
        for(NetworkPlayer player : networkPlayers) {
            DatagramPacket infoPacket = new DatagramPacket(buf, buf.length, player.getNetworkAddress(), player.getPort());
            try {
                serverSocket.send(infoPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Metoda czekająca na informacje o zmienie stanu gry od klientów
     */
    public void receiveGameLogicInfo() {
        try {

            serverSocket.receive(packet);
            buf = packet.getData();

            String data = new String(packet.getData(),0,packet.getLength());
            System.out.println("server receiveGameLogicInfo " + data);
            String prefix = getPrefix(data);

            if(prefix == "gameover") {
                gameOver = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda rozsyłająca stan gry do pozostałych klientów
     */
    public void resendToOtherPlayers() {

        DatagramPacket infoPacket;
        for(NetworkPlayer player: networkPlayers) {
            infoPacket = new DatagramPacket(buf, buf.length, player.getNetworkAddress(), player.getPort());
            try {
                serverSocket.send(infoPacket);
                System.out.println("server resendToOtherPlayers " + new String(buf,0,buf.length));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Metoda rokodywująca informację zawartą w otrzymanym pakiecie
     * @param data Tekst z pakietu
     * @return polecienie z pakietu
     */

    private String getPrefix(String data) {
        String prefix = "";
        int i=0;
        while (i<data.length() && data.charAt(i) != '|') {
            i++;
        }
        prefix = data.substring(0,i);

        return prefix;
    }
}
