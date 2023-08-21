import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            String userName = reader.readLine();
            writer.println(userName);

            Thread readThread = new Thread(new ReadThread(socket));
            readThread.start();

            String message;
            while (true) {
                message = reader.readLine();
                writer.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private record ReadThread(Socket socket) implements Runnable {
        public void run() {
        }
    }
}
