import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.Scanner;

public class Hydrogen {
    private final int HYDROGEN_PORT = 50000;
    private final String SERVER_IP = "localhost";
    private final int id;

    public Hydrogen(int id) {
        this.id = id;
    }

    public void start() {
        try {
            Socket socket = new Socket(SERVER_IP, HYDROGEN_PORT);
            System.out.println("Connected to server");
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
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

            for (int ID = 1; ID <= n; ID++) {
                int randomTime = random.nextInt(1000 - 50) + 50;
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
                String request = "H" + ID + ", request, " + timeStamp;

                System.out.println(request);
                out.println(request);
                Thread.sleep(randomTime);
            }

            long endTime = System.currentTimeMillis();
            System.out.println("HYDROGEN THREAD END");
            System.out.println("Runtime: " + (endTime - startTime) + " milliseconds");

            socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Hydrogen hydrogen = new Hydrogen(1); // Assuming ID starts from 1
        hydrogen.start();
    }
}
