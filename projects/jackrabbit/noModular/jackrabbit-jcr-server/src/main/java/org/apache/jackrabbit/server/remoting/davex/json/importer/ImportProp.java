package org.apache.jackrabbit.server.remoting.davex.json.importer;

import org.apache.jackrabbit.server.remoting.davex.diff.DiffException;
import org.apache.jackrabbit.server.remoting.davex.json.handler.JsonDiffHandler;
import org.apache.jackrabbit.spi.Name;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import java.io.IOException;

public final class ImportProp extends ImportProperty {

    private final JsonDiffHandler jsonDiffHandler;
    final Value value;

    public ImportProp(JsonDiffHandler jsonDiffHandler, String parentPath, String name, Value value) throws IOException {
        super(parentPath, name);
        this.jsonDiffHandler = jsonDiffHandler;
        try {
            if (value == null) {
                this.value = jsonDiffHandler.extractValuesFromRequest(getPath())[0];
            } else {
                this.value = value;
            }
        } catch (RepositoryException e) {
            throw new DiffException(e.getMessage(), e);
        }
    }

    @Override
    public void createItem(Node parent) throws RepositoryException {
        parent.setProperty(name, value);
    }

    @Override
    void startValueElement(ContentHandler contentHandler) throws IOException {
        try {
            String str = value.getString();
            contentHandler.startElement(Name.NS_SV_URI, VALUE, Name.NS_SV_PREFIX + ":" + VALUE, new AttributesImpl());
            contentHandler.characters(str.toCharArray(), 0, str.length());
            contentHandler.endElement(Name.NS_SV_URI, VALUE, Name.NS_SV_PREFIX + ":" + VALUE);
        } catch (SAXException e) {
            throw new DiffException(e.getMessage());
        } catch (ValueFormatException e) {
            throw new DiffException(e.getMessage());
        } catch (RepositoryException e) {
            throw new DiffException(e.getMessage());
        }
    }
}
