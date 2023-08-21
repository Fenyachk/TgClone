import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ChatWindow extends JFrame {
    private final JTextArea chatArea;
    private final JTextField messageField;
    private final User authenticatedUser;
    private Socket socket;
    private PrintWriter writer;
    private final Chat chat;

    public ChatWindow(User authenticatedUser, User selectedUser, Chat chat) {
        this.authenticatedUser = authenticatedUser;
        this.chat = chat;
        setTitle("Chat with " + selectedUser.getLogin());
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);
        messageField = new JTextField();
        JButton sendButton = new JButton("Send");
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);
        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());
        setVisible(true);
        connectToServer();
        ReadThread readThread = new ReadThread();
        Thread thread = new Thread(readThread);
        thread.start();
    }

    private void connectToServer() {
        try {
            socket = new Socket("127.0.0.1", 12345);
            writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println(authenticatedUser.getLogin());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty() && chat.getId() != -1) {
            int chatId = chat.getId();
            String fullMessage = chatId + ":" + message;
            writer.println(fullMessage);
            messageField.setText("");
        }
    }

    private class ReadThread implements Runnable {
        private BufferedReader threadReader;

        public ReadThread() {
            try {
                threadReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        public void run() {
            try {
                String serverMessage;
                while ((serverMessage = threadReader.readLine()) != null) {
                    String[] parts = serverMessage.split(":", 3);
                    if (parts.length == 3) {
                        String senderUsername = parts[0];
                        String chatIdString = parts[1].trim();
                        String message = parts[2];
                        try {
                            int chatId = Integer.parseInt(chatIdString);
                            if (chat.getId() == chatId) {
                                chatArea.append(senderUsername + ": " + message + "\n");
                            }
                        } catch (NumberFormatException ex) {
                            System.err.println("Invalid chat ID: " + chatIdString);
                        }
                    }
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatWindow(null, null, null));
    }
}
