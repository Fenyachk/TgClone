public class Chat {
    private int id;
    private final User user1;
    private final User user2;

    public Chat(int id, User user1, User user2) {
        this.id = id;
        this.user1 = user1;
        this.user2 = user2;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser1() {
        return user1;
    }

    public User getUser2() {
        return user2;
    }
}
