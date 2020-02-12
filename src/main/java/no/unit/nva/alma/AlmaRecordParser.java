package no.unit.nva.alma;

import com.google.common.io.CharStreams;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class AlmaRecordParser {

    public static final String MARC_DATAFIELD_245 = "245";
    public static final char MARC_SUBFIELD_A = 'a';
    public static final char MARC_SUBFIELD_B = 'b';
    public static final String TITLE_AS_JSON = "{\"title\":\"%s\"}";
    public static final String EMPTY_TITLE = "EMPTY TITLE";
    public static final String EMPTY_JSON = "{}";
    public static final String EMPTY_STRING = "";
    public static final String DELIMITER_A = "\\A";
    public static final String RECORDS_START_TAG = "<records>";
    public static final String RECORDS_END_TAG = "</records>";
    public static final String DESIRED_COLLECTION_START_TAG = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<collection xmlns=\"http://www.loc.gov/MARC21/slim\">";
    public static final String COLLECTION_END_TAG = "</collection>";
    public static final String BLANK = " ";
    public static final String NUMBER_OF_RECORDS_IS_ZERO = "<numberOfRecords>0</numberOfRecords>";
    public static final String MALFORMED_XML_IN_SRU_RESPONSE = "Malformed xml in SRU Response.";


    /**
     * Parses a SRU-response to extract the title of an marcxml-record.
     *
     * @param inputStreamReader SRU-response
     * @return simple json with <code>title</code>
     * @throws IOException some stream reading went south
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public String extractPublicationTitle(InputStreamReader inputStreamReader) throws IOException {
        InputStream inputStream =
                new ByteArrayInputStream(CharStreams.toString(inputStreamReader).getBytes(StandardCharsets.UTF_8));
        // since Marc4j is not able to parse sru response schema, we have to work with the default marc4j-xml schema
        // but therefor we need to replace some tags
        inputStream = this.prepareSruResponseForMarc4jParsing(inputStream);
        final MarcXmlReader reader = new MarcXmlReader(inputStream);
        if (reader.hasNext()) {
            Record record = reader.next();
            DataField datafield = (DataField) record.getVariableField(MARC_DATAFIELD_245);
            StringBuilder title = new StringBuilder(EMPTY_TITLE);
            List<Subfield> subfields = datafield.getSubfields();
            String valueB = null;
            for (Subfield subfield : subfields) {
                char code = subfield.getCode();
                if (code == MARC_SUBFIELD_A) {
                    title = new StringBuilder(subfield.getData());
                }
                if (code == MARC_SUBFIELD_B) {
                    valueB = subfield.getData();
                }
            }
            if (StringUtils.isNotEmpty(valueB)) {
                title.append(BLANK).append(valueB);
            }
            return String.format(TITLE_AS_JSON, title.toString());
        }
        return EMPTY_JSON;
    }

    private InputStream prepareSruResponseForMarc4jParsing(InputStream inputStream) throws IOException {
        String text;
        StringBuilder parsedText = new StringBuilder();
        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            text = scanner.useDelimiter(DELIMITER_A).next();
        }
        if (text.contains(NUMBER_OF_RECORDS_IS_ZERO)) {
            parsedText.append(DESIRED_COLLECTION_START_TAG);
        } else if (text.contains(RECORDS_START_TAG) && text.contains(RECORDS_END_TAG)) {
            final int startIndex = text.indexOf(RECORDS_START_TAG);
            final int endIndex = text.indexOf(RECORDS_END_TAG);
            text = text.substring(startIndex, endIndex);
            text = text.replace(RECORDS_START_TAG, DESIRED_COLLECTION_START_TAG);
            parsedText.append(text);
        } else {
            throw new IOException(MALFORMED_XML_IN_SRU_RESPONSE);
        }
        parsedText.append(COLLECTION_END_TAG);
        System.out.println("SRU records:" + parsedText.toString());
        return new ByteArrayInputStream(parsedText.toString().getBytes());
    }
}
