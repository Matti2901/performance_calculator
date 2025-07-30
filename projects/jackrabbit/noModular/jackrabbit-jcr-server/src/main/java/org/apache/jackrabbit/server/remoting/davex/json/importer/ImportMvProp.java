package org.apache.jackrabbit.server.remoting.davex.json.importer;

import org.apache.jackrabbit.JcrConstants;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ImportMvProp extends ImportProperty {

    private final JsonDiffHandler jsonDiffHandler;
    public List<Value> values = new ArrayList<Value>();

    public ImportMvProp(JsonDiffHandler jsonDiffHandler, String parentPath, String name) throws IOException {
        super(parentPath, name);
        this.jsonDiffHandler = jsonDiffHandler;
    }

    @Override
    public void createItem(Node parent) throws RepositoryException {
        Value[] vls = values.toArray(new Value[values.size()]);
        if (JcrConstants.JCR_MIXINTYPES.equals(name)) {
            JsonDiffHandler.setMixins(parent, vls);
        } else {
            parent.setProperty(name, vls);
        }
    }

    @Override
    void startValueElement(ContentHandler contentHandler) throws IOException {
        try {
            // Multi-valued property with values present in the request
            // multi-part
            if (values.size() == 0) {
                values = Arrays.asList(jsonDiffHandler.extractValuesFromRequest(getPath()));
            }

            for (Value v : values) {
                String str = v.getString();
                contentHandler.startElement(Name.NS_SV_URI, VALUE, Name.NS_SV_PREFIX + ":" + VALUE, new AttributesImpl());
                contentHandler.characters(str.toCharArray(), 0, str.length());
                contentHandler.endElement(Name.NS_SV_URI, VALUE, Name.NS_SV_PREFIX + ":" + VALUE);
            }
        } catch (SAXException e) {
            throw new DiffException(e.getMessage());
        } catch (ValueFormatException e) {
            throw new DiffException(e.getMessage());
        } catch (RepositoryException e) {
            throw new DiffException(e.getMessage());
        }
    }
}
