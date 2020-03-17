package no.unit.nva.alma;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class DebugUtilsTest {

    @Test
    public void testDumpException() {
        final RuntimeException runtimeException = new RuntimeException("RuntimeException");
        DebugUtils debugUtils = new DebugUtils();
        String exceptionDump = debugUtils.dumpException(runtimeException);
        assertNotNull(exceptionDump);
    }

}
