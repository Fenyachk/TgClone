
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class UserListForm extends JFrame {
    private final JList<String> userList;
    private final List<ChatOpenListener> chatOpenListeners = new ArrayList<>();
    private final DefaultListModel<String> userModel = new DefaultListModel<>();
    private final User authenticatedUser;
    private final DatabaseManager databaseManager;
    private final List<Thread> chatReadThreads = new ArrayList<>();

    public UserListForm(User authenticatedUser, DatabaseManager databaseManager) {
        this.authenticatedUser = authenticatedUser;
        this.databaseManager = databaseManager;
        setTitle("User List");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        userList = new JList<>(userModel);
        JScrollPane scrollPane = new JScrollPane(userList);
        add(scrollPane, BorderLayout.CENTER);
        JButton chatButton = new JButton("Open Chat");
        chatButton.addActionListener(e -> openChat());
        add(chatButton, BorderLayout.SOUTH);
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> updateUsersList());
        add(refreshButton, BorderLayout.NORTH);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                for (Thread thread : chatReadThreads) {
                    thread.interrupt();
                }
                authenticatedUser.setOnline(false);
                DatabaseManager dbManager = new DatabaseManager();
                dbManager.updateUserStatus(authenticatedUser);
            }
        });
        updateUsersList();
    }

    public void updateUsersList() {
        userModel.clear();
        List<User> allUsers = getUsersFromDatabase();
        for (User user : allUsers) {
            if (user.getId() != authenticatedUser.getId()) {
                boolean isOnline = isUserOnline(user);
                String userStatus = isOnline ? "Online" : "Offline";
                userModel.addElement(user.getLogin() + " (" + userStatus + ")");
            }
        }
    }

    private void openChat() {
        String selectedUsername = userList.getSelectedValue();
        if (selectedUsername != null) {
            User selectedUser = findUserByUsername(selectedUsername.split(" ")[0]);
            Chat chat = createOrFindChat(authenticatedUser, selectedUser);
            if (chat != null) {
                showChatWindow(authenticatedUser, selectedUser, chat);
            }
        }
    }

    private void showChatWindow(User authenticatedUser, User selectedUser, Chat chat) {
        ChatWindow chatWindow = new ChatWindow(authenticatedUser, selectedUser, chat);
        chatWindow.setVisible(true);
    }

    private Chat createOrFindChat(User user1, User user2) {
        List<Chat> chats = databaseManager.getChatsByUsers(user1, user2);
        if (!chats.isEmpty()) {
            return chats.get(0);
        } else {
            Chat newChat = new Chat(-1, user1, user2);
            databaseManager.addChat(newChat);
            return newChat;
        }
    }

    private User findUserByUsername(String username) {
        List<User> userList = getUsersFromDatabase();
        for (User user : userList) {
            if (user.getLogin().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public void addChatOpenListener(ChatOpenListener listener) {
        chatOpenListeners.add(listener);
    }

    private List<User> getUsersFromDatabase() {
        DatabaseManager dbManager = new DatabaseManager();

        return dbManager.getAllUsers();
    }

    private boolean isUserOnline(User user) {
        return user.isOnline();
    }

}
