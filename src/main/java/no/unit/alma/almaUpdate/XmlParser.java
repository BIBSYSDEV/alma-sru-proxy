package no.unit.alma.almaUpdate;

import org.marc4j.MarcXmlReader;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
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


public class XmlParser {

    public static final String EMPTY_STRING = "";
    public static final String CLOSING_BRACKET = ")";
    public static final String XML_PROTOCOL = "<?xml version='1.0' encoding='UTF-8'?>";
    public static final String MARC_TAG_001 = "001";
    public static final String MARC_TAG_856 = "856";
    public static final char MARC_CODE_U = 'u';
    public static final char MARC_CODE_3 = '3';
    public static final String MARC_PREFIX = "marc:";
    public static final String NODE_TEMPLATE = "<record>"
            + "<datafield tag='856' ind1='4' ind2='2'>"
            + "<subfield code='3'>1</subfield>"
            + "<subfield code='u'>2</subfield>"
            + "<subfield code='q'>3</subfield>"
            + "</datafield>"
            + "</record>";

    /**
     * Parses a SRU-response to extract the title of an marc21xml-record.
     *
     * @param xml marc21-xml record
     * @return simple json with <code>title</code>
     * @throws TransformerException         some stream reading went south
     */
    private Record record;

    public XmlParser(String xml) throws TransformerException{
        this.record = asMarcRecord(asDocument(xml.replace("&", "&amp;")));
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

    public Document create856Node(String description, String url, String descType) throws ParserConfigurationException, IOException, SAXException, TransformerException{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newDefaultInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(NODE_TEMPLATE));
        Document doc = db.parse(is);
        NodeList datafields = doc.getElementsByTagName("datafield");
        NodeList subfields = datafields.item(0).getChildNodes();
        subfields.item(0).setTextContent(description);
        subfields.item(1).setTextContent(url);
        if(descType != null){
            subfields.item(2).setTextContent(descType);
        } else{
            datafields.item(0).removeChild(subfields.item(2));
        }
        return doc;
    }

    public boolean alreadyExists(String description, String url){
        List<DataField> dataFieldList = record.getDataFields();
        for (DataField dataField : dataFieldList) {
            Subfield subField_U;
            Subfield subField_3;
            String dataFieldTag = dataField.getTag();
            if (dataFieldTag.equals(MARC_TAG_856)) {
                subField_U = dataField.getSubfield(MARC_CODE_U);
                subField_3 = dataField.getSubfield(MARC_CODE_3);
                if(subField_U != null && subField_3 != null) {
                    System.out.println(subField_3.getData() + "    " + subField_U.getData());
                    if(subField_U.getData().equals(url) && subField_3.getData().equals(description)) {
                        return true;
                    }
                }
            }
        }
        return false;
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
