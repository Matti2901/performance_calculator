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
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinition;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class ImportNode extends ImportItem {

    private static final String LOCAL_NAME = "node";

    private final JsonDiffHandler jsonDiffHandler;
    private ImportProp ntName;
    private ImportProp uuid;

    private List<ImportNode> childN = new ArrayList<ImportNode>();
    private List<ImportProperty> childP = new ArrayList<ImportProperty>();

    public ImportNode(JsonDiffHandler jsonDiffHandler, String parentPath, String name) throws IOException {
        super(parentPath, name);
        this.jsonDiffHandler = jsonDiffHandler;
    }

    private String getUUID() {
        if (uuid != null && uuid.value != null) {
            try {
                return uuid.value.getString();
            } catch (RepositoryException e) {
                JsonDiffHandler.log.error(e.getMessage());
            }
        }
        return null;
    }

    private String getPrimaryType() {
        if (ntName != null && ntName.value != null) {
            try {
                return ntName.value.getString();
            } catch (RepositoryException e) {
                JsonDiffHandler.log.error(e.getMessage());
            }
        }
        return null;
    }

    @Override
    public boolean mandatesImport(Node parent) {
        String primaryType = getPrimaryType();
        // Very simplistic and simplified test for protection that doesn't
        // take mixin types into account and ignores all JCR primary types
        if (!primaryType.startsWith(Name.NS_NT_PREFIX)) {
            try {
                NodeType nt = jsonDiffHandler.getNodeTypeManager().getNodeType(primaryType);
                for (NodeDefinition nd : nt.getChildNodeDefinitions()) {
                    if (nd.isProtected()) {
                        return true;
                    }
                }
                for (PropertyDefinition pd : nt.getPropertyDefinitions()) {
                    if (!pd.getName().startsWith(Name.NS_JCR_PREFIX) && pd.isProtected()) {
                        return true;
                    }
                }
            } catch (RepositoryException e) {
                JsonDiffHandler.log.warn(e.getMessage(), e);
            }
        }
        return false;
    }

    public void addProp(ImportProp prop) {
        if (prop.name.equals(JcrConstants.JCR_PRIMARYTYPE)) {
            ntName = prop;
        } else if (prop.name.equals(JcrConstants.JCR_UUID)) {
            uuid = prop;
        } else {
            // regular property
            childP.add(prop);
        }
    }

    public void addProp(ImportMvProp prop) {
        childP.add(prop);
    }

    public void addNode(ImportNode node) {
        childN.add(node);
    }

    @Override
    public void importItem(ContentHandler contentHandler) throws IOException {
        try {
            AttributesImpl attr = new AttributesImpl();
            setNameAttribute(attr);
            contentHandler.startElement(Name.NS_SV_URI, LOCAL_NAME, Name.NS_SV_PREFIX + ":" + LOCAL_NAME, attr);

            if (ntName != null && ntName.value != null) {
                ntName.importItem(contentHandler);
            }
            if (uuid != null && uuid.value != null) {
                uuid.importItem(contentHandler);
            }

            for (ImportProperty prop : childP) {
                prop.importItem(contentHandler);
            }

            for (ImportNode node : childN) {
                node.importItem(contentHandler);
            }
            contentHandler.endElement(Name.NS_SV_URI, LOCAL_NAME, Name.NS_SV_PREFIX + ":" + LOCAL_NAME);
        } catch (SAXException e) {
            throw new DiffException(e.getMessage(), e);
        }
    }

    @Override
    public void createItem(Node parent) throws RepositoryException, IOException {
        if (mandatesImport(parent)) {
            ContentHandler ch = JsonDiffHandler.createContentHandler(parent);
            try {
                ch.startDocument();
                importItem(ch);
                ch.endDocument();
            } catch (SAXException e) {
                throw new DiffException(e.getMessage(), e);
            }
        } else {
            Node n;
            String uuidValue = getUUID();
            String primaryType = getPrimaryType();
            if (uuidValue == null) {
                n = (primaryType == null) ? parent.addNode(name) : parent.addNode(name, primaryType);
            } else {
                n = JsonDiffHandler.importNode(parent, name, primaryType, uuidValue);
            }
            // create all properties
            for (ImportItem obj : childP) {
                obj.createItem(n);
            }
            // recursively create all child nodes
            for (ImportItem obj : childN) {
                obj.createItem(n);
            }
        }
    }
}
