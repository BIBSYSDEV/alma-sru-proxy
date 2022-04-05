package no.sikt.alma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response;
import no.unit.alma.Config;
import no.unit.alma.sru.AlmaSruConnection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class GetAlmaSruRecordHandlerTest {

    public static final String SRU_RESPONSE_2_HITS = "/SRU_response_2_hits.xml";
    public static final String MOCK_MMS_ID = "1123456789";
    public static final String MOCK_INSTITUTION = "NTNU_UB";
    public static final String EXPECTED_TITLE = "Bedriftsintern telekommunikasjon";
    public static final String MOCK_SRU_HOST = "alma-sru-host-dot-com";

    private static AlmaSruConnection mockConnection;
    private static GetAlmaSruRecordHandler getAlmaSruRecordHandler;

    @BeforeAll
    public static void setupServer() {
        final Config instance = Config.getInstance();
        instance.setAlmaSruHost(MOCK_SRU_HOST);

        mockConnection = mock(AlmaSruConnection.class);
        getAlmaSruRecordHandler = new GetAlmaSruRecordHandler(mockConnection);
    }

    @AfterAll
    public static void stopServer() {
        if (getAlmaSruRecordHandler != null) {
            getAlmaSruRecordHandler.getApplicationContext().close();
        }
    }

    @Test
    public void testHandler() throws IOException {
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(GetAlmaSruRecordHandler.MMSID_KEY, MOCK_MMS_ID);
        queryParameters.put(GetAlmaSruRecordHandler.INSTITUTION_KEY, MOCK_INSTITUTION);
        Map<String, Object> event = new HashMap<>();
        event.put(GetAlmaSruRecordHandler.QUERY_STRING_PARAMETERS_KEY, queryParameters);

        InputStream stream = GetAlmaSruRecordHandlerTest.class.getResourceAsStream(SRU_RESPONSE_2_HITS);
        when(mockConnection.connect(any())).thenReturn(new InputStreamReader(stream));

        final GatewayResponse gatewayResponse = getAlmaSruRecordHandler.execute(event);

        assertEquals(Response.Status.OK.getStatusCode(), gatewayResponse.getStatusCode());
        assertTrue(gatewayResponse.getBody().contains(EXPECTED_TITLE));
    }
}