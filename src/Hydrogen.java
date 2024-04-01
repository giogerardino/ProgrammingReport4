import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.Scanner;

public class Hydrogen {
    private final int HYDROGEN_PORT = 50000;
    private final String SERVER_IP = "192.168.68.124";
    private final int id;

    public Hydrogen(int id) {
        this.id = id;
    }

    public void start() {
        try {
//            InetAddress address = InetAddress.getByName(SERVER_IP);
            Socket socket = new Socket("localhost", HYDROGEN_PORT);
            System.out.println("Connected to server");
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            Random random = new Random(12345);

            new HydrogenListenThread(socket).start();

            Scanner scanner = new Scanner(System.in);
            int n = 0;
            do {
                n = scanner.nextInt();
                if (n <= 0)
                    System.out.println("Enter a valid number of hydrogen molecules");
            } while (n <= 0);

            long startTime = System.currentTimeMillis();
            int ID = 1;

            while (ID <= n) {
                int randomTime = random.nextInt(1000 - 50) + 50;

                String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
                String request = "H-" + ID + ", request, " + timeStamp;

                out.writeObject(request);
                ID++;

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

    public class HydrogenListenThread extends Thread {
        protected Socket socket;

        public HydrogenListenThread(Socket hydrogen) {
            this.socket = hydrogen;
        }

        public void run() {
            try {
                ObjectInputStream inHydrogen = new ObjectInputStream(socket.getInputStream());
                while (true) {
                    try {
                        String received = (String) inHydrogen.readObject();
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
        Hydrogen hydrogen = new Hydrogen(1); // Assuming ID starts from 1
        hydrogen.start();
    }
}
