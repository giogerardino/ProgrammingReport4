import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Random;

public class Hydrogen {
    private final int HYDROGEN_PORT = 50000;
    private final String SERVER_IP = "localhost";
    private final int id;
    private final int N = 2000000;

    public Hydrogen(int id) {
        this.id = id;
    }

    public void start() {
        try {
            Socket socket = new Socket(SERVER_IP, HYDROGEN_PORT);
            System.out.println("Connected to server");
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Random random = new Random();

            long startTime = System.currentTimeMillis();

            for (int ID = 1; ID <= N; ID++) {
                int randomTime = random.nextInt(1000 - 50) + 50;
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
                String request = "H" + ID + ", request, " + timeStamp;

                System.out.println(request);
                out.println(request);
                Thread.sleep(randomTime);
            }
            String serverResponse;
            int counter = 0;
            while (counter < N && (serverResponse = in.readLine()) != null) {
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
        Hydrogen hydrogen = new Hydrogen(1); // Assuming ID starts from 1
        hydrogen.start();
    }
}
