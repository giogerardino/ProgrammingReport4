import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Oxygen {
    private static final AtomicInteger idCounter = new AtomicInteger(1);
    private int id;
    private Server server;
    private Set<String> sentRequests;

    public Oxygen(Server server) {
        this.id = idCounter.getAndIncrement();
        this.server = server;
        this.sentRequests = new HashSet<>();
    }

    public synchronized void sendBondRequest() {
        if (sentRequests.contains("O" + id)) {
            System.out.println("Error: Duplicate bond request from O" + id);
            return;
        }
        server.receiveBondRequest("O" + id);
        sentRequests.add("O" + id);
        System.out.println("Sent bond request from O" + id);
    }

    public void receiveBondConfirmation() {
        System.out.println("Received bond confirmation for O" + id);
    }

    public static void main(String[] args) {
        Server server = new Server();
        Oxygen oxygen = new Oxygen(server);
        oxygen.sendBondRequest();
    }
}
