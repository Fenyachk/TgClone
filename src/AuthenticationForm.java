import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AuthenticationForm extends JFrame {
    private final JTextField loginField;
    private final JPasswordField passwordField;
    private final List<AuthenticationListener> authenticationListeners = new ArrayList<>();

    public AuthenticationForm() {
        setTitle("Authentication");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel loginLabel = new JLabel("Login:");
        loginField = new JTextField();
        panel.add(loginLabel);
        panel.add(loginField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        panel.add(passwordLabel);
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> authenticateUser());
        panel.add(loginButton);

        add(panel);
        setVisible(true);
    }

    public void addAuthenticationListener(AuthenticationListener listener) {
        authenticationListeners.add(listener);
    }

    private void notifyAuthenticationListeners(User authenticatedUser) {
        for (AuthenticationListener listener : authenticationListeners) {
            listener.onUserAuthenticated(authenticatedUser);
        }
    }

    private void authenticateUser() {
        String login = loginField.getText();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);

        User authenticatedUser = authenticateWithDatabase(login, password);
        if (authenticatedUser != null) {
            authenticatedUser.setOnline(true);
            DatabaseManager dbManager = new DatabaseManager();
            dbManager.updateUserStatus(authenticatedUser);
            notifyAuthenticationListeners(authenticatedUser);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Authentication failed. Invalid login or password.");
        }
    }

    private User authenticateWithDatabase(String login, String password) {
        DatabaseManager dbManager = new DatabaseManager();

        User user = dbManager.getUserByLogin(login);

        if (user != null && user.getPassword().equals(password)) {
            return user;
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AuthenticationForm::new);
    }
}
