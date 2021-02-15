package no.unit.alma.almaUpdate;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class GetRecordByISBNConnection {

    public InputStreamReader connect(URL url) throws IOException {
        return new InputStreamReader(url.openStream());
    }

}
