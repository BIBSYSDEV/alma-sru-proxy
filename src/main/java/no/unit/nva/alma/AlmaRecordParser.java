package no.unit.nva.alma;

import com.google.common.io.CharStreams;
import org.marc4j.MarcStreamReader;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AlmaRecordParser {

    public static final String MARC_DATAFIELD_245 = "245";
    public static final char MARC_SUBFIELD_A = 'a';
    public static final char MARC_SUBFIELD_B = 'b';
    public static final String TITLE_AS_JSON = "{\"title\":\"%s\"}";
    public static final String EMPTY_TITLE = "EMPTY TITLE";
    public static final String EMPTY_JSON = "{}";
    public static final String EMPTY_STRING = "";


    /**
     * Parses a SRU-response to extract the title of an marcxml-record.
     * @param inputStreamReader SRU-response
     * @return simple json with <code>title</code>
     * @throws IOException some stream reading went south
     */
    public String extractPublicationTitle(InputStreamReader inputStreamReader) throws IOException {
        InputStream inputStream =
                new ByteArrayInputStream(CharStreams.toString(inputStreamReader).getBytes(StandardCharsets.UTF_8));
        final MarcStreamReader reader = new MarcStreamReader(inputStream, StandardCharsets.UTF_8.name());
        if (reader.hasNext()) {
            Record record = reader.next();
            DataField datafield = (DataField) record.getVariableField(MARC_DATAFIELD_245);
            List<Subfield> subfields = datafield.getSubfields();
            for (Subfield subfield : subfields) {
                char code = subfield.getCode();
                String title = EMPTY_STRING;
                String valueB;
                if (code == MARC_SUBFIELD_A) {
                    title = subfield.getData();
                }
                if (code == MARC_SUBFIELD_B) {
                    valueB = subfield.getData();
                    title += valueB;
                }
                return String.format(TITLE_AS_JSON, title);
            }
            return String.format(TITLE_AS_JSON, EMPTY_TITLE);
        }
        return EMPTY_JSON;
    }
}
