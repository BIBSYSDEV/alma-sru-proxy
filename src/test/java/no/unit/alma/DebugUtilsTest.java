package no.unit.alma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DebugUtilsTest {

    @Test
    public void testDumpException() {
        final RuntimeException runtimeException = new RuntimeException("RuntimeException");
        DebugUtils debugUtils = new DebugUtils();
        String exceptionDump = debugUtils.dumpException(runtimeException);
        assertNotNull(exceptionDump);
    }

}
