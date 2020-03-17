package no.unit.nva.alma;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AlmaRecordParserTest {

    public static final String SRU_RESPONSE_2_HITS = "/SRU_response_2_hits.xml";
    public static final String SRU_RESPONSE_WITH_SUBTITLE = "/SRU_response_with_subtitle.xml";
    public static final String SRU_RESPONSE_END_TRUNCATED = "/SRU_response_truncated.xml";
    public static final String SRU_RESPONSE_START_TRUNCATED = "/SRU_response_top_truncated.xml";
    public static final String SRU_RESPONSE_ZERO_HITS = "/SRU_zero_hits.xml";
    public static final String EXPECTED_TITLE = "Bedriftsintern telekommunikasjon";
    public static final String EXPECTED_SUBTITLE =
            "Emotions and legal judgements : normative issues and empirical findings";


    @Test
    public void testExtractPublicationTitle() throws IOException, ParserConfigurationException, TransformerException,
            SAXException, XPathExpressionException {
        InputStream stream = AlmaRecordParserTest.class.getResourceAsStream(SRU_RESPONSE_2_HITS);
        InputStreamReader inputStreamReader = new InputStreamReader(stream);
        AlmaRecordParser almaRecordParser = new AlmaRecordParser();
        Reference reference = almaRecordParser.extractPublicationTitle(inputStreamReader);
        assertTrue(reference.getTitle().contains(EXPECTED_TITLE));
    }

    @Test
    public void testExtractPublicationSubtitle() throws IOException, ParserConfigurationException, TransformerException,
            SAXException, XPathExpressionException {
        InputStream stream = AlmaRecordParserTest.class.getResourceAsStream(SRU_RESPONSE_WITH_SUBTITLE);
        InputStreamReader inputStreamReader = new InputStreamReader(stream);
        AlmaRecordParser almaRecordParser = new AlmaRecordParser();
        Reference reference = almaRecordParser.extractPublicationTitle(inputStreamReader);
        assertTrue(reference.getTitle().contains(EXPECTED_SUBTITLE));
    }

    @Test
    public void testExtractPublicationTitle_FromZeroHits() throws IOException, ParserConfigurationException,
            TransformerException, SAXException, XPathExpressionException {
        InputStream stream = AlmaRecordParserTest.class.getResourceAsStream(SRU_RESPONSE_ZERO_HITS);
        InputStreamReader inputStreamReader = new InputStreamReader(stream);
        AlmaRecordParser almaRecordParser = new AlmaRecordParser();
        Reference reference = almaRecordParser.extractPublicationTitle(inputStreamReader);
        assertNull(reference.getTitle());
    }

    @Test
    public void testExtractPublicationTitle_MalformedSruResponseOnEnd() throws IOException,
            ParserConfigurationException, TransformerException, SAXException, XPathExpressionException {
        InputStream stream = AlmaRecordParserTest.class.getResourceAsStream(SRU_RESPONSE_END_TRUNCATED);
        InputStreamReader inputStreamReader = new InputStreamReader(stream);
        AlmaRecordParser almaRecordParser = new AlmaRecordParser();
        Assertions.assertThrows(SAXException.class, () -> almaRecordParser.extractPublicationTitle(inputStreamReader));
    }

    @Test
    public void testExtractPublicationTitle_MalformedSruResponseOnStart() throws IOException,
            ParserConfigurationException, TransformerException, SAXException, XPathExpressionException {
        InputStream stream = AlmaRecordParserTest.class.getResourceAsStream(SRU_RESPONSE_START_TRUNCATED);
        InputStreamReader inputStreamReader = new InputStreamReader(stream);
        AlmaRecordParser almaRecordParser = new AlmaRecordParser();
        Assertions.assertThrows(SAXException.class, () -> almaRecordParser.extractPublicationTitle(inputStreamReader));
    }
}
