package no.unit.nva.alma;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class GatewayResponseTest {

    private static final String EMPTY_STRING = "";
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    public static final String CORS_HEADER = "CORS header";
    public static final String MOCK_BODY = "mock";
    public static final String ERROR_BODY = "error";
    public static final String ERROR_JSON = "{\"error\":\"error\"}";

    @Test
    public void testErrorResponse() {
        GatewayResponse gatewayResponse = new GatewayResponse();
        gatewayResponse.setBody(null);
        gatewayResponse.setErrorBody(ERROR_BODY);
        gatewayResponse.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
        assertEquals(ERROR_JSON, gatewayResponse.getBody());
    }

    @Test
    public void testNoCorsHeaders() {
        final Config config = Config.getInstance();
        config.setCorsHeader(EMPTY_STRING);
        final String corsHeader = config.getCorsHeader();
        GatewayResponse gatewayResponse = new GatewayResponse(MOCK_BODY, Response.Status.BAD_REQUEST.getStatusCode());
        assertFalse(gatewayResponse.getHeaders().containsKey(GatewayResponse.CORS_ALLOW_ORIGIN_HEADER));
        assertFalse(gatewayResponse.getHeaders().containsValue(corsHeader));

        config.setCorsHeader(CORS_HEADER);
        GatewayResponse gatewayResponse1 = new GatewayResponse(MOCK_BODY, Response.Status.BAD_REQUEST.getStatusCode());
        assertTrue(gatewayResponse1.getHeaders().containsKey(GatewayResponse.CORS_ALLOW_ORIGIN_HEADER));
    }

}
