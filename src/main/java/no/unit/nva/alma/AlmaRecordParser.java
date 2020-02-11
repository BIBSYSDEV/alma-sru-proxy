package no.unit.nva.alma;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStreamReader;

public class AlmaRecordParser {

    public String extractPublicationData(InputStreamReader inputStreamReader) throws XMLStreamException {
        String json = "";
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLEventReader reader = xmlInputFactory.createXMLEventReader(inputStreamReader);
        while (reader.hasNext()) {
            XMLEvent nextEvent = reader.nextEvent();
            if (nextEvent.isStartElement()) {
                StartElement startElement = nextEvent.asStartElement();
                if (startElement.getName().getLocalPart().equals("dc:title")) {
                    nextEvent = reader.nextEvent();
                    final String title = nextEvent.asCharacters().getData();
                    json = "{\"title\":\"" + title + "\"}";
                }
            }
        }
        return json;
    }
}
