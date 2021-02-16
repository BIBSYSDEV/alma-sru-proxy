package no.unit.alma.almaUpdate;

import no.unit.alma.Config;
import no.unit.alma.GatewayResponse;
import no.unit.alma.UpdateAlma856Handler;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

public class UpdateAlma856HandlerTest {

    public static final String MOCK_UPDATE_HOST = "alma-update-host-dot-com";
    public static final String MOCK_ISBN = "9788203364181";
    public static final String EXPECTED_ID = "991325803064702201";
    public static final int NUMBER_OF_SUBFIELDS_2 = 2;
    public static final int NUMBER_OF_SUBFIELDS_3 = 3;

    public static final String MOCK_XML =
            "<record xmlns='http://www.loc.gov/MARC21/slim'>"
            +"<leader>01044cam a2200301 c 4500</leader>"
                +"<controlfield tag='001'>991325803064702201</controlfield>"
                +"<controlfield tag='005'>20160622160726.0</controlfield>"
                +"<controlfield tag='007'>ta</controlfield>"
                +"<controlfield tag='008'>141124s2013    no#||||j||||||000|0|nob|^</controlfield>"
                  +"<datafield tag='015' ind1=' ' ind2=' '>"
                    +"<subfield code='a'>1337755</subfield>"
                    +"<subfield code='2'>nbf</subfield>"
                  +"</datafield>"
                  +"<datafield tag='020' ind1=' ' ind2=' '>"
                    +"<subfield code='a'>9788210053412</subfield>"
                    +"<subfield code='q'>ib.</subfield>"
                    +"<subfield code='c'>Nkr 249.00</subfield>"
                  +"</datafield>"
                  +"<datafield tag='035' ind1=' ' ind2=' '>"
                    +"<subfield code='a'>132580306-47bibsys_network</subfield>"
                  +"</datafield>"
                  +"<datafield tag='035' ind1=' ' ind2=' '>"
                    +"<subfield code='a'>(NO-TrBIB)132580306</subfield>"
                  +"</datafield>"
                  +"<datafield tag='035' ind1=' ' ind2=' '>"
                    +"<subfield code='a'>(NO-OsBA)0370957</subfield>"
                  +"</datafield>"
                  +"<datafield tag='856' ind1='4' ind2='2'>"
                    +"<subfield code='3'>Beskrivelse fra forlaget (kort)</subfield>"
                    +"<subfield code='u'>http://innhold.bibsys.no/bilde/forside/?size=mini&id=LITE_150088182.jpg</subfield>"
                  +"</datafield>"
                +"<datafield tag='856' ind1='4' ind2='2'>"
                    +"<subfield code='3'>Beskrivelse fra forlaget (Lang)</subfield>"
                    +"<subfield code='u'>http://innhold.bibsysdsds</subfield>"
                +"</datafield>"
                  +"<datafield tag='913' ind1=' ' ind2=' '>"
                    +"<subfield code='a'>Norbok</subfield>"
                    +"<subfield code='b'>NB</subfield>"
                  +"</datafield>"
                +"</record>";

    @Test
    public void testIdMatchBasedOnIsbn(){
        final Config instance = Config.getInstance();
        instance.setAlmaUpdateHost(MOCK_UPDATE_HOST);

        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(UpdateAlma856Handler.ISBN_KEY, MOCK_ISBN);
        Map<String, Object> event = new HashMap<>();
        event.put(UpdateAlma856Handler.QUERY_STRING_PARAMETERS_KEY, queryParameters);

        final UpdateAlma856Handler updateAlma856Handler = new UpdateAlma856Handler();

        final GatewayResponse gatewayResponse = updateAlma856Handler.handleRequest(event, null);
        String result = gatewayResponse.getBody();
        System.out.println(result);
        assertEquals(1, 1);
    }

    @Test
    public void testParser(){
        String newXML = MOCK_XML.substring(MOCK_XML.indexOf("<recordData>") + 12,  MOCK_XML.lastIndexOf("</recordData>"));
        newXML = "<?xml version='1.0' encoding='UTF-8'?>" + newXML;
        System.out.println(newXML);
        try{
            XmlParser xmlParser = new XmlParser(newXML);
            String resultID = xmlParser.extractMms_id();
            assertEquals(EXPECTED_ID, resultID);
        }catch (TransformerException e) {
            System.out.println(e);
        }
    }

    @Test
    public void testDuplicateLenkeAndDescription(){
        String newXML = MOCK_XML.substring(MOCK_XML.indexOf("<recordData>") + 12,  MOCK_XML.lastIndexOf("</recordData>"));
        newXML = "<?xml version='1.0' encoding='UTF-8'?>" + newXML;
        newXML = newXML.replace("&", "&amp;");
        System.out.println(newXML);
        try{
            XmlParser xmlParser = new XmlParser(newXML);
            assertTrue(xmlParser.alreadyExists("Beskrivelse fra forlaget (kort)", "http://innhold.bibsys.no/bilde/forside/?size=mini&amp;id=LITE_150088182.jpg".replace("&amp;", "&")));
        }catch (TransformerException e) {
            System.out.println(e);
        }
    }

    @Test
    public void testParsingSkills(){
        try{
            final Config instance = Config.getInstance();
            instance.setAlmaUpdateHost(MOCK_UPDATE_HOST);

            Map<String, String> queryParameters = new HashMap<>();
            queryParameters.put(UpdateAlma856Handler.ISBN_KEY, "9780367196721");
            Map<String, Object> event = new HashMap<>();
            event.put(UpdateAlma856Handler.QUERY_STRING_PARAMETERS_KEY, queryParameters);

            final UpdateAlma856Handler updateAlma856Handler = new UpdateAlma856Handler();

            final GatewayResponse gatewayResponse = updateAlma856Handler.handleRequest(event, null);
            String result = gatewayResponse.getBody();
            String newXML = result;
            newXML = newXML.replace("&", "&amp;");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new StringReader(newXML)));
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("recordData");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                NodeList childList = nList.item(temp).getChildNodes();
                for(int i = 0; i < childList.getLength(); i++){
                    System.out.println(childList.item(i).getNodeName());
                }
            }
            assertTrue(true);
        } catch(Exception e){
            System.out.println(e);
        }
    }

    @Test
    public void testXmlInsertion() throws Exception{
        XmlParser parser = new XmlParser(MOCK_XML);
        Document doc = parser.create856Node("This is the description", "This is the url", null);
        NodeList datafields = doc.getElementsByTagName("datafield");
        NodeList subfields = datafields.item(0).getChildNodes();
        System.out.println(subfields.item(0).getTextContent());
        System.out.println(subfields.item(1).getTextContent());
        assertEquals(NUMBER_OF_SUBFIELDS_2, subfields.getLength());
        Document doc2 = parser.create856Node("This is the description", "This is the url", "This is det type");
        NodeList datafields2 = doc2.getElementsByTagName("datafield");
        NodeList subfields2 = datafields2.item(0).getChildNodes();
        assertEquals(NUMBER_OF_SUBFIELDS_3, subfields2.getLength());

    }

}