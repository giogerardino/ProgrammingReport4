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
                System.out.println("Oxygen connection established from: " + oxygenSocket.getInetAddress() +"\n");

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
    
                    // Process the bond if there are enough molecules
                    if (hydrogenRequests.size() >= 2 && oxygenInput != null) {
                        processBond(hydrogenRequests, oxygenInput);
                    } else {
                        System.out.println("Insufficient molecules for bonding");
                    }
                }
            } catch (SocketException e) {
                // Connection was reset, handle gracefully
                System.out.println("\nConnection reset by client.");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    hydrogenSocket.close();
                    oxygenSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    
        private void processBond(List<String> hydrogenRequests, String oxygenInput) {
            // Form bond logic goes here
            String timeStamp = getCurrentTimeStamp();
            System.out.println("Bond formed at: " + timeStamp +"\n");
    
            // Send confirmation to hydrogen
            for (String hydrogenRequest : hydrogenRequests) {
                try {
                    PrintWriter hydrogenOut = new PrintWriter(hydrogenSocket.getOutputStream(), true);
                    hydrogenOut.println(hydrogenRequest + ", bonded, " + timeStamp);
                    hydrogenOut.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    
            // Send confirmation to oxygen
            try {
                PrintWriter oxygenOut = new PrintWriter(oxygenSocket.getOutputStream(), true);
                oxygenOut.println(oxygenInput + ", bonded, " + timeStamp);
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
