package eu.theinvaded.mastondroid.utils;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
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

    public static SpannableStringBuilder trimSpannable(SpannableStringBuilder spannable) {
        int trimCount = 0;

        String text = spannable.toString();

        while (text.length() > 0 && text.endsWith("\n")) {
            text = text.substring(0, text.length() - 1);
            trimCount += 1;
        }

        return spannable.delete(spannable.length() - trimCount, spannable.length());
    }
}
