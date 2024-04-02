import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Server {

    private static final int HYDROGEN_PORT = 50000;
    private static final int OXYGEN_PORT = 60000;

    private ServerSocket hydrogenServerSocket;
    private ServerSocket oxygenServerSocket;

    private volatile int numHydrogen = 0;
    private volatile int numOxygen = 0;

    private final Object hydrogenLock = new Object();
    private final Object oxygenLock = new Object();

    private long firstBondRequestTime = 0;
    private long lastBondCompletionTime = 0;

    public void start() {
        try {
            hydrogenServerSocket = new ServerSocket(HYDROGEN_PORT);
            oxygenServerSocket = new ServerSocket(OXYGEN_PORT);
            InetAddress inetAddress = InetAddress.getLocalHost();
            System.out.println("Server opened at: " + inetAddress.getHostAddress());
            System.out.println("Server is listening...");

            while (true) {
                Socket hydrogenSocket = hydrogenServerSocket.accept();
                System.out.println("Hydrogen connection established from: " + hydrogenSocket.getInetAddress());

                Socket oxygenSocket = oxygenServerSocket.accept();
                System.out.println("Oxygen connection established from: " + oxygenSocket.getInetAddress() + "\n");

                new BondThread(hydrogenSocket, oxygenSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class BondThread extends Thread {
        private Socket hydrogenSocket;
        private Socket oxygenSocket;
    
        public BondThread(Socket hydrogenSocket, Socket oxygenSocket) {
            this.hydrogenSocket = hydrogenSocket;
            this.oxygenSocket = oxygenSocket;
        }
    
        public void run() {
            long firstBondRequestTime = System.currentTimeMillis();
            long lastBondCompletionTime = 0;
    
            try {
                BufferedReader hydrogenIn = new BufferedReader(new InputStreamReader(hydrogenSocket.getInputStream()));
                BufferedReader oxygenIn = new BufferedReader(new InputStreamReader(oxygenSocket.getInputStream()));
    
                while (!hydrogenSocket.isClosed() && !oxygenSocket.isClosed()) {
                    List<String> hydrogenRequests = new ArrayList<>();
    
                    // Read and process hydrogen requests until at least 2 are received
                    String hydrogenInput;
                    while (hydrogenRequests.size() < 2 && (hydrogenInput = hydrogenIn.readLine()) != null) {
                        System.out.println("Received hydrogen request: " + hydrogenInput);
                        hydrogenRequests.add(hydrogenInput);
                    }
    
                    // Read the oxygen request
                    String oxygenInput = oxygenIn.readLine();
                    System.out.println("Received oxygen request: " + oxygenInput);
                    System.out.flush();
    
                    // Process the bond if there are enough molecules
                    if (hydrogenRequests.size() >= 2 && oxygenInput != null) {
                        processBond(hydrogenRequests, oxygenInput);
                        lastBondCompletionTime = System.currentTimeMillis();
                    } else {
                        System.out.println("Insufficient molecules for bonding");
                        this.oxygenSocket.close();
                        this.hydrogenSocket.close();
                    }
                }
            } catch (SocketException e) {
                System.out.println("\nConnection reset by client.");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Close sockets
                try {
                    hydrogenSocket.close();
                    oxygenSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Compute and print execution time
                long executionTime = lastBondCompletionTime - firstBondRequestTime;
                System.out.println("\nFirst bond request: " + firstBondRequestTime);
                System.out.println("Last bond completion: " + lastBondCompletionTime);
                System.out.println("Total execution time: " + executionTime + " milliseconds");
            }
        }
    
        private void processBond(List<String> hydrogenRequests, String oxygenInput) {
            // Form bond logic goes here
            String timeStamp = getCurrentTimeStamp();
            System.out.println("Bond formed at: " + timeStamp + "\n");
    
            // Send confirmation to hydrogen
            for (String hydrogenRequest : hydrogenRequests) {
                try {
                    PrintWriter hydrogenOut = new PrintWriter(hydrogenSocket.getOutputStream(), true);
                    String moleculeID = hydrogenRequest.split(",")[0];
                    hydrogenOut.println(moleculeID + ", bonded, " + timeStamp);
                    hydrogenOut.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                PrintWriter oxygenOut = new PrintWriter(oxygenSocket.getOutputStream(), true);
                String moleculeID = oxygenInput.split(",")[0]; // Extract the molecule ID from the request
                oxygenOut.println(moleculeID + ", bonded, " + timeStamp);
                oxygenOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    
        private String getCurrentTimeStamp() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        }
    }
    

    public synchronized void receiveBondRequest(String moleculeID) {
        if (moleculeID.startsWith("H")) {
            synchronized (hydrogenLock) {
                numHydrogen++;
            }
        } else if (moleculeID.startsWith("O")) {
            synchronized (oxygenLock) {
                numOxygen++;
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
