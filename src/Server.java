import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

public class Server {
    private Set<String> hydrogenRequests;
    private Set<String> oxygenRequests;
    private Set<String> sentConfirmations;

    public Server() {
        hydrogenRequests = new HashSet<>();
        oxygenRequests = new HashSet<>();
        sentConfirmations = new HashSet<>();
    }

    public void startServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ServerThread(clientSocket, this).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized void receiveBondRequest(String requestId) {
        char type = requestId.charAt(0);
        if (type == 'H') {
            if (hydrogenRequests.contains(requestId)) {
                System.out.println("Error: Duplicate bond request from " + requestId);
                return;
            }
            hydrogenRequests.add(requestId);
            checkAndBond();
        } else if (type == 'O') {
            if (oxygenRequests.contains(requestId)) {
                System.out.println("Error: Duplicate bond request from " + requestId);
                return;
            }
            oxygenRequests.add(requestId);
            checkAndBond();
        }
    }

    private void checkAndBond() {
        if (hydrogenRequests.size() >= 2 && oxygenRequests.size() >= 1) {
            String hydrogen1 = hydrogenRequests.iterator().next();
            hydrogenRequests.remove(hydrogen1);
            String hydrogen2 = hydrogenRequests.iterator().next();
            hydrogenRequests.remove(hydrogen2);
            String oxygen = oxygenRequests.iterator().next();
            oxygenRequests.remove(oxygen);

            if (sentConfirmations.contains(hydrogen1) || sentConfirmations.contains(hydrogen2) || sentConfirmations.contains(oxygen)) {
                System.out.println("Error: Bond confirmation sent before request");
                return;
            }

            sendBondConfirmation(hydrogen1);
            sendBondConfirmation(hydrogen2);
            sendBondConfirmation(oxygen);
        }
    }

    private void sendBondConfirmation(String requestId) {
        System.out.println("Sending bond confirmation for " + requestId);
        sentConfirmations.add(requestId);
        // Send confirmation message to client
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.startServer(12345); // Choose an available port
    }

    private static class ServerThread extends Thread {
        private Socket socket;
        private Server server;
        public ServerThread(Socket socket, Server server) {
            this.socket = socket;
            this.server = server;
        }
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                String request;
                while ((request = in.readLine()) != null) {
                    server.receiveBondRequest(request); // Call receiveBondRequest of the Server instance
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
