package no.unit.nva.alma;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

public class FetchAlmaRecordHandler implements RequestHandler<Map<String, Object>, GatewayResponse> {

    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "An error occurred, error has been logged";

    public static final String MISSING_EVENT_ELEMENT_QUERYSTRINGPARAMETERS =
            "Missing event element 'queryStringParameters'.";
    public static final String MANDATORY_PARAMETER_SCN_MISSING = "Mandatory parameter 'scn' is missing.";
    public static final String MANDATORY_PARAMETER_CREATORNAME_MISSING =
            "Mandatory parameter 'creatorname' is missing.";
    public static final String QUERY_STRING_PARAMETERS_KEY = "queryStringParameters";
    public static final String CREATOR_NAME_KEY = "creatorname";
    public static final String SCN_KEY = "scn";
    protected final transient AlmaSruConnection connection;


    public FetchAlmaRecordHandler() {
        connection = new AlmaSruConnection();
    }

    public FetchAlmaRecordHandler(AlmaSruConnection connection) {
        this.connection = connection;
    }

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
        } catch (MissingParameterException e) {
            DebugUtils.dumpException(e);
            gatewayResponse.setErrorBody(e.getMessage()); // Exception contains missing parameter name
            gatewayResponse.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
            return gatewayResponse;
        }

        Map<String, String> queryStringParameters = (Map<String, String>) input.get(QUERY_STRING_PARAMETERS_KEY);
        String scn = queryStringParameters.get(SCN_KEY);
        String creatorName = queryStringParameters.get(CREATOR_NAME_KEY);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            final URL queryUrl = connection.generateQueryUrl(scn, creatorName);
            try (InputStreamReader streamReader = connection.connect(queryUrl)) {
                AlmaRecordParser almaRecordParser = new AlmaRecordParser();
                Reference json = almaRecordParser.extractPublicationTitle(streamReader);
                gatewayResponse.setBody(gson.toJson(json, Reference.class));
                gatewayResponse.setStatusCode(Response.Status.OK.getStatusCode());
            }
        } catch (URISyntaxException | IOException | TransformerException | SAXException | ParserConfigurationException
                | XPathExpressionException e) {
            DebugUtils.dumpException(e);
            gatewayResponse.setErrorBody(INTERNAL_SERVER_ERROR_MESSAGE);
            gatewayResponse.setStatusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
        return gatewayResponse;
    }

    @SuppressWarnings("unchecked")
    private void checkParameters(Map<String, Object> input) {
        if (Objects.isNull(input) || !input.containsKey(QUERY_STRING_PARAMETERS_KEY)
                || Objects.isNull(input.get(QUERY_STRING_PARAMETERS_KEY))) {
            throw new MissingParameterException(MISSING_EVENT_ELEMENT_QUERYSTRINGPARAMETERS);
        }
        Map<String, String> queryStringParameters = (Map<String, String>) input.get(QUERY_STRING_PARAMETERS_KEY);
        final String scn = queryStringParameters.get(SCN_KEY);
        if (StringUtils.isEmpty(scn)) {
            throw new MissingParameterException(MANDATORY_PARAMETER_SCN_MISSING);
        }
        final String creatorName = queryStringParameters.get(CREATOR_NAME_KEY);
        if (StringUtils.isEmpty(creatorName)) {
            throw new MissingParameterException(MANDATORY_PARAMETER_CREATORNAME_MISSING);
        }
    }

}
