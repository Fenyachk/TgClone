import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DATABASE_URL = "jdbc:sqlite:../users.sqlite";

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    public User getUserById(int userId) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String login = resultSet.getString("login");
                    String password = resultSet.getString("password");
                    return new User(userId, login, password);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserByLogin(String login) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE login = ?")) {
            statement.setString(1, login);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String password = resultSet.getString("password");
                    return new User(id, login, password);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM users"); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String login = resultSet.getString("login");
                String password = resultSet.getString("password");
                User user = new User(id, login, password);
                user.setOnline(resultSet.getBoolean("online"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void updateUserStatus(User user) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE users SET online = ? WHERE id = ?")) {
            statement.setBoolean(1, user.isOnline());
            statement.setInt(2, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addChat(Chat chat) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO chats (user1_id, user2_id) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            int user1Id = chat.getUser1().getId();
            int user2Id = chat.getUser2().getId();

            if (user1Id > user2Id) {
                int temp = user1Id;
                user1Id = user2Id;
                user2Id = temp;
            }

            statement.setInt(1, user1Id);
            statement.setInt(2, user2Id);

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int chatId = generatedKeys.getInt(1);
                chat.setId(chatId);
                chat.getUser1().addChatId(chatId);
                chat.getUser2().addChatId(chatId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Chat> getChatsByUsers(User user1Param, User user2Param) {
        List<Chat> chats = new ArrayList<>();
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM chats WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)")) {
            statement.setInt(1, user1Param.getId());
            statement.setInt(2, user2Param.getId());
            statement.setInt(3, user2Param.getId());
            statement.setInt(4, user1Param.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int chatId = resultSet.getInt("id");
                    User user1 = getUserById(resultSet.getInt("user1_id"));
                    User user2 = getUserById(resultSet.getInt("user2_id"));
                    Chat chat = new Chat(chatId, user1, user2);
                    chats.add(chat);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chats;
    }
}
