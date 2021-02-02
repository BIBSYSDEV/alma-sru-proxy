package no.unit.utils;

/**
 * From org.apache.commons.lang3.StringUtils
 * in org.apache.commons:commons-lang3:3.9
 */
public class StringUtils {

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }
}
