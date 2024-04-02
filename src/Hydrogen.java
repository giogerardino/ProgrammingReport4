import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Hydrogen {
    private final int HYDROGEN_PORT = 50000;
    private final String SERVER_IP = "localhost";
    private int id;

    public Hydrogen(int id) {
        this.id = id;
    }

    public void start() {
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            socket = new Socket(SERVER_IP, HYDROGEN_PORT);
            System.out.println("Connected to server");
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Random random = new Random();

            Scanner scanner = new Scanner(System.in);
            int n = 0;
            do {
                System.out.println("Enter the number of hydrogen molecules:");
                System.out.flush();
                n = scanner.nextInt();
                if (n <= 0)
                    System.out.println("Enter a valid number of hydrogen molecules");
            } while (n <= 0);

            long startTime = System.currentTimeMillis();
            n = n * 2;
            AtomicBoolean bondingComplete = new AtomicBoolean(false); // Flag to track if bonding is complete
            for (int ID = 1; ID <= n && !bondingComplete.get(); ID++) {
                int randomTime = random.nextInt(1000 - 50) + 50;
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
                String request = "H" + ID + ", request, " + timeStamp;

                System.out.println(request);
                out.println(request);
                Thread.sleep(randomTime);

                // Check if bonding is complete asynchronously
                BufferedReader finalIn = in;
                new Thread(() -> {
                    try {
                        String serverResponse;
                        while ((serverResponse = finalIn.readLine()) != null) {
                            if (serverResponse.contains("Insufficient molecules for bonding")) {
                                System.out.println(serverResponse);
                                bondingComplete.set(true); // Set flag to true to exit loop
                                break; // Exit loop if bonding is complete
                            }
                            System.out.println(serverResponse);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

                if (bondingComplete.get()) {
                    break; // Exit loop if bonding is complete
                }
            }

            long endTime = System.currentTimeMillis();
            System.out.println("== END ==");
            System.out.println("Runtime: " + (endTime - startTime) + " milliseconds");
            System.exit(0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close(); // Close the input stream
                }
                if (out != null) {
                    out.close(); // Close the output stream
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
        Hydrogen hydrogen = new Hydrogen(1); // Assuming ID starts from 1
        hydrogen.start();
    }
}
