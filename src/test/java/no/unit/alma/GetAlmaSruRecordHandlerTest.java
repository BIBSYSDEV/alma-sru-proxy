package no.unit.alma;

import no.unit.alma.sru.AlmaSruConnection;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetAlmaSruRecordHandlerTest {

    public static final String SRU_RESPONSE_2_HITS = "/SRU_response_2_hits.xml";
    public static final String MOCK_MMS_ID = "1123456789";
    public static final String MOCK_INSTITUTION = "NTNU_UB";
    public static final String MOCK_ISBN = "43423-432432-432432";
    public static final String MOCK_SRU_HOST = "alma-sru-host-dot-com";
    public static final String EXPECTED_TITLE = "Bedriftsintern telekommunikasjon";

    @Test
    public void testFetchRecord_MissingQueryStrings() {
        final Config instance = Config.getInstance();
        instance.setAlmaSruHost(MOCK_SRU_HOST);

        GetAlmaSruRecordHandler mockAlmaRecordHandler = new GetAlmaSruRecordHandler();
        GatewayResponse result = mockAlmaRecordHandler.handleRequest(null, null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), result.getStatusCode());
        assertTrue(result.getBody().contains(GetAlmaSruRecordHandler.MISSING_EVENT_ELEMENT_QUERYSTRINGPARAMETERS));

        Map<String, Object> event = new HashMap<>();
        result = mockAlmaRecordHandler.handleRequest(event, null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), result.getStatusCode());
        assertTrue(result.getBody().contains(GetAlmaSruRecordHandler.MISSING_EVENT_ELEMENT_QUERYSTRINGPARAMETERS));

        event.put(GetAlmaSruRecordHandler.QUERY_STRING_PARAMETERS_KEY, null);
        result = mockAlmaRecordHandler.handleRequest(event, null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), result.getStatusCode());
        assertTrue(result.getBody().contains(GetAlmaSruRecordHandler.MISSING_EVENT_ELEMENT_QUERYSTRINGPARAMETERS));

        Map<String, String> queryParameters = new HashMap<>();
        event.put(GetAlmaSruRecordHandler.QUERY_STRING_PARAMETERS_KEY, queryParameters);
        result = mockAlmaRecordHandler.handleRequest(event, null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), result.getStatusCode());
        assertTrue(result.getBody().contains(GetAlmaSruRecordHandler.MANDATORY_PARAMETERS_MISSING));

        queryParameters = new HashMap<>();
        queryParameters.put(GetAlmaSruRecordHandler.MMSID_KEY, MOCK_MMS_ID);
        queryParameters.put(GetAlmaSruRecordHandler.INSTITUTION_KEY, MOCK_INSTITUTION);
        event.put(GetAlmaSruRecordHandler.QUERY_STRING_PARAMETERS_KEY, queryParameters);
        result = mockAlmaRecordHandler.handleRequest(event, null);
        // inntil videre
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), result.getStatusCode());
    }

    @Test
    void testFetchRecord_BadCombinationOfParameters() {
        final Config instance = Config.getInstance();
        instance.setAlmaSruHost(MOCK_SRU_HOST);

        GetAlmaSruRecordHandler mockAlmaRecordHandler = new GetAlmaSruRecordHandler();
        Map<String, Object> event = new HashMap<>();

        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(GetAlmaSruRecordHandler.MMSID_KEY, MOCK_MMS_ID);
        queryParameters.put(GetAlmaSruRecordHandler.ISBN_KEY, MOCK_ISBN);
        event.put(GetAlmaSruRecordHandler.QUERY_STRING_PARAMETERS_KEY, queryParameters);
        GatewayResponse result = mockAlmaRecordHandler.handleRequest(event, null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), result.getStatusCode());
        assertTrue(result.getBody().contains(GetAlmaSruRecordHandler.COMBINATION_OF_PARAMETERS_NOT_SUPPORTED));

        queryParameters = new HashMap<>();
        queryParameters.put(GetAlmaSruRecordHandler.ISBN_KEY, MOCK_ISBN);
        queryParameters.put(GetAlmaSruRecordHandler.INSTITUTION_KEY, MOCK_INSTITUTION);
        event.put(GetAlmaSruRecordHandler.QUERY_STRING_PARAMETERS_KEY, queryParameters);
        result = mockAlmaRecordHandler.handleRequest(event, null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), result.getStatusCode());
        assertTrue(result.getBody().contains(GetAlmaSruRecordHandler.COMBINATION_OF_PARAMETERS_NOT_SUPPORTED));
    }

    @Test
    public void testFetchRecordTitleByMmsid() throws IOException {
        final Config instance = Config.getInstance();
        instance.setAlmaSruHost(MOCK_SRU_HOST);

        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(GetAlmaSruRecordHandler.MMSID_KEY, MOCK_MMS_ID);
        queryParameters.put(GetAlmaSruRecordHandler.INSTITUTION_KEY, MOCK_INSTITUTION);
        Map<String, Object> event = new HashMap<>();
        event.put(GetAlmaSruRecordHandler.QUERY_STRING_PARAMETERS_KEY, queryParameters);

        AlmaSruConnection mockConnection =  mock(AlmaSruConnection.class);
        InputStream stream = GetAlmaSruRecordHandlerTest.class.getResourceAsStream(SRU_RESPONSE_2_HITS);
        GetAlmaSruRecordHandler mockAlmaRecordHandler = new GetAlmaSruRecordHandler(mockConnection);
        when(mockConnection.connect(any())).thenReturn(new InputStreamReader(stream));

        final GatewayResponse gatewayResponse = mockAlmaRecordHandler.handleRequest(event, null);
        assertEquals(Response.Status.OK.getStatusCode(), gatewayResponse.getStatusCode());
        assertTrue(gatewayResponse.getBody().contains(EXPECTED_TITLE));
    }

    @Test
    public void testFetchRecordTitleByIsbn() throws IOException {
        final Config instance = Config.getInstance();
        instance.setAlmaSruHost(MOCK_SRU_HOST);

        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(GetAlmaSruRecordHandler.ISBN_KEY, MOCK_ISBN);
        Map<String, Object> event = new HashMap<>();
        event.put(GetAlmaSruRecordHandler.QUERY_STRING_PARAMETERS_KEY, queryParameters);

        AlmaSruConnection mockConnection =  mock(AlmaSruConnection.class);
        InputStream stream = GetAlmaSruRecordHandlerTest.class.getResourceAsStream(SRU_RESPONSE_2_HITS);
        GetAlmaSruRecordHandler mockAlmaRecordHandler = new GetAlmaSruRecordHandler(mockConnection);
        when(mockConnection.connect(any())).thenReturn(new InputStreamReader(stream));

        final GatewayResponse gatewayResponse = mockAlmaRecordHandler.handleRequest(event, null);
        assertEquals(Response.Status.OK.getStatusCode(), gatewayResponse.getStatusCode());
        assertTrue(gatewayResponse.getBody().contains(EXPECTED_TITLE));
    }

}
