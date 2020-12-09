package no.unit.alma;

import java.util.Locale;
import no.unit.cql.formatter.CqlFormatter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class AlmaSruConnection {

    public static final String HTTPS = "https";
    public static final String VERSION_KEY = "version";
    public static final String SRU_VERSION_1_2 = "1.2";
    public static final String OPERATION_KEY = "operation";
    public static final String OPERATION_SEARCH_RETRIEVE = "searchRetrieve";
    public static final String RECORD_SCHEMA_KEY = "recordSchema";
    public static final String RECORD_SCHEMA = "marcxml";
    public static final String MAXIMUM_RECORDS_KEY = "maximumRecords";
    public static final String MAX_NUMBER_RECORDS = "2";
    public static final String ONE_RECORD_ONLY = "1";
    public static final String START_RECORD_KEY = "startRecord";
    public static final String START_RECORD_1 = "1";
    public static final String QUERY_KEY = "query";

    protected InputStreamReader connect(URL url) throws IOException {
        return new InputStreamReader(url.openStream());
    }

    protected URL generateQueryByAuthorUrl(String scn, String creatorName)
            throws MalformedURLException, URISyntaxException {
        String encodedCqlQuery = new CqlFormatter()
                .withRetrospective(true)
                .withSorting(true)
                .withAuthorityId(scn)
                .withCreator(creatorName)
                .encode();
        URI uri = new URIBuilder()
                .setScheme(HTTPS)
                .setHost(Config.getInstance().getAlmaSruHost())
                .setPath(Config.ALMA_SRU_QUERY_PATH_NETWORK)
                .setParameter(VERSION_KEY, SRU_VERSION_1_2)
                .setParameter(OPERATION_KEY, OPERATION_SEARCH_RETRIEVE)
                .setParameter(RECORD_SCHEMA_KEY, RECORD_SCHEMA)
                .setParameter(MAXIMUM_RECORDS_KEY, MAX_NUMBER_RECORDS)
                .setParameter(START_RECORD_KEY, START_RECORD_1)
                .setParameter(QUERY_KEY, encodedCqlQuery)
                .build();
        return uri.toURL();
    }

    protected URL generateQueryByMmsIdUrl(String mmsId, String institution)
            throws MalformedURLException, URISyntaxException {
        String encodedCqlQuery = new CqlFormatter()
                .withRetrospective(false)
                .withSorting(false)
                .withMmsId(mmsId)
                .withInstitution(institution)
                .encode();
        String almaSruQueryPath = Config.ALMA_SRU_QUERY_PATH_NETWORK;
        if (StringUtils.isNotEmpty(institution)) {
            almaSruQueryPath = almaSruQueryPath.replace("NETWORK", institution.toUpperCase(Locale.getDefault()));
        }
        URI uri = new URIBuilder()
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
        return uri.toURL();
    }

}
