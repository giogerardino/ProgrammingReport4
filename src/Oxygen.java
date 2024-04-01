import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.Scanner;

public class Oxygen {
    private final int OXYGEN_PORT = 60000;
    private final String SERVER_IP = "192.168.68.124";
    private final int id;

    public Oxygen(int id) {
        this.id = id;
    }

    public void start() {
        try {
//            InetAddress address = InetAddress.getByName(SERVER_IP);
            Socket socket = new Socket("localhost", OXYGEN_PORT);
            System.out.println("Connected to server");
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            Random random = new Random(12345);

            new OxygenListenThread(socket).start();

            Scanner scanner = new Scanner(System.in);
            int m = 0;
            do {
                m = scanner.nextInt();
                if (m <= 0)
                    System.out.println("Enter a valid number of oxygen molecules");
            } while (m <= 0);

            long startTime = System.currentTimeMillis();
            int ID = 1;

            while (ID <= m) {
                int randomTime = random.nextInt(1000 - 50) + 50;

                String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
                String request = "O-" + ID + ", request, " + timeStamp;

                out.writeObject(request);
                ID++;

                Thread.sleep(randomTime);
            }

            long endTime = System.currentTimeMillis();
            System.out.println("OXYGEN THREAD END");
            System.out.println("Runtime: " + (endTime - startTime) + " milliseconds");

            socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class OxygenListenThread extends Thread {
        protected Socket socket;

        public OxygenListenThread(Socket oxygen) {
            this.socket = oxygen;
        }

        public void run() {
            try {
                ObjectInputStream inOxygen = new ObjectInputStream(socket.getInputStream());
                while (true) {
                    try {
                        String received = (String) inOxygen.readObject();
                        System.out.println(received);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        return;
                    }
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
