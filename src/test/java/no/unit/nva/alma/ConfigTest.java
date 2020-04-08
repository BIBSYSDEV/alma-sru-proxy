package no.unit.nva.alma;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ConfigTest {

    @Test
    public void testCheckPropertiesNothingSet() {
        final Config config = Config.getInstance();
        config.setAlmaSruHost(null);
        Assertions.assertThrows(RuntimeException.class, () -> config.checkProperties());
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

    @Test
    public void testCheckPropertiesSetOnlyApiKey() {
        final Config instance = Config.getInstance();
        instance.setAlmaSruHost(null);
        Assertions.assertThrows(RuntimeException.class, () -> instance.checkProperties());
    }
}
