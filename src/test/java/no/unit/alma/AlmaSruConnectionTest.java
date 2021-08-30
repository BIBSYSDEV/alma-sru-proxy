package no.unit.alma;

import no.unit.alma.sru.AlmaSruConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlmaSruConnectionTest {

    public static final String SRU_RESPONSE_2_HITS = "/SRU_response_2_hits.xml";
    public static final String MOCK_MMS_ID = "1123456789";
    public static final String MOCK_INSTITUTION = "NTNU_UB";
    public static final String MOCK_ISBN = "43423-432432-432432";

    private AlmaSruConnection almaSruConnection;

    @BeforeEach
    public void setup() {
        almaSruConnection = new AlmaSruConnection();
    }

    @Test
    public void testConnect() throws IOException {
        final URL localFileUrl = AlmaSruConnectionTest.class.getResource(SRU_RESPONSE_2_HITS);
        final InputStreamReader streamReader = almaSruConnection.connect(localFileUrl);
        assertNotNull(streamReader);
        streamReader.close();
    }

    @Test
    public void testGenerateQueryByMmsIdUrl() throws MalformedURLException, URISyntaxException {
        URL url = almaSruConnection.generateQueryByMmsIdUrl(MOCK_MMS_ID, null, null);
        assertTrue(url.getQuery().endsWith("query=alma.mms_id%3D1123456789"));
    }

    @Test
    public void testGenerateQueryByMmsIdUrlWithInstitution() throws MalformedURLException, URISyntaxException {
        URL url = almaSruConnection.generateQueryByMmsIdUrl(MOCK_MMS_ID, MOCK_INSTITUTION, null);
        assertTrue(url.getQuery().endsWith("query=alma.all_for_ui%3D1123456789"));
    }

    @Test
    public void testGenerateQueryByIsbnUrl() throws MalformedURLException, URISyntaxException {
        URL url = almaSruConnection.generateQueryByIsbnUrl(MOCK_ISBN);
        assertTrue(url.getQuery().endsWith("query=alma.isbn%3D43423-432432-432432"));
    }

}
