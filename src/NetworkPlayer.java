import java.net.InetAddress;

/**
 * Klasa opisująca połączonego z serwerem klienta
 */
public class NetworkPlayer {

    private InetAddress networkAddress;
    private int port;
    private String name;

    public NetworkPlayer(String nam, int prt, InetAddress address) {
        name = nam;
        port = prt;
        networkAddress = address;
    }

    public String getName() {
        return name;
    }

    public InetAddress getNetworkAddress() {
        return networkAddress;
    }

    public int getPort() {
        return port;
    }
}
