package no.unit.alma;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DebugUtils {

    public static final String NEWLINE = "\n";
    public static final String CARRIAGE_RETURN = "\r";

    /**
     * Writes a stackTrace into a string.
     * @param e any Exception
     * @return Stringdump of exception
     */
    public static String dumpException(Exception e) {
        return getStackTrace(e).replace(NEWLINE, CARRIAGE_RETURN);
    }

    /**
     * From org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace
     * in org.apache.commons:commons-lang3:3.9
     */
    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

}
