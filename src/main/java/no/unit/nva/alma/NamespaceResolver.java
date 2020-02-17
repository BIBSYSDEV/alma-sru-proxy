package no.unit.nva.alma;

import org.w3c.dom.Document;

import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;

public class NamespaceResolver implements NamespaceContext {
    // the delegate
    private Document sourceDocument;

    /**
     * This constructor stores the source document to search the namespaces in
     * it.
     *
     * @param document
     *            source document
     */
    public NamespaceResolver(Document document) {
        sourceDocument = document;
    }

    /**
     * The lookup for the namespace uris is delegated to the stored document.
     *
     * @param prefix
     *            to search for
     * @return uri
     */
    public String getNamespaceURI(String prefix) {
        if (prefix.equals("srw")) {
            return "http://www.loc.gov/zing/srw/";
        } else {
            return "http://www.loc.gov/MARC21/slim";
        }
    }

    /**
     * This method is not needed in this context, but can be implemented in a
     * similar way.
     */
    public String getPrefix(String namespaceURI) {
        return sourceDocument.lookupPrefix(namespaceURI);
    }

    public Iterator<String> getPrefixes(String namespaceURI) {
        // not implemented yet
        return null;
    }
}
