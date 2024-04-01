import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

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
                new HydrogenThread(hydrogenSocket).start();

                Socket oxygenSocket = oxygenServerSocket.accept();
                new OxygenThread(oxygenSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bond(Socket hydrogenSocket, Socket oxygenSocket) {
        try {
            ObjectOutputStream hydrogenOut = new ObjectOutputStream(hydrogenSocket.getOutputStream());
            ObjectOutputStream oxygenOut = new ObjectOutputStream(oxygenSocket.getOutputStream());

            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

            synchronized (hydrogenLock) {
                numHydrogen--;
                hydrogenOut.writeObject("bonded, " + timeStamp);
            }

            synchronized (oxygenLock) {
                numOxygen--;
                oxygenOut.writeObject("bonded, " + timeStamp);
            }

            System.out.println("Bond formed at: " + timeStamp);
        } catch (IOException e) {
            e.printStackTrace();
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

    private class HydrogenThread extends Thread {
        private Socket socket;

        public HydrogenThread(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String input;
                while ((input = in.readLine()) != null) {
                    if (input.equals("request")) {
                        receiveBondRequest("H");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class OxygenThread extends Thread {
        private Socket socket;

        public OxygenThread(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String input;
                while ((input = in.readLine()) != null) {
                    if (input.equals("request")) {
                        receiveBondRequest("O");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
