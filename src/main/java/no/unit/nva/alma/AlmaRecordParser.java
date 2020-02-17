package no.unit.nva.alma;

import com.google.common.io.CharStreams;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class AlmaRecordParser {

    public static final String MARC_DATAFIELD_245 = "245";
    public static final char MARC_SUBFIELD_A = 'a';
    public static final char MARC_SUBFIELD_B = 'b';
    public static final String BLANK = " ";
    public static final String MARC_RECORD_XPATH = "//marc:record";
    public static final String MARC_NAMESPACE = "http://www.loc.gov/MARC21/slim";
    public static final String COLLECTION_ELEMENT = "collection";
    public static final int FIRST_NODE = 0;


    /**
     * Parses a SRU-response to extract the title of an marcxml-record.
     *
     * @param inputStreamReader SRU-response
     * @return simple json with <code>title</code>
     * @throws IOException some stream reading went south
     */
    public Reference extractPublicationTitle(InputStreamReader inputStreamReader) throws IOException, TransformerException, SAXException, ParserConfigurationException, XPathExpressionException {

        Reference reference = new Reference();
        try (InputStream inputStream = new ByteArrayInputStream(
                CharStreams.toString(inputStreamReader).getBytes(StandardCharsets.UTF_8))) {

            Record record = getFirstMarcRecord(inputStream);

            if (isNull(record)) {
                return reference;
            }

            DataField datafield = (DataField) record.getVariableField(MARC_DATAFIELD_245);
            String title = datafield.getSubfields().stream()
                    .filter(this::filterSubfields)
                    .collect(Collectors.toMap(Subfield::getCode, Subfield::getData))
                    .entrySet().stream().sorted(Map.Entry.comparingByKey())
                    .map(Map.Entry::getValue).collect(Collectors.joining(BLANK));

            reference.setTitle(title);
            return reference;
        }
    }

    private boolean filterSubfields(Subfield subfield) {
        return subfield.getCode() == MARC_SUBFIELD_A || subfield.getCode() == MARC_SUBFIELD_B;
    }

    private Record getFirstMarcRecord(InputStream inputStream) throws TransformerException,
            XPathExpressionException, IOException, SAXException, ParserConfigurationException {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(inputStream);
        document.getDocumentElement().normalize();
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new NamespaceResolver(document));
        Node element = (Node) xpath.evaluate(MARC_RECORD_XPATH, document.getDocumentElement(), XPathConstants.NODE);

        if (isNull(element)) {
            return null;
        }

        Document result = documentBuilder.newDocument();
        Node collection = result.createElementNS(MARC_NAMESPACE, COLLECTION_ELEMENT);
        result.appendChild(collection);
        Node node = result.importNode(element, true);
        result.getElementsByTagNameNS(MARC_NAMESPACE, COLLECTION_ELEMENT).item(FIRST_NODE).appendChild(node);

        Source source = new DOMSource(result);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Result outputTarget = new StreamResult(outputStream);
        TransformerFactory.newInstance().newTransformer().transform(source, outputTarget);
        return new MarcXmlReader(new ByteArrayInputStream(outputStream.toByteArray())).next();
    }
}
