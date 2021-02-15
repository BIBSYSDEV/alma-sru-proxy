package no.unit.alma;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import no.unit.alma.almaUpdate.GetRecordByISBNConnection;
import no.unit.utils.StringUtils;

import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class UpdateAlma856Handler implements RequestHandler<Map<String, Object>, GatewayResponse> {

    public static final String QUERY_URL = "https://api.sandbox.bibs.aws.unit.no/alma?isbn=";
    public static final String QUERY_STRING_PARAMETERS_KEY = "queryStringParameters";
    public static final String ISBN_KEY = "isbn";

    public static final String MISSING_EVENT_ELEMENT_QUERYSTRINGPARAMETERS =
            "Missing event element 'queryStringParameters'.";
    public static final String MANDATORY_PARAMETERS_MISSING = "Mandatory parameters 'mms_id' or 'isbn' is missing.";
    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "An error occurred, error has been logged";

    protected final transient GetRecordByISBNConnection connection = new GetRecordByISBNConnection();

    /**
     * Main lambda function to update the links in Alma records.
     *
     * @param input payload with identifying parameters
     * @return a GatewayResponse
     */
    @Override
    @SuppressWarnings("unchecked")
    public GatewayResponse handleRequest(final Map<String, Object> input, Context context) {
        //TODO: Remove - For testing only
        System.out.println(input);

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
        String isbn = queryStringParameters.get(ISBN_KEY);

        try {
            URL theURL = new URL(QUERY_URL + isbn);
            InputStreamReader streamReader = connection.connect(theURL);
            String xml = new BufferedReader(streamReader)
                    .lines()
                    .collect(Collectors.joining(System.lineSeparator()));
            gatewayResponse.setBody(xml);
            gatewayResponse.setStatusCode(Response.Status.OK.getStatusCode());

        }
        catch (Exception e){
            System.out.println(e.toString());
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
        final String isbn = queryStringParameters.get(ISBN_KEY);
        if (StringUtils.isEmpty(isbn)) {
            throw new ParameterException(MANDATORY_PARAMETERS_MISSING);
        }
    }

}
