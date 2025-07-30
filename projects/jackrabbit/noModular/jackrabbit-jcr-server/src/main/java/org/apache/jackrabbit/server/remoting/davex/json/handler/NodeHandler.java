package org.apache.jackrabbit.server.remoting.davex.json.handler;

import org.apache.jackrabbit.commons.json.JsonHandler;
import org.apache.jackrabbit.server.remoting.davex.diff.DiffException;
import org.apache.jackrabbit.server.remoting.davex.json.importer.ImportItem;
import org.apache.jackrabbit.server.remoting.davex.json.importer.ImportMvProp;
import org.apache.jackrabbit.server.remoting.davex.json.importer.ImportNode;
import org.apache.jackrabbit.server.remoting.davex.json.importer.ImportProp;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.io.IOException;
import java.util.Stack;

/**
 * Inner class for parsing a simple json object defining a node and its
 * child nodes and/or child properties
 */
public final class NodeHandler implements JsonHandler {
    private final JsonDiffHandler jsonDiffHandler;
    private Node parent;
    private String key;

    private Stack<ImportItem> st = new Stack<ImportItem>();

    public NodeHandler(JsonDiffHandler jsonDiffHandler, Node parent, String nodeName) throws IOException {
        this.jsonDiffHandler = jsonDiffHandler;
        this.parent = parent;
        key = nodeName;
    }

    @Override
    public void object() throws IOException {
        ImportNode n;
        if (st.isEmpty()) {
            try {
                n = new ImportNode(jsonDiffHandler, parent.getPath(), key);
            } catch (RepositoryException e) {
                throw new DiffException(e.getMessage(), e);
            }

        } else {
            ImportItem obj = st.peek();
            n = new ImportNode(jsonDiffHandler, obj.getPath(), key);
            if (obj instanceof ImportNode) {
                ((ImportNode) obj).addNode(n);
            } else {
                throw new DiffException("Invalid DIFF format: The JSONArray may only contain simple values.");
            }
        }
        st.push(n);
    }

    @Override
    public void endObject() throws IOException {
        // element on stack must be ImportMvProp since array may only
        // contain simple values, no arrays/objects are allowed.
        ImportItem obj = st.pop();
        if (!((obj instanceof ImportNode))) {
            throw new DiffException("Invalid DIFF format.");
        }
        if (st.isEmpty()) {
            // everything parsed -> start adding all nodes and properties
            try {
                if (obj.mandatesImport(parent)) {
                    obj.importItem(JsonDiffHandler.createContentHandler(parent));
                } else {
                    obj.createItem(parent);
                }
            } catch (IOException e) {
                JsonDiffHandler.log.error(e.getMessage());
                throw new DiffException(e.getMessage(), e);
            } catch (RepositoryException e) {
                JsonDiffHandler.log.error(e.getMessage());
                throw new DiffException(e.getMessage(), e);
            }
        }
    }

    @Override
    public void array() throws IOException {
        ImportItem obj = st.peek();
        ImportMvProp prop = new ImportMvProp(jsonDiffHandler, obj.getPath(), key);
        if (obj instanceof ImportNode) {
            ((ImportNode) obj).addProp(prop);
        } else {
            throw new DiffException("Invalid DIFF format: The JSONArray may only contain simple values.");
        }
        st.push(prop);
    }

    @Override
    public void endArray() throws IOException {
        // element on stack must be ImportMvProp since array may only
        // contain simple values, no arrays/objects are allowed.
        ImportItem obj = st.pop();
        if (!((obj instanceof ImportMvProp))) {
            throw new DiffException("Invalid DIFF format: The JSONArray may only contain simple values.");
        }
    }

    @Override
    public void key(String key) throws IOException {
        this.key = key;
    }

    @Override
    public void value(String value) throws IOException {
        Value v = (value == null) ? null : jsonDiffHandler.vf.createValue(value);
        value(v);
    }

    @Override
    public void value(boolean value) throws IOException {
        value(jsonDiffHandler.vf.createValue(value));
    }

    @Override
    public void value(long value) throws IOException {
        Value v = jsonDiffHandler.vf.createValue(value);
        value(v);
    }

    @Override
    public void value(double value) throws IOException {
        value(jsonDiffHandler.vf.createValue(value));
    }

    private void value(Value v) throws IOException {
        ImportItem obj = st.peek();
        if (obj instanceof ImportMvProp) {
            ((ImportMvProp) obj).values.add(v);
        } else {
            ((ImportNode) obj).addProp(new ImportProp(jsonDiffHandler, obj.getPath(), key, v));
        }
    }
}
