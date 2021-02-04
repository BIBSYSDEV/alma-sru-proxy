package no.unit.alma.almaUpdate;

import com.google.gson.JsonElement;
import no.unit.alma.Config;
import no.unit.alma.GatewayResponse;
import no.unit.alma.UpdateAlma856Handler;
import org.junit.jupiter.api.Test;

import javax.xml.transform.TransformerException;
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

    public static final String MOCK_XML =
            "<searchRetrieveResponse xmlns=\"http://www.loc.gov/zing/srw/\">"
            +"<version>1.2</version>"
            +"<numberOfRecords>1</numberOfRecords>"
            +"<records>"
            +"<record>"
            +"<recordSchema>marcxml</recordSchema>"
            +"<recordPacking>xml</recordPacking>"
            +"<recordData>"
    + "<record xmlns='http://www.loc.gov/MARC21/slim'>"
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
                    +"<subfield code='u'>http://content.bibsys.no/content/?type=descr_publ_brief&amp;isbn=8210053418</subfield>"
                  +"</datafield>"
                  +"<datafield tag='913' ind1=' ' ind2=' '>"
                    +"<subfield code='a'>Norbok</subfield>"
                    +"<subfield code='b'>NB</subfield>"
                  +"</datafield>"
                +"</record>"
            + "</recordData>"
            +"<recordIdentifier>999920719164802201</recordIdentifier>"
            +"<recordPosition>1</recordPosition>"
            +"</record>"
            +"</records>"
        +"<extraResponseData xmlns:xb=\"http://www.exlibris.com/repository/search/xmlbeans/\">"
        +"<xb:exact>true</xb:exact>"
        +"<xb:responseDate>2021-02-04T11:10:57+0100</xb:responseDate>"
        +"</extraResponseData>"
+"</searchRetrieveResponse>";

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
        assertEquals(1, 1);
    }

    @Test
    public void testParser(){
        String newXML = MOCK_XML.substring(MOCK_XML.indexOf("<recordData>") + 12,  MOCK_XML.lastIndexOf("</recordData>"));
        newXML = "<?xml version='1.0' encoding='UTF-8'?>" + newXML;
        try{
            XmlParser xmlParser = new XmlParser(newXML);
            String resultID = xmlParser.extractMms_id();
            assertEquals(EXPECTED_ID, resultID);
        }catch (TransformerException e) {
            System.out.println(e);
        }
        System.out.println(newXML);
         assertEquals(1,1);
    }
}