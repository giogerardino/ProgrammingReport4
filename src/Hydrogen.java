import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Hydrogen {
    private static final AtomicInteger idCounter = new AtomicInteger(1);
    private int id;
    private Server server;
    private Set<String> sentRequests;

    public Hydrogen(Server server) {
        this.id = idCounter.getAndIncrement();
        this.server = server;
        this.sentRequests = new HashSet<>();
    }

    public synchronized void sendBondRequest() {
        if (sentRequests.contains("H" + id)) {
            System.out.println("Error: Duplicate bond request from H" + id);
            return;
        }
        server.receiveBondRequest("H" + id);
        sentRequests.add("H" + id);
        System.out.println("Sent bond request from H" + id);
    }

    public void receiveBondConfirmation() {
        System.out.println("Received bond confirmation for H" + id);
    }

    public static void main(String[] args) {
        Server server = new Server();
        Hydrogen hydrogen = new Hydrogen(server);
        hydrogen.sendBondRequest();
    }
}
