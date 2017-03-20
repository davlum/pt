package tools;

import exceptions.UserException;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Class to hash user Passwords.
 */
public class Hash {

    public static String createPassword(String clearString) throws UserException {
        if (clearString == null) {
            throw new UserException("No password defined!");
        }
        return BCrypt.hashpw(clearString, BCrypt.gensalt());
    }

    public static boolean checkPassword(String candidate, String encryptedPassword) {

        return candidate != null && encryptedPassword != null && BCrypt.checkpw(candidate, encryptedPassword);
    }
}