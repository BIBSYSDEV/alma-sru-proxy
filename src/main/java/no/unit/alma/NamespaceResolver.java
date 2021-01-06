package no.unit.alma;

import org.w3c.dom.Document;

import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;

public class NamespaceResolver implements NamespaceContext {
    public static final String MARC21_PREFIX = "http://www.loc.gov/MARC21/slim";

    // the delegate
    private final transient Document sourceDocument;

    /**
     * This constructor stores the source document to search the namespaces in
     * it.
     *
     * @param document source document
     */
    public NamespaceResolver(Document document) {
        sourceDocument = document;
    }

    /**
     * The lookup for the namespace uris is delegated to the stored document.
     *
     * @param prefix to search for
     * @return uri
     */
    @Override
    public String getNamespaceURI(String prefix) {
        return MARC21_PREFIX;
    }

    /**
     * This method is not needed in this context, but can be implemented in a
     * similar way.
     */
    @Override
    public String getPrefix(String namespaceURI) {
        return sourceDocument.lookupPrefix(namespaceURI);
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceURI) {
        // not implemented yet
        return null;
    }

}
