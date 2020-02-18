package no.unit.nva.alma;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import static org.junit.Assert.assertNotNull;

public class AlmaSruConnectionTest {


    public static final String SRU_RESPONSE_2_HITS = "/SRU_response_2_hits.xml";

    @Test
    public void testConnect() throws IOException {
        final URL localFileUrl = AlmaSruConnectionTest.class.getResource(SRU_RESPONSE_2_HITS);
        AlmaSruConnection connection = new AlmaSruConnection();
        final InputStreamReader streamReader = connection.connect(localFileUrl);
        assertNotNull(streamReader);
        streamReader.close();
    }

}
