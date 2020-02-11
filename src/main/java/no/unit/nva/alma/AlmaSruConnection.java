package no.unit.nva.alma;

import no.unit.cql.formatter.CqlFormatter;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class AlmaSruConnection {

    public static final String HTTPS = "https";

    protected InputStreamReader connect(URL url) throws IOException {
        return new InputStreamReader(url.openStream());
    }

    protected URL generateQueryUrl(String scn, String creatorName)
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
                .setPath(Config.ALMA_SRU_QUERY_PATH)
                .setParameter("version", "1.2")
                .setParameter("operation", "searchRetrieve")
                .setParameter("recordSchema", "dc")
                .setParameter("maximumRecords", "50")
                .setParameter("startRecord", "1")
                .setParameter("query", encodedCqlQuery)
                .build();
        return uri.toURL();
    }

}
