package no.unit.utils;

/**
 * From org.apache.commons.lang3.StringUtils.
 * in org.apache.commons:commons-lang3:3.9
 */
public final class StringUtils {

    private StringUtils() {
        // NO-OP
    }

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.isEmpty();
    }

    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }
}
