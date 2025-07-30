package org.apache.jackrabbit.server.remoting.davex.json.importer;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.server.remoting.davex.diff.DiffException;
import org.apache.jackrabbit.server.remoting.davex.json.handler.JsonDiffHandler;
import org.apache.jackrabbit.spi.Name;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.jcr.Node;
import javax.jcr.PropertyType;
import java.io.IOException;

public abstract class ImportProperty extends ImportItem {

    static final String VALUE = "value";
    static final String TYPE = "type";
    static final String LOCAL_NAME = "property";

    public ImportProperty(String parentPath, String name) throws IOException {
        super(parentPath, name);
    }

    @Override
    public boolean mandatesImport(Node parent) {
        // TODO: verify again if a protected property (except for jcr:primaryType and jcr:mixinTypes) will ever change outside the scope of importing the whole tree.
        return false;
    }

    @Override
    public void importItem(ContentHandler contentHandler) throws IOException {
        try {
            AttributesImpl propAtts = new AttributesImpl();
            setNameAttribute(propAtts);
            setTypeAttribute(propAtts);
            contentHandler.startElement(Name.NS_SV_URI, LOCAL_NAME, Name.NS_SV_PREFIX + ":" + LOCAL_NAME, propAtts);
            startValueElement(contentHandler);
            contentHandler.endElement(Name.NS_SV_URI, LOCAL_NAME, Name.NS_SV_PREFIX + ":" + LOCAL_NAME);
        } catch (SAXException e) {
            throw new DiffException(e.getMessage(), e);
        }
    }

    void setTypeAttribute(AttributesImpl attr) {
        String type = null;
        if (name.equals(JcrConstants.JCR_PRIMARYTYPE)) {
            type = PropertyType.nameFromValue(PropertyType.NAME);
        } else if (name.equals(JcrConstants.JCR_MIXINTYPES)) {
            type = PropertyType.nameFromValue(PropertyType.NAME);
        } else if (name.equals(JcrConstants.JCR_UUID)) {
            type = PropertyType.nameFromValue(PropertyType.STRING);
        } else {
            type = PropertyType.nameFromValue(PropertyType.UNDEFINED);
        }
        attr.addAttribute(Name.NS_SV_URI, TYPE, Name.NS_SV_PREFIX + ":" + TYPE, TYPE_CDATA, type);
    }

    abstract void startValueElement(ContentHandler contentHandler) throws IOException;
}
