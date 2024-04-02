import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.Scanner;

public class Oxygen {
    private final int OXYGEN_PORT = 60000;
    private final String SERVER_IP = "localhost";
    private final int id;

    public Oxygen(int id) {
        this.id = id;
    }

    public void start() {
        try {
            Socket socket = new Socket(SERVER_IP, OXYGEN_PORT);
            System.out.println("Connected to server");
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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

            for (int ID = 1; ID <= m; ID++) {
                int randomTime = random.nextInt(1000 - 50) + 50;
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
                String request = "O" + ID + ", request, " + timeStamp;

                System.out.println(request);
                out.println(request);
                Thread.sleep(randomTime);
            }

            String serverResponse;
            int counter = 0;
            while (counter < m && (serverResponse = in.readLine()) != null) {
                System.out.println(serverResponse);
                counter++;
            }

            long endTime = System.currentTimeMillis();
            System.out.println("== END ==");
            System.out.println("Runtime: " + (endTime - startTime) + " milliseconds");

            socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Oxygen oxygen = new Oxygen(1); // Assuming ID starts from 1
        oxygen.start();
    }
}
