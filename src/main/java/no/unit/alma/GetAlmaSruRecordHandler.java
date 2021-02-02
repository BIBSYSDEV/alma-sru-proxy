package no.unit.alma;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import no.unit.utils.StringUtils;

import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class GetAlmaSruRecordHandler implements RequestHandler<Map<String, Object>, GatewayResponse> {

    public static final String QUERY_STRING_PARAMETERS_KEY = "queryStringParameters";
    public static final String MMSID_KEY = "mms_id";
    public static final String INSTITUTION_KEY = "institution";
    public static final String ISBN_KEY = "isbn";

    public static final String MISSING_EVENT_ELEMENT_QUERYSTRINGPARAMETERS =
            "Missing event element 'queryStringParameters'.";
    public static final String MANDATORY_PARAMETERS_MISSING = "Mandatory parameters 'mms_id' or 'isbn' is missing.";
    public static final String COMBINATION_OF_PARAMETERS_NOT_SUPPORTED =
            format("This combination of parameters is not supported. Supply either %s alone, %s and %s or only %s",
                    MMSID_KEY, MMSID_KEY, INSTITUTION_KEY, ISBN_KEY);
    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "An error occurred, error has been logged";

    protected final transient AlmaSruConnection connection;

    public GetAlmaSruRecordHandler() {
        connection = new AlmaSruConnection();
    }

    public GetAlmaSruRecordHandler(AlmaSruConnection connection) {
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
        GatewayResponse gatewayResponse = new GatewayResponse();
        try {
            Config.getInstance().checkProperties();
            this.checkParameters(input);
        } catch (ParameterException e) {
            DebugUtils.dumpException(e);
            gatewayResponse.setErrorBody(e.getMessage()); // Exception contains missing parameter name
            gatewayResponse.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
            return gatewayResponse;
        }

        Map<String, String> queryStringParameters = (Map<String, String>) input.get(QUERY_STRING_PARAMETERS_KEY);
        String mmsId = queryStringParameters.get(MMSID_KEY);
        String institution = queryStringParameters.get(INSTITUTION_KEY);
        String isbn = queryStringParameters.get(ISBN_KEY);

        try {
            URL queryUrl;
            if (StringUtils.isNotEmpty(mmsId) && StringUtils.isEmpty(isbn)) {
                queryUrl = connection.generateQueryByMmsIdUrl(mmsId, institution);
            } else if (StringUtils.isNotEmpty(isbn) && StringUtils.isEmpty(mmsId)) {
                queryUrl = connection.generateQueryByIsbnUrl(isbn);
            } else {
                throw new RuntimeException(format("This state should not be reached, as parameters MMSID = %s "
                        + "and ISBN= %s should have been checked against this previously", mmsId, isbn));
            }
            try (InputStreamReader streamReader = connection.connect(queryUrl)) {
                String xml = new BufferedReader(streamReader)
                        .lines()
                        .collect(Collectors.joining(System.lineSeparator()));
                gatewayResponse.setBody(xml);
                gatewayResponse.setStatusCode(Response.Status.OK.getStatusCode());
            }
        } catch (URISyntaxException | IOException e) {
            DebugUtils.dumpException(e);
            gatewayResponse.setErrorBody(INTERNAL_SERVER_ERROR_MESSAGE + " : " + e.getMessage());
            gatewayResponse.setStatusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
        return gatewayResponse;
    }

    @SuppressWarnings("unchecked")
    private void checkParameters(Map<String, Object> input) {
        if (Objects.isNull(input) || !input.containsKey(QUERY_STRING_PARAMETERS_KEY)
                || Objects.isNull(input.get(QUERY_STRING_PARAMETERS_KEY))) {
            throw new ParameterException(MISSING_EVENT_ELEMENT_QUERYSTRINGPARAMETERS);
        }
        Map<String, String> queryStringParameters = (Map<String, String>) input.get(QUERY_STRING_PARAMETERS_KEY);
        final String mmsId = queryStringParameters.get(MMSID_KEY);
        final String isbn = queryStringParameters.get(ISBN_KEY);
        final String institution = queryStringParameters.get(INSTITUTION_KEY);
        if (StringUtils.isEmpty(mmsId) && StringUtils.isEmpty(isbn)) {
            throw new ParameterException(MANDATORY_PARAMETERS_MISSING);
        } else if (StringUtils.isNotEmpty(mmsId) && StringUtils.isNotEmpty(isbn)) {
            throw new ParameterException(COMBINATION_OF_PARAMETERS_NOT_SUPPORTED);
        } else if (StringUtils.isNotEmpty(isbn) && StringUtils.isNotEmpty(institution)) {
            throw new ParameterException(COMBINATION_OF_PARAMETERS_NOT_SUPPORTED);
        }
    }

}
