package no.unit.nva.alma;

import java.util.Arrays;
import java.util.Map;

import static no.unit.nva.alma.Logger.LogLevel.ERROR;
import static no.unit.nva.alma.Logger.LogLevel.INFO;

public class Logger {

    public static final String TAB = "\t";

    protected enum LogLevel {
        ERROR, INFO
    }

    private static class LazyHolder {
        public static final Logger INSTANCE = new Logger();
    }

    /**
     * Singleton instance for Logger.
     *
     * @return the one and only Logger instance
     */
    public static Logger instance() {
        return LazyHolder.INSTANCE;
    }

    public void error(Exception e) {
        error(e.getMessage());
        Arrays.stream(e.getStackTrace()).forEach(x -> System.out.println(TAB + x.toString()));
    }

    public void error(String errorMessage) {
        System.out.println(ERROR.name() + " - " + errorMessage);
    }

    public void info(Map<String, Object> infoMessage) {
        System.out.println(INFO.name() + " - " + infoMessage);
    }
}
