package no.unit.alma;

import com.google.gson.JsonObject;
import no.unit.utils.StringUtils;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * POJO containing response object for API Gateway.
 */
public class GatewayResponse {

    public static final String CORS_ALLOW_ORIGIN_HEADER = "Access-Control-Allow-Origin";
    public static final String EMPTY_JSON = "{}";
    public static final transient String ERROR_KEY = "error";
    private String body;
    private transient Map<String, String> headers;
    private int statusCode;

    /**
     * GatewayResponse contains response status, response headers and body with payload resp. error messages.
     */
    public GatewayResponse() {
        this.statusCode = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        this.body = EMPTY_JSON;
        this.generateDefaultHeaders();
    }

    /**
     * GatewayResponse convenience constructor to set response status and body with payload direct.
     */
    public GatewayResponse(final String body, final int status) {
        this.statusCode = status;
        this.body = body;
        this.generateDefaultHeaders();
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setStatusCode(int status) {
        this.statusCode = status;
    }

    /**
     * Set error message as a json string to body.
     *
     * @param message message from exception
     */
    public void setErrorBody(String message) {
        JsonObject json = new JsonObject();
        json.addProperty(ERROR_KEY, message);
        this.body = json.toString();
    }

    private void generateDefaultHeaders() {
        Map<String, String> headers = new ConcurrentHashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        final String corsAllowDomain = Config.getInstance().getCorsHeader();
        if (StringUtils.isNotEmpty(corsAllowDomain)) {
            headers.put(CORS_ALLOW_ORIGIN_HEADER, corsAllowDomain);
        }
        headers.put("Access-Control-Allow-Methods", "OPTIONS,GET");
        headers.put("Access-Control-Allow-Credentials", "true");
        headers.put("Access-Control-Allow-Headers", HttpHeaders.CONTENT_TYPE);
        this.headers = Map.copyOf(headers);
    }

}
