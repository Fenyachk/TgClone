import java.awt.*;

public class MainApp {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            AuthenticationForm authForm = new AuthenticationForm();
            authForm.addAuthenticationListener(MainApp::showUserListForm);
        });
    }

    private static void showUserListForm(User authenticatedUser) {
        DatabaseManager dbManager = new DatabaseManager();
        UserListForm userListForm = new UserListForm(authenticatedUser, dbManager);
        userListForm.updateUsersList();
        userListForm.addChatOpenListener(selectedUser -> showChatWindow(authenticatedUser, selectedUser));
        userListForm.setVisible(true);
    }

    private static void showChatWindow(User authenticatedUser, User selectedUser) {
        ChatWindow chatWindow = new ChatWindow(authenticatedUser, selectedUser, null);
        chatWindow.setVisible(true);
    }

}
