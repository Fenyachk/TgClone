import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int PORT = 12345;
    private final List<ClientHandler> clients = new ArrayList<>();

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Waiting for clients...");
            while (true) {
                Socket clientSocket = serverSocket.accept();

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter writer;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                OutputStream output = socket.getOutputStream();
                writer = new PrintWriter(output, true);

                String userName = reader.readLine();

                broadcast(userName + " has joined the chat");

                String clientMessage;
                while ((clientMessage = reader.readLine()) != null) {
                    broadcast(userName + ": " + clientMessage);
                }

                broadcast(userName + " has left the chat");
                clients.remove(this);
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(String message) {
            writer.println(message);
        }
    }

    private void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
