import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Oxygen {
    private final int OXYGEN_PORT = 60000;
    private final String SERVER_IP = "localhost";
    private final int id;

    public Oxygen(int id) {
        this.id = id;
    }

    public void start() {
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            socket = new Socket(SERVER_IP, OXYGEN_PORT);
            System.out.println("Connected to server");
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Random random = new Random();

            Scanner scanner = new Scanner(System.in);
            int m = 0;
            do {
                System.out.println("Enter the number of oxygen molecules:");
                m = scanner.nextInt();
                if (m <= 0)
                    System.out.println("Enter a valid number of oxygen molecules");
            } while (m <= 0);

            long startTime = System.currentTimeMillis();

            AtomicBoolean bondingComplete = new AtomicBoolean(false); // Flag to track if bonding is complete

            // Start a separate thread to continuously listen for server responses
            int finalM = m;
            BufferedReader finalIn = in;
            Thread responseThread = new Thread(() -> {
                try {
                    String serverResponse;
                    int counter = 0;
                    while (counter < finalM && !bondingComplete.get() && (serverResponse = finalIn.readLine()) != null) {
                        System.out.println(serverResponse);
                        if (serverResponse.contains("Insufficient molecules for bonding")) {
                            bondingComplete.set(true);
                        }
                        counter++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            responseThread.start();

            // Send requests for oxygen molecules
            for (int ID = 1; ID <= m && !bondingComplete.get(); ID++) {
                int randomTime = random.nextInt(1000 - 50) + 50;
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
                String request = "O" + ID + ", request, " + timeStamp;

                System.out.println(request);
                out.println(request);
                Thread.sleep(randomTime);
            }

            responseThread.join(); // Wait for response thread to finish

            long endTime = System.currentTimeMillis();
            System.out.println("== END ==");
            System.out.println("Runtime: " + (endTime - startTime) + " milliseconds");

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close(); // Close the output stream
                }
                if (in != null) {
                    in.close(); // Close the input stream
                }
                if (socket != null) {
                    socket.close(); // Close the socket
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Oxygen oxygen = new Oxygen(1); // Assuming ID starts from 1
        oxygen.start();
    }
}
