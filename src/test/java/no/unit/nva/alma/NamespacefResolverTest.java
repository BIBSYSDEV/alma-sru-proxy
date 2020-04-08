package no.unit.nva.alma;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

import static no.unit.nva.alma.AlmaRecordParserTest.SRU_RESPONSE_2_HITS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class NamespacefResolverTest {

    @Test
    public void getNamespaceURI() {
        assertEquals("http://www.loc.gov/MARC21/slim", new NamespaceResolver(null).getNamespaceURI(null));
    }

    @Test
    public void getPrefix() throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        InputStream stream = AlmaRecordParserTest.class.getResourceAsStream(SRU_RESPONSE_2_HITS);
        Document document = documentBuilder.parse(stream);
        assertNull(new NamespaceResolver(document).getPrefix("http://www.loc.gov/MARC21/slim"));
    }

    @Test
    public void getPrefixes() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        InputStream stream = AlmaRecordParserTest.class.getResourceAsStream(SRU_RESPONSE_2_HITS);
        Document document = documentBuilder.parse(stream);
        assertNull(new NamespaceResolver(document).getPrefixes("http://www.loc.gov/MARC21/slim"));
    }
}