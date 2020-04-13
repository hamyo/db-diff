package dbdiff.utils;

public class Strings {
    public static String concat(String str1, String str2) {
        if (str1 == null) {
            return str2;
        }

        if (str2 == null) {
            return str2;
        }

        return str1.concat(" " + str2);
    }
}
