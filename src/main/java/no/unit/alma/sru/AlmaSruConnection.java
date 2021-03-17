package no.unit.alma.sru;

import no.unit.alma.Config;
import no.unit.alma.sru.cql.formatter.CqlFormatter;
import no.unit.utils.StringUtils;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;

public class AlmaSruConnection {

    public static final String HTTPS = "https";
    public static final String VERSION_KEY = "version";
    public static final String SRU_VERSION_1_2 = "1.2";
    public static final String OPERATION_KEY = "operation";
    public static final String OPERATION_SEARCH_RETRIEVE = "searchRetrieve";
    public static final String RECORD_SCHEMA_KEY = "recordSchema";
    public static final String RECORD_SCHEMA = "marcxml";
    public static final String MAXIMUM_RECORDS_KEY = "maximumRecords";
    public static final String ONE_RECORD_ONLY = "1";
    public static final String START_RECORD_KEY = "startRecord";
    public static final String START_RECORD_1 = "1";
    public static final String QUERY_KEY = "query";
    public static final String NETWORK = "NETWORK";

    public InputStreamReader connect(URL url) throws IOException {
        return new InputStreamReader(url.openStream());
    }

    /**
     * Builds an URL for quering Alma based on MMSID.
     *
     * @param mmsId Almas MMSID
     * @param institution Institution name
     * @return URL to connect to
     * @throws MalformedURLException If URL that is built is not valid
     * @throws URISyntaxException If URL that is built is not valid
     */
    public URL generateQueryByMmsIdUrl(String mmsId, String institution)
            throws MalformedURLException, URISyntaxException {
        String encodedCqlQuery = new CqlFormatter()
                .withRetrospective(false)
                .withSorting(false)
                .withMmsId(mmsId)
                .withInstitution(institution)
                .encode();
        if (StringUtils.isEmpty(institution)) {
            return getAlmaURI(encodedCqlQuery).toURL();
        } else {
            return getAlmaURI(institution, encodedCqlQuery).toURL();
        }
    }

    /**
     * Builds an URL for quering Alma based on ISBN.
     *
     * @param isbn to query for
     * @return URL to connect to
     * @throws MalformedURLException If URL that is built is not valid
     * @throws URISyntaxException If URL that is built is not valid
     */
    public URL generateQueryByIsbnUrl(String isbn)
            throws MalformedURLException, URISyntaxException {
        String encodedCqlQuery = new CqlFormatter()
                .withRetrospective(false)
                .withSorting(false)
                .withIsbn(isbn)
                .encode();
        URI uri = getAlmaURI(encodedCqlQuery);
        return uri.toURL();
    }

    private URI getAlmaURI(String institution, String encodedCqlQuery) throws URISyntaxException {
        String almaSruQueryPath = Config.ALMA_SRU_QUERY_PATH_NETWORK;
        if (StringUtils.isNotEmpty(institution)) {
            almaSruQueryPath = almaSruQueryPath.replace(NETWORK, institution.toUpperCase(Locale.getDefault()));
        }
        return buildFullUri(encodedCqlQuery, almaSruQueryPath);
    }

    private URI getAlmaURI(String encodedCqlQuery) throws URISyntaxException {
        String almaSruQueryPath = Config.ALMA_SRU_QUERY_PATH_NETWORK;
        return buildFullUri(encodedCqlQuery, almaSruQueryPath);
    }

    private URI buildFullUri(String encodedCqlQuery, String almaSruQueryPath) throws URISyntaxException {
        return new URIBuilder()
                .setScheme(HTTPS)
                .setHost(Config.getInstance().getAlmaSruHost())
                .setPath(almaSruQueryPath)
                .setParameter(VERSION_KEY, SRU_VERSION_1_2)
                .setParameter(OPERATION_KEY, OPERATION_SEARCH_RETRIEVE)
                .setParameter(RECORD_SCHEMA_KEY, RECORD_SCHEMA)
                .setParameter(MAXIMUM_RECORDS_KEY, ONE_RECORD_ONLY)
                .setParameter(START_RECORD_KEY, START_RECORD_1)
                .setParameter(QUERY_KEY, encodedCqlQuery)
                .build();
    }
}
