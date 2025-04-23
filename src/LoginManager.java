import java.util.*;
public class LoginManager {
    public static User login(String nric, String pw) {
        User u = DataStore.users.get(nric);
        if (u!=null && u.checkPassword(pw)) return u;
        return null;
    }
    public static boolean changePassword(User u, String newPw) {
        u.setPassword(newPw);
        return true;
    }
}
