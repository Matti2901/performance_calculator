package org.apache.jackrabbit.server.remoting.davex.json.importer;

import org.apache.jackrabbit.server.remoting.davex.diff.DiffException;
import org.apache.jackrabbit.spi.Name;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.AttributesImpl;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.io.IOException;

public abstract class ImportItem {

    static final String TYPE_CDATA = "CDATA";

    final String parentPath;
    final String name;
    final String path;

    public ImportItem(String parentPath, String name) throws IOException {
        if (name == null) {
            throw new DiffException("Invalid DIFF format: NULL key.");
        }
        this.name = name;
        this.parentPath = parentPath;
        this.path = parentPath + "/" + name;
    }

    void setNameAttribute(AttributesImpl attr) {
        attr.addAttribute(Name.NS_SV_URI, "name", Name.NS_SV_PREFIX + ":name", TYPE_CDATA, name);
    }

    public String getPath() {
        return path;
    }

    public abstract boolean mandatesImport(Node parent);

    public abstract void createItem(Node parent) throws RepositoryException, IOException;

    public abstract void importItem(ContentHandler contentHandler) throws IOException;
}
