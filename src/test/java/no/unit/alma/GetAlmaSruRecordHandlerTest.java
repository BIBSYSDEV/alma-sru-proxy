package no.unit.alma;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import no.unit.alma.sru.AlmaSruConnection;
import no.unit.marc.Reference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetAlmaSruRecordHandlerTest {

    public static final String SRU_RESPONSE_1_HIT = "/SRU_response_1_hit.xml";
    public static final String SRU_RESPONSE_2_HITS = "/SRU_response_2_hits.xml";
    public static final String SRU_RESPONSE_3_HITS_FOR_ONE_ISBN = "/SRU_response_3_hits_for_one_isbn.xml";
    public static final String SRU_RESPONSE_2_WITH_BAD_XML = "/SRU_response_bad_xml.xml";
    public static final String MOCK_MMS_ID = "1123456789";
    public static final String MOCK_INSTITUTION = "NTNU_UB";
    public static final String MOCK_ISBN = "978-0-367-19672-1";
    public static final String MOCK_SRU_HOST = "alma-sru-host-dot-com";
    public static final String EXPECTED_TITLE = "Bedriftsintern telekommunikasjon";
    public static final String MMSID_FROM_FIRST_CORRECT_POST_FROM_ISBN_WITH_TWO_HITS = "999921024491202201";
    public static final String MMSID_FROM_SECOND_CORRECT_POST_FROM_ISBN_WITH_TWO_HITS = "998611116644702201";
    public static final String MMSID_FROM_WRONG_POST_FROM_ISBN_WITH_TWO_HITS =   "999920928694002201";

    private AlmaSruConnection mockConnection;
    private GetAlmaSruRecordHandler mockAlmaRecordHandler;

    /**
     * Sets up test objects with mocks.
     */
    @BeforeEach
    public void setup() {
        final Config instance = Config.getInstance();
        instance.setAlmaSruHost(MOCK_SRU_HOST);

        mockConnection = mock(AlmaSruConnection.class);
        mockAlmaRecordHandler = new GetAlmaSruRecordHandler(mockConnection);
    }

    @Test
    public void testFetchRecord_MissingQueryStrings() {
        mockAlmaRecordHandler = new GetAlmaSruRecordHandler();

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
    public void testFetchRecord_BadCombinationOfParameters() {
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
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(GetAlmaSruRecordHandler.MMSID_KEY, MOCK_MMS_ID);
        queryParameters.put(GetAlmaSruRecordHandler.INSTITUTION_KEY, MOCK_INSTITUTION);
        Map<String, Object> event = new HashMap<>();
        event.put(GetAlmaSruRecordHandler.QUERY_STRING_PARAMETERS_KEY, queryParameters);

        InputStream stream = GetAlmaSruRecordHandlerTest.class.getResourceAsStream(SRU_RESPONSE_2_HITS);
        when(mockConnection.connect(any())).thenReturn(new InputStreamReader(stream));

        final GatewayResponse gatewayResponse = mockAlmaRecordHandler.handleRequest(event, null);

        assertEquals(Response.Status.OK.getStatusCode(), gatewayResponse.getStatusCode());
        assertTrue(gatewayResponse.getBody().contains(EXPECTED_TITLE));
    }

    @Test
    public void testFetchRecordTitleByIsbn() throws IOException {
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(GetAlmaSruRecordHandler.ISBN_KEY, MOCK_ISBN);
        Map<String, Object> event = new HashMap<>();
        event.put(GetAlmaSruRecordHandler.QUERY_STRING_PARAMETERS_KEY, queryParameters);

        InputStream stream = GetAlmaSruRecordHandlerTest.class.getResourceAsStream(SRU_RESPONSE_2_HITS);
        when(mockConnection.connect(any())).thenReturn(new InputStreamReader(stream));

        final GatewayResponse gatewayResponse = mockAlmaRecordHandler.handleRequest(event, null);

        assertEquals(Response.Status.OK.getStatusCode(), gatewayResponse.getStatusCode());
        assertTrue(gatewayResponse.getBody().contains(EXPECTED_TITLE));
    }

    @Test
    public void getsOnlyCorrectPostsFromIsbn() throws IOException {
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(GetAlmaSruRecordHandler.ISBN_KEY, MOCK_ISBN);
        Map<String, Object> event = new HashMap<>();
        event.put(GetAlmaSruRecordHandler.QUERY_STRING_PARAMETERS_KEY, queryParameters);

        InputStream stream = GetAlmaSruRecordHandlerTest.class.getResourceAsStream(SRU_RESPONSE_3_HITS_FOR_ONE_ISBN);
        when(mockConnection.connect(any())).thenReturn(new InputStreamReader(stream));

        final GatewayResponse gatewayResponse = mockAlmaRecordHandler.handleRequest(event, null);

        assertEquals(Response.Status.OK.getStatusCode(), gatewayResponse.getStatusCode());
        assertTrue(gatewayResponse.getBody().contains(MMSID_FROM_FIRST_CORRECT_POST_FROM_ISBN_WITH_TWO_HITS));
        assertTrue(gatewayResponse.getBody().contains(MMSID_FROM_SECOND_CORRECT_POST_FROM_ISBN_WITH_TWO_HITS));
        assertFalse(gatewayResponse.getBody().contains(MMSID_FROM_WRONG_POST_FROM_ISBN_WITH_TWO_HITS));
    }

    @Test
    public void returnsErrorWhenAlmaRespondsWithBadXML() throws IOException {
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(GetAlmaSruRecordHandler.ISBN_KEY, MOCK_ISBN);
        Map<String, Object> event = new HashMap<>();
        event.put(GetAlmaSruRecordHandler.QUERY_STRING_PARAMETERS_KEY, queryParameters);

        InputStream stream = GetAlmaSruRecordHandlerTest.class.getResourceAsStream(SRU_RESPONSE_2_WITH_BAD_XML);
        when(mockConnection.connect(any())).thenReturn(new InputStreamReader(stream));

        final GatewayResponse gatewayResponse = mockAlmaRecordHandler.handleRequest(event, null);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), gatewayResponse.getStatusCode());
    }

    @Test
    void returnsListOfReferenceObjectsAsJson() throws IOException {
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(GetAlmaSruRecordHandler.MMSID_KEY, MOCK_MMS_ID);
        Map<String, Object> event = new HashMap<>();
        event.put(GetAlmaSruRecordHandler.QUERY_STRING_PARAMETERS_KEY, queryParameters);

        InputStream stream = GetAlmaSruRecordHandlerTest.class.getResourceAsStream(SRU_RESPONSE_1_HIT);
        when(mockConnection.connect(any())).thenReturn(new InputStreamReader(stream));

        final GatewayResponse gatewayResponse = mockAlmaRecordHandler.handleRequest(event, null);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type listOfMyClassObject = new TypeToken<List<Reference>>() {}.getType();
        List<Reference> references = gson.fromJson(gatewayResponse.getBody(), listOfMyClassObject);

        assertEquals(1, references.size());
        assertEquals("1986", references.get(0).getYear());
        assertEquals("998611116644702201", references.get(0).getId());
    }
}
