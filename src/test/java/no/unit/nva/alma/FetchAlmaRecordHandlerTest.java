package no.unit.nva.alma;

import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FetchAlmaRecordHandlerTest {

    public static final String MOCK_CREATOR_NAME = "Creator, Mock";
    public static final String MOCK_SCN = "1123456789";

    @Test
    public void testFetchRecord_MissingQueryStrings() {
        final Config instance = Config.getInstance();
        instance.setAlmaSruHost(Config.ALMA_SRU_HOST_KEY);

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

}
