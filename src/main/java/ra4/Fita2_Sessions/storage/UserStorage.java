package ra4.Fita2_Sessions.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import ra4.Fita2_Sessions.handler.ClientHandler;

public class UserStorage {

    private static UserStorage instance = new UserStorage();
    private ConcurrentHashMap<String, ClientHandler> users = new ConcurrentHashMap<>();

    private UserStorage() {
    }

    public static UserStorage getInstance() {
        return instance;
    }

    public boolean exists(String username) {
        return users.containsKey(username);
    }

    public void addUser(String username, ClientHandler handler) {
        users.put(username, handler);
    }

    public void removeUser(String username) {
        users.remove(username);
    }

    public List<String> getUserList() {
        return new ArrayList<>(users.keySet());
    }
}
