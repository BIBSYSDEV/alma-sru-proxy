package no.unit.alma.almaUpdate;

import org.marc4j.MarcXmlReader;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class XmlParser {

    public static final String EMPTY_STRING = "";
    public static final String CLOSING_BRACKET = ")";
    public static final String MARC_TAG_001 = "001";
    public static final String MARC_TAG_856 = "856";
    public static final String MARC_PREFIX = "marc:";

    /**
     * Parses a SRU-response to extract the title of an marc21xml-record.
     *
     * @param xml marc21-xml record
     * @return simple json with <code>title</code>
     * @throws TransformerException         some stream reading went south
     */
    private Record record;

    public XmlParser(String xml) throws TransformerException{
        this.record = asMarcRecord(asDocument(xml));
    }
    public Record getRecord(){
        return record;
    }

    public void setRecord(String xml) throws TransformerException {
        this.record = asMarcRecord(asDocument(xml));
    }

    @SuppressWarnings("PMD.NcssCount")
    public String extractMms_id() {
        List<ControlField> controlFieldList = record.getControlFields();
        for (ControlField controlField : controlFieldList) {
            String controlFieldTag = controlField.getTag();
            if (controlFieldTag.equals(MARC_TAG_001)) {
                return controlField.getData();
            }
        }
        return null;
    }


    private Record asMarcRecord(Document doc) throws TransformerException {
        ByteArrayOutputStream outputStream = removeStylesheet(doc);
        return new MarcXmlReader(new ByteArrayInputStream(outputStream.toByteArray())).next();
    }


    private ByteArrayOutputStream removeStylesheet(Document result) throws TransformerException {
        Source source = new DOMSource(result);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Result outputTarget = new StreamResult(outputStream);
        TransformerFactory.newInstance().newTransformer().transform(source, outputTarget);
        return outputStream;
    }

    private Document asDocument(String sruxml) {
        Document document = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            String removedMarcInSruXml = sruxml.replace(MARC_PREFIX, EMPTY_STRING);
            InputSource is = new InputSource(new StringReader(removedMarcInSruXml));
            document = builder.parse(is);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println("Something went wrong during parsing of sruResponse. " + e.getMessage());
        }
        return document;
    }
}
