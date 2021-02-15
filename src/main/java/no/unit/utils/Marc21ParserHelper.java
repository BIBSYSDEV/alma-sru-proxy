package no.unit.utils;

import no.unit.alma.sru.ParsingException;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
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
import java.io.StringReader;
import java.io.StringWriter;

public class Marc21ParserHelper {

    public static final String MARC_PREFIX = "marc:";
    public static final String MARC_TAG_020 = "020";
    public static final char MARC_CODE_A = 'a';

    public static String getCorrectPostsFromIsbnAsXML(String xml, String isbn) throws ParsingException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            String removedMarcInSruXml = xml.replace(MARC_PREFIX, "");
            String removedSpaceBetweenElements = removedMarcInSruXml.replaceAll(">[\\s\r\n]*<", "><");
            InputSource is = new InputSource(new StringReader(removedSpaceBetweenElements));
            Document document = builder.parse(is);

            XPath path = XPathFactory.newInstance().newXPath();
            NodeList recordsNodes = (NodeList) path.compile("searchRetrieveResponse/records")
                    .evaluate(document, XPathConstants.NODE);

            String recordsAsXML = "<records>";
            for (int i = 0; recordsNodes.getLength() > i; i++) {
                path = XPathFactory.newInstance().newXPath();
                Node recordNode = (Node) path.compile("recordData/record")
                        .evaluate(recordsNodes.item(i), XPathConstants.NODE);

                Document marcFriendlyDoc = builder.newDocument();
                Node importedNode = marcFriendlyDoc.importNode(recordNode, true);
                marcFriendlyDoc.appendChild(importedNode);

                Record record = asMarcRecords(marcFriendlyDoc).next();

                for (DataField dataField : record.getDataFields()) {
                    String datafieldTag = dataField.getTag();
                    if (MARC_TAG_020.equals(datafieldTag) && theDataFieldHasCorrectIsbnInSubfield(dataField, isbn)) {
                        recordsAsXML += nodeToString(recordNode);
                    }
                }
            }
            recordsAsXML += "</records>";
            return recordsAsXML;
        } catch (ParserConfigurationException | SAXException | IOException
                | TransformerException | XPathExpressionException e) {
            throw new ParsingException("Could not parse xml to marc21 data", e);
        }
    }

    private static boolean theDataFieldHasCorrectIsbnInSubfield(DataField dataField, String isbn) {
        Subfield subfield = dataField.getSubfield(MARC_CODE_A);
        if (subfield != null) {
            String isbnFromDataField = subfield.getData();
            return isbn.equals(isbnFromDataField);
        }
        return false;
    }

    private static MarcXmlReader asMarcRecords(Document doc) throws TransformerException {
        ByteArrayOutputStream outputStream = removeStylesheet(doc);
        return new MarcXmlReader(new ByteArrayInputStream(outputStream.toByteArray()));
    }

    private static ByteArrayOutputStream removeStylesheet(Document result) throws TransformerException {
        Source source = new DOMSource(result);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Result outputTarget = new StreamResult(outputStream);
        TransformerFactory.newInstance().newTransformer().transform(source, outputTarget);
        return outputStream;
    }

    private static String nodeToString(Node node) throws TransformerException {
        StringWriter buf = new StringWriter();
        Transformer xform = TransformerFactory.newInstance().newTransformer();
        xform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        xform.transform(new DOMSource(node), new StreamResult(buf));
        return buf.toString();
    }
}
