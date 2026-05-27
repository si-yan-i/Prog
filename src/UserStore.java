import java.util.HashMap;
import java.util.Map;


public class UserStore {


    private static final Map<String, String> users = new HashMap<>();


    public static boolean exists(String username) {
        return users.containsKey(username.toLowerCase());
    }


    public static boolean register(String username, String password) {
        if (exists(username)) return false;
        users.put(username.toLowerCase(), password);
        return true;
    }

    public static boolean authenticate(String username, String password) {
        String stored = users.get(username.toLowerCase());
        return stored != null && stored.equals(password);
    }
}