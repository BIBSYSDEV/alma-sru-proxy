package no.unit.nva.alma;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.Response;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

public class FetchAlmaRecordHandler implements RequestHandler<Map<String, Object>, GatewayResponse> {

    public static final String MISSING_EVENT_ELEMENT_QUERYSTRINGPARAMETERS =
            "Missing event element 'queryStringParameters'.";
    public static final String MANDATORY_PARAMETER_SCN_MISSING = "Mandatory parameter 'scn' is missing.";
    public static final String MANDATORY_PARAMETER_CREATORNAME_MISSING =
            "Mandatory parameter 'creatorname' is missing.";
    public static final String QUERY_STRING_PARAMETERS_KEY = "queryStringParameters";
    public static final String CREATOR_NAME_KEY = "creatorname";
    public static final String SCN_KEY = "scn";

    /**
     * Main lambda function to fetch records from Alma.
     *
     * @param input payload with identifying parameters
     * @return a GatewayResponse
     */
    @Override
    @SuppressWarnings("unchecked")
    public GatewayResponse handleRequest(final Map<String, Object> input, Context context) {
        System.out.println(input);
        GatewayResponse gatewayResponse = new GatewayResponse();
        try {
            Config.getInstance().checkProperties();
            this.checkParameters(input);
        } catch (RuntimeException e) {
            System.out.println(e);
            gatewayResponse.setErrorBody(e.getMessage());
            gatewayResponse.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
            return gatewayResponse;
        }

        Map<String, String> queryStringParameters = (Map<String, String>) input.get(QUERY_STRING_PARAMETERS_KEY);
        String scn = queryStringParameters.get(SCN_KEY);
        String creatorName = queryStringParameters.get(CREATOR_NAME_KEY);
        AlmaSruConnection connection = new AlmaSruConnection();
        try {
            final URL queryUrl = connection.generateQueryUrl(scn, creatorName);
            try (InputStreamReader streamReader = connection.connect(queryUrl)) {
                AlmaRecordParser almaRecordParser = new AlmaRecordParser();
                final String json = almaRecordParser.extractPublicationData(streamReader);
            }
        } catch (URISyntaxException | IOException | XMLStreamException e) {
            System.out.println(e);
            gatewayResponse.setErrorBody(e.getMessage());
            gatewayResponse.setStatusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
        return gatewayResponse;
    }

    @SuppressWarnings("unchecked")
    private void checkParameters(Map<String, Object> input) {
        if (Objects.isNull(input) || !input.containsKey(QUERY_STRING_PARAMETERS_KEY)
                || Objects.isNull(input.get(QUERY_STRING_PARAMETERS_KEY))) {
            throw new RuntimeException(MISSING_EVENT_ELEMENT_QUERYSTRINGPARAMETERS);
        }
        Map<String, String> queryStringParameters = (Map<String, String>) input.get(QUERY_STRING_PARAMETERS_KEY);
        final String scn = queryStringParameters.get(SCN_KEY);
        if (StringUtils.isEmpty(scn)) {
            throw new RuntimeException(MANDATORY_PARAMETER_SCN_MISSING);
        }
        final String creatorName = queryStringParameters.get(CREATOR_NAME_KEY);
        if (StringUtils.isEmpty(creatorName)) {
            throw new RuntimeException(MANDATORY_PARAMETER_CREATORNAME_MISSING);
        }
    }

}
