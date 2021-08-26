package no.unit.alma;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import nva.commons.core.ioutils.IoUtils;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class AvailabilityParserTest {

    public static final String SRU_HOLDINGS_SIMPLE_XML = "sru_holdings_simple.xml";
    public static final String SRU_HOLDINGS_COMPLEX_XML = "sru_holdings_complex.xml";
    public static final String SRU_HOLDINGS_WITH_STRUCTURED_XML = "sru_holdings_with_structured.xml";
    public static final String SRU_HOLDINGS_WITH_ONLY_COPY_XML = "sru_holdings_with_only_copy.xml";
    private final AvailabilityParser availabilityParser = new AvailabilityParser();

    private Document createDocument(String fileName) throws SAXException, IOException, ParserConfigurationException {
        final String xmlDoc = IoUtils.stringFromResources(Path.of(fileName));
        return availabilityParser.parseXml(xmlDoc);
    }

    @Test
    public void retrieveSruHoldingInfo_sample1()
        throws XPathExpressionException, IOException, ParserConfigurationException, SAXException {
        String libraryCode = "1042300";
        Document sruExampleSimple = createDocument(SRU_HOLDINGS_SIMPLE_XML);
        AvailabilityResponse holdingInfo = availabilityParser.parseSruHoldingInfo(sruExampleSimple, libraryCode);
        assertEquals(2, holdingInfo.getTotalNumberOfItems());
        assertEquals(0, holdingInfo.getNumberAvailForInterLibraryLoan());
        System.out.println(holdingInfo.getAvailableDate());
    }

    @Test
    public void retrieveSruHoldingInfo_sample2()
        throws XPathExpressionException, IOException, ParserConfigurationException, SAXException {
        String libraryCode = "1150401";
        Document sruExampleComplex = createDocument(SRU_HOLDINGS_COMPLEX_XML);
        AvailabilityResponse holdingInfo = availabilityParser.parseSruHoldingInfo(sruExampleComplex, libraryCode);
        assertEquals(3, holdingInfo.getTotalNumberOfItems());
        assertEquals(2, holdingInfo.getNumberAvailForInterLibraryLoan());
        System.out.println(holdingInfo.getAvailableDate());
    }

    @Test
    public void retrieveSruHoldingInfo_sample3()
        throws XPathExpressionException, IOException, ParserConfigurationException, SAXException {
        String libraryCode = "1190206";
        Document sruExampleWithStructured = createDocument(SRU_HOLDINGS_WITH_STRUCTURED_XML);
        AvailabilityResponse holdingInfo = availabilityParser
            .parseSruHoldingInfo(sruExampleWithStructured, libraryCode);
        assertEquals(5, holdingInfo.getTotalNumberOfItems());
        assertEquals(5, holdingInfo.getNumberAvailForInterLibraryLoan());
        System.out.println(holdingInfo.getAvailableDate());
    }

    @Test
    public void retrieveSruHoldingInfo_sample4()
        throws XPathExpressionException, IOException, ParserConfigurationException, SAXException {
        String libraryCode = "1110301";
        Document sruExampleWithOnlyCopy = createDocument(SRU_HOLDINGS_WITH_ONLY_COPY_XML);
        AvailabilityResponse holdingInfo = availabilityParser.parseSruHoldingInfo(sruExampleWithOnlyCopy, libraryCode);
        assertEquals(0, holdingInfo.getTotalNumberOfItems());
        assertEquals(0, holdingInfo.getNumberAvailForInterLibraryLoan());
        System.out.println(holdingInfo.getAvailableDate());
    }
}