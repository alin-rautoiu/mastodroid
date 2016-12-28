package eu.theinvaded.mastondroid.utils;

/**
 * Created by alin on 27.12.2016.
 */


public class StringUtils {
    public static String EMPTY = "";

    private StringUtils() {
    }

    public static boolean isNullOrEmpty(String string) {
        return !isNotNullOrEmpty(string);
    }

    public static boolean isNotNullOrEmpty(String string) {
        return string != null && !string.equals(EMPTY);
    }
}
