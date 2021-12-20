package no.unit.alma;

import java.io.IOException;
import java.io.StringReader;
import java.time.ZonedDateTime;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class AvailabilityParser {

    private static final String XPATH_INSTITUTION_IDENTIFIER = "institutionIdentifier/value";
    private static final String XPATH_COPIES_COUNT = "holdingSimple/copiesSummary/copiesCount";
    private static final String XPATH_COPIES_SUMMARY_STATUS = "holdingSimple/copiesSummary/status";
    private static final String XPATH_AVAILABLE_FOR = "availableFor";
    private static final String XPATH_AVAILABLE_COUNT = "availableCount";
    private static final String XPATH_EARLIEST_DISPATCH_DATE = "earliestDispatchDate";
    private static final String XPATH_HOLDING = "//holding";
    private static final String AVAILABILITYCODE_AVAILAVLE_FOR_INTERLIBRARYLOAN = "1";
    private static final String ISILPREFIX = "NO-";

    public AvailabilityResponse getAvailabilityResponse(String xml, String libraryCode)
        throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        Document doc = parseXml(xml);
        return parseSruHoldingInfo(doc, libraryCode);
    }

    protected Document parseXml(String xml) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource inputSource = new InputSource(new StringReader(xml));
        return db.parse(inputSource);
    }

    protected AvailabilityResponse parseSruHoldingInfo(Document doc, String libraryCode)
            throws XPathExpressionException {
        AvailabilityResponse availabilityResponse = new AvailabilityResponse();
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList holdingNodeList = (NodeList) xpath.compile(XPATH_HOLDING).evaluate(doc, XPathConstants.NODESET);
        for (int holdingCounter = 0; holdingCounter < holdingNodeList.getLength(); holdingCounter++) {
            parseHoldingNode(libraryCode, availabilityResponse, xpath, holdingNodeList.item(holdingCounter));
        }
        return availabilityResponse;
    }

    private void parseHoldingNode(String libraryCode, AvailabilityResponse availabilityResponse, XPath xpath,
                                  Node holding) throws XPathExpressionException {
        String institution = (String) xpath.compile(XPATH_INSTITUTION_IDENTIFIER)
            .evaluate(holding, XPathConstants.STRING);
        if (institution.equalsIgnoreCase(ISILPREFIX + libraryCode)) {
            String parsedCopiesCount = (String) xpath.compile(XPATH_COPIES_COUNT)
                .evaluate(holding, XPathConstants.STRING);
            if (NumberUtils.isCreatable(parsedCopiesCount)) {
                int copies = Integer.parseInt(parsedCopiesCount);
                availabilityResponse.setTotalNumberOfItems(availabilityResponse.getTotalNumberOfItems() + copies);
            }
            NodeList statusNodes = (NodeList) xpath.compile(XPATH_COPIES_SUMMARY_STATUS)
                .evaluate(holding, XPathConstants.NODESET);
            for (int statusNodeCounter = 0; statusNodeCounter < statusNodes.getLength(); statusNodeCounter++) {
                parseStatus(availabilityResponse, xpath, statusNodes.item(statusNodeCounter));
            }
        }
    }

    private void parseStatus(AvailabilityResponse availabilityResponse, XPath xpath, Node statusNode)
            throws XPathExpressionException {
        String availableFor = (String) xpath.compile(XPATH_AVAILABLE_FOR).evaluate(statusNode, XPathConstants.STRING);
        if (AVAILABILITYCODE_AVAILAVLE_FOR_INTERLIBRARYLOAN.equalsIgnoreCase(availableFor)) {
            availabilityResponse.setNumberAvailForInterLibraryLoan(
                calculateNewAvailabilityCount(availabilityResponse, xpath, statusNode)
            );
            updateAvailableBeanWithAvailableDate(availabilityResponse, xpath, statusNode);
        }
    }

    private int calculateNewAvailabilityCount(AvailabilityResponse availabilityResponse, XPath xpath, Node statusNode)
            throws XPathExpressionException {
        int availableCount = Integer.parseInt(
            (String) xpath.compile(XPATH_AVAILABLE_COUNT).evaluate(statusNode, XPathConstants.STRING)
        );
        return availabilityResponse.getNumberAvailForInterLibraryLoan() + availableCount;
    }

    private void updateAvailableBeanWithAvailableDate(AvailabilityResponse availabilityResponse, XPath xpath,
                                                      Node statusNode) throws XPathExpressionException {
        ZonedDateTime earliestDispatchDate = getEarliestDispatchDate(xpath, statusNode);
        ZonedDateTime availableDate = getAvailableDate(availabilityResponse);
        if (earliestDispatchDate != null && (availableDate == null || availableDate.isBefore(earliestDispatchDate))) {
            availabilityResponse.setAvailableDate(earliestDispatchDate.toString());
        }
    }

    private ZonedDateTime getAvailableDate(AvailabilityResponse availabilityResponse) {
        String availableDateString = availabilityResponse.getAvailableDate();
        ZonedDateTime availableDate = null;
        if (StringUtils.isNotEmpty(availableDateString)) {
            availableDate = ZonedDateTime.parse(availableDateString);
        }
        return availableDate;
    }

    private ZonedDateTime getEarliestDispatchDate(XPath xpath, Node statusNode) throws XPathExpressionException {
        ZonedDateTime earliestDispatchDate = null;
        String earliestDispatchDateString = (String) xpath.compile(XPATH_EARLIEST_DISPATCH_DATE)
            .evaluate(statusNode, XPathConstants.STRING);
        if (StringUtils.isNotEmpty(earliestDispatchDateString)) {
            earliestDispatchDate = ZonedDateTime.parse(earliestDispatchDateString);
        }
        return earliestDispatchDate;
    }

}
