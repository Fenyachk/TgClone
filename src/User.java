import java.util.ArrayList;
import java.util.List;

public class User {
    private final int id;
    private final String login;
    private final String password;
    private boolean online;
    private final List<Integer> chatIds;
    
    public User(int id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.chatIds = new ArrayList<>();
    }

    public void addChatId(int chatId) {
        chatIds.add(chatId);
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getPassword() {
        return password;
    }

    public String getLogin() {
        return login;
    }

    public int getId() {
        return id;
    }
}
