package no.unit.nva.alma;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchAlmaRecordHandlerTest {

    public static final String SRU_RESPONSE_2_HITS = "/SRU_response_2_hits.xml";
    public static final String MOCK_CREATOR_NAME = "Creator, Mock";
    public static final String MOCK_SCN = "1123456789";
    public static final String MOCK_SRU_HOST = "alma-sru-host-dot-com";

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    AlmaSruConnection mockConnection;

    @Test
    public void testFetchRecord_MissingQueryStrings() {
        final Config instance = Config.getInstance();
        instance.setAlmaSruHost(MOCK_SRU_HOST);

        FetchAlmaRecordHandler mockAlmaRecordHandler = new FetchAlmaRecordHandler();
        GatewayResponse result = mockAlmaRecordHandler.handleRequest(null, null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), result.getStatusCode());
        assertTrue(result.getBody().contains(FetchAlmaRecordHandler.MISSING_EVENT_ELEMENT_QUERYSTRINGPARAMETERS));

        Map<String, Object> event = new HashMap<>();
        result = mockAlmaRecordHandler.handleRequest(event, null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), result.getStatusCode());
        assertTrue(result.getBody().contains(FetchAlmaRecordHandler.MISSING_EVENT_ELEMENT_QUERYSTRINGPARAMETERS));

        event.put(FetchAlmaRecordHandler.QUERY_STRING_PARAMETERS_KEY, null);
        result = mockAlmaRecordHandler.handleRequest(event, null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), result.getStatusCode());
        assertTrue(result.getBody().contains(FetchAlmaRecordHandler.MISSING_EVENT_ELEMENT_QUERYSTRINGPARAMETERS));

        Map<String, String> queryParameters = new HashMap<>();
        event.put(FetchAlmaRecordHandler.QUERY_STRING_PARAMETERS_KEY, queryParameters);
        result = mockAlmaRecordHandler.handleRequest(event, null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), result.getStatusCode());
        assertTrue(result.getBody().contains(FetchAlmaRecordHandler.MANDATORY_PARAMETER_SCN_MISSING));

        queryParameters = new HashMap<>();
        queryParameters.put(FetchAlmaRecordHandler.SCN_KEY, MOCK_SCN);
        event.put(FetchAlmaRecordHandler.QUERY_STRING_PARAMETERS_KEY, queryParameters);
        result = mockAlmaRecordHandler.handleRequest(event, null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), result.getStatusCode());
        assertTrue(result.getBody().contains(FetchAlmaRecordHandler.MANDATORY_PARAMETER_CREATORNAME_MISSING));

        queryParameters = new HashMap<>();
        queryParameters.put(FetchAlmaRecordHandler.SCN_KEY, MOCK_SCN);
        queryParameters.put(FetchAlmaRecordHandler.CREATOR_NAME_KEY, MOCK_CREATOR_NAME);
        event.put(FetchAlmaRecordHandler.QUERY_STRING_PARAMETERS_KEY, queryParameters);
        result = mockAlmaRecordHandler.handleRequest(event, null);
        // inntil videre
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), result.getStatusCode());

    }

    @Test
    public void testFetchRecordTitle() throws IOException {
        final Config instance = Config.getInstance();
        instance.setAlmaSruHost(MOCK_SRU_HOST);

        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(FetchAlmaRecordHandler.SCN_KEY, MOCK_SCN);
        queryParameters.put(FetchAlmaRecordHandler.CREATOR_NAME_KEY, MOCK_CREATOR_NAME);
        Map<String, Object> event = new HashMap<>();
        event.put(FetchAlmaRecordHandler.QUERY_STRING_PARAMETERS_KEY, queryParameters);

        InputStream stream = AlmaRecordParserTest.class.getResourceAsStream(SRU_RESPONSE_2_HITS);
        FetchAlmaRecordHandler mockAlmaRecordHandler = new FetchAlmaRecordHandler(mockConnection);
        when(mockConnection.connect(any())).thenReturn(new InputStreamReader(stream));

        final GatewayResponse gatewayResponse = mockAlmaRecordHandler.handleRequest(event, null);
        assertEquals(Response.Status.OK.getStatusCode(), gatewayResponse.getStatusCode());
        assertTrue(gatewayResponse.getBody().contains(AlmaRecordParserTest.EXPECTED_TITLE));

    }

//    @Test
    public void testFetchRecordTitle_Full_On_IntegrationTest() {
        final Config instance = Config.getInstance();
        instance.setAlmaSruHost("bibsys.alma.exlibrisgroup.com/view/sru/47BIBSYS_NETWORK");

        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(FetchAlmaRecordHandler.SCN_KEY, "8071732");
        queryParameters.put(FetchAlmaRecordHandler.CREATOR_NAME_KEY, "Gabriel, Ute");
        Map<String, Object> event = new HashMap<>();
        event.put(FetchAlmaRecordHandler.QUERY_STRING_PARAMETERS_KEY, queryParameters);

        FetchAlmaRecordHandler mockAlmaRecordHandler = new FetchAlmaRecordHandler();

        final GatewayResponse gatewayResponse = mockAlmaRecordHandler.handleRequest(event, null);
        assertEquals(Response.Status.OK.getStatusCode(), gatewayResponse.getStatusCode());
        assertTrue(gatewayResponse.getBody().contains(AlmaRecordParserTest.EXPECTED_SUBTITLE));

    }

}
