package no.unit.nva.alma;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;

public class GatewayResponseTest {

    private static final String EMPTY_STRING = "";

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
        Assertions.assertEquals(ERROR_JSON, gatewayResponse.getBody());
    }

    @Test
    public void testNoCorsHeaders() {
        final Config config = Config.getInstance();
        config.setCorsHeader(EMPTY_STRING);
        final String corsHeader = config.getCorsHeader();
        GatewayResponse gatewayResponse = new GatewayResponse(MOCK_BODY, Response.Status.BAD_REQUEST.getStatusCode());
        Assertions.assertFalse(gatewayResponse.getHeaders().containsKey(GatewayResponse.CORS_ALLOW_ORIGIN_HEADER));
        Assertions.assertFalse(gatewayResponse.getHeaders().containsValue(corsHeader));

        config.setCorsHeader(CORS_HEADER);
        GatewayResponse gatewayResponse1 = new GatewayResponse(MOCK_BODY, Response.Status.BAD_REQUEST.getStatusCode());
        Assertions.assertTrue(gatewayResponse1.getHeaders().containsKey(GatewayResponse.CORS_ALLOW_ORIGIN_HEADER));
    }

}
