package no.unit.nva.alma;

import com.google.common.io.CharStreams;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;
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
import java.util.Optional;
import java.util.stream.Collectors;

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
     * @throws TransformerException some stream reading went south
     * @throws SAXException some stream reading went south
     * @throws ParserConfigurationException some stream reading went south
     * @throws XPathExpressionException some stream reading went south
     */
    public Reference extractPublicationTitle(InputStreamReader inputStreamReader) throws IOException,
            TransformerException, SAXException, ParserConfigurationException, XPathExpressionException {

        Reference reference = new Reference();
        try (InputStream inputStream = new ByteArrayInputStream(
                CharStreams.toString(inputStreamReader).getBytes(StandardCharsets.UTF_8))) {

            Optional<Record> record = getFirstMarcRecord(inputStream);

            record.ifPresent(value -> reference.setTitle(extractTitleFromMarcRecord(value).get()));
        }
        return reference;
    }

    private Optional<String> extractTitleFromMarcRecord(Record record) {
        VariableField variableField = record.getVariableField(MARC_DATAFIELD_245);
        if (variableField instanceof  DataField) {
            DataField datafield245 = (DataField) variableField;
            return getTitleFromMarc245(datafield245);
        } else {
            return Optional.empty();
        }
    }

    private Optional<String> getTitleFromMarc245(DataField datafield245) {
        return Optional.of(datafield245.getSubfields().stream()
                .filter(this::containsSubFieldAOrSubFieldB)
                .collect(Collectors.toMap(Subfield::getCode, Subfield::getData))
                .entrySet().stream().sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue).collect(Collectors.joining(BLANK)));
    }

    private boolean containsSubFieldAOrSubFieldB(Subfield subfield) {
        return subfield.getCode() == MARC_SUBFIELD_A || subfield.getCode() == MARC_SUBFIELD_B;
    }

 private Optional<Record> getFirstMarcRecord(InputStream inputStream) throws TransformerException,
            XPathExpressionException, IOException, SAXException, ParserConfigurationException {

        DocumentBuilder documentBuilder = createDocumentBuilder();
        Optional<Node> element = extractFirstMarcRecord(inputStream, documentBuilder);

        Optional<Record> optionalRecord = Optional.empty();
        if (element.isPresent()) {
            Document result = documentBuilder.newDocument();
            addExtractedRecordToResultDoc(element.get(), result);

            ByteArrayOutputStream outputStream = perfomMysteriousTransformation(result);
            optionalRecord =Optional.ofNullable(readRecordFromXMLStream(outputStream));
        }
        return optionalRecord;
    }

    private Record readRecordFromXMLStream(ByteArrayOutputStream outputStream) {
        return new MarcXmlReader(new ByteArrayInputStream(outputStream.toByteArray())).next();
    }

    private ByteArrayOutputStream perfomMysteriousTransformation(Document result) throws TransformerException {
        Source source = new DOMSource(result);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Result outputTarget = new StreamResult(outputStream);
        TransformerFactory.newInstance().newTransformer().transform(source, outputTarget);
        return  outputStream;
    }

    private void addExtractedRecordToResultDoc(Node element, Document result) {
        Node collection = result.createElementNS(MARC_NAMESPACE, COLLECTION_ELEMENT);
        result.appendChild(collection);
        Node node = result.importNode(element, true);
        result.getElementsByTagNameNS(MARC_NAMESPACE, COLLECTION_ELEMENT).item(FIRST_NODE).appendChild(node);
    }

    private Optional<Node> extractFirstMarcRecord(InputStream inputStream, DocumentBuilder documentBuilder)
        throws SAXException, IOException, XPathExpressionException {
        Document document = parseInputStreamToXmlDoc(inputStream, documentBuilder);
        return searchForTheFirstMarcRecord(document);
    }

    private Document parseInputStreamToXmlDoc(InputStream inputStream, DocumentBuilder documentBuilder)
        throws SAXException, IOException {
        Document document = documentBuilder.parse(inputStream);
        document.getDocumentElement().normalize();
        return document;
    }

    private Optional<Node> searchForTheFirstMarcRecord(Document document) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new NamespaceResolver(document));
        return Optional.ofNullable((Node) xpath.evaluate(MARC_RECORD_XPATH,
                document.getDocumentElement(),
                XPathConstants.NODE));
    }

    private DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        return documentBuilderFactory.newDocumentBuilder();
    }
}
