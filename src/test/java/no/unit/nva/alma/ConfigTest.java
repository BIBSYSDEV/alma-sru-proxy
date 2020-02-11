package no.unit.nva.alma;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ConfigTest {

    @Test(expected = RuntimeException.class)
    public void testCheckPropertiesNothingSet() {
        final Config config = Config.getInstance();
        config.setAlmaSruHost(null);
        config.checkProperties();
        fail();
    }

    @Test
    public void testCorsHeaderNotSet() {
        final Config config = Config.getInstance();
        config.setCorsHeader(null);
        final String corsHeader = config.getCorsHeader();
        assertNull(corsHeader);
    }

    @Test
    public void testCheckPropertiesSet() {
        final Config instance = Config.getInstance();
        instance.setAlmaSruHost(Config.ALMA_SRU_HOST_KEY);
        assertTrue(instance.checkProperties());
        assertEquals(Config.ALMA_SRU_HOST_KEY, instance.getAlmaSruHost());
    }


    @Test(expected = RuntimeException.class)
    public void testCheckPropertiesSetOnlyApiKey() {
        final Config instance = Config.getInstance();
        instance.setAlmaSruHost(null);
        instance.checkProperties();
        fail();
    }
}
