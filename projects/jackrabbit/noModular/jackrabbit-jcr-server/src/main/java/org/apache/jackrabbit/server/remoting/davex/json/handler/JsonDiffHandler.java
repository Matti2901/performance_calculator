/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.server.remoting.davex.json.handler;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.webdav.JcrValueType;
import org.apache.jackrabbit.server.remoting.davex.diff.DiffException;
import org.apache.jackrabbit.server.remoting.davex.diff.DiffHandler;
import org.apache.jackrabbit.server.remoting.davex.protect.ProtectedRemoveManager;
import org.apache.jackrabbit.server.util.RequestData;
import org.apache.jackrabbit.commons.json.JsonHandler;
import org.apache.jackrabbit.commons.json.JsonParser;
import org.apache.jackrabbit.util.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Item;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.ItemDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

/** <code>JsonDiffHandler</code>... */
public class JsonDiffHandler implements DiffHandler {

    public static final Logger log = LoggerFactory.getLogger(JsonDiffHandler.class);

    private static final String ORDER_POSITION_AFTER = "#after";
    private static final String ORDER_POSITION_BEFORE = "#before";
    private static final String ORDER_POSITION_FIRST = "#first";
    private static final String ORDER_POSITION_LAST = "#last";

    private final Session session;
    final ValueFactory vf;
    private final String requestItemPath;
    private final RequestData data;
    private final ProtectedRemoveManager protectedRemoveManager;

    private NodeTypeManager ntManager;

    public JsonDiffHandler(Session session, String requestItemPath, RequestData data) throws RepositoryException {
        this(session, requestItemPath, data, null);
    }

    public JsonDiffHandler(Session session, String requestItemPath, RequestData data, ProtectedRemoveManager protectedRemoveManager) throws RepositoryException {
        this.session = session;
        this.requestItemPath = requestItemPath;
        this.data = data;
        vf = session.getValueFactory();
        this.protectedRemoveManager = protectedRemoveManager;
    }

    //--------------------------------------------------------< DiffHandler >---
    /**
     * @see DiffHandler#addNode(String, String)
     */
    @Override
    public void addNode(String targetPath, String diffValue) throws DiffException {
        if (diffValue == null || !(diffValue.startsWith("{") && diffValue.endsWith("}"))) {
            throw new DiffException("Invalid 'addNode' value '" + diffValue + "'");
        }

        try {
            String itemPath = getItemPath(targetPath);
            String parentPath = Text.getRelativeParent(itemPath, 1);
            String nodeName = Text.getName(itemPath);

            addNode(parentPath, nodeName, diffValue);

        } catch (RepositoryException e) {
            throw new DiffException(e.getMessage(), e);
        }
    }

    /**
     * @see DiffHandler#setProperty(String, String) 
     */
    @Override
    public void setProperty(String targetPath, String diffValue) throws DiffException {
        try {
            String itemPath = getItemPath(targetPath);
            Item item = session.getItem(Text.getRelativeParent(itemPath, 1));
            if (!item.isNode()) {
                throw new DiffException("No such node " + itemPath, new ItemNotFoundException(itemPath));
            }

            Node parent = (Node) item;
            String propName = Text.getName(itemPath);

            if (JcrConstants.JCR_MIXINTYPES.equals(propName)) {
                setMixins(parent, extractValuesFromRequest(targetPath));
            } else if (JcrConstants.JCR_PRIMARYTYPE.equals(propName)) {
                setPrimaryType(parent, extractValuesFromRequest(targetPath));
            } else {
                if (diffValue == null || diffValue.length() == 0) {
                    // single valued property with value present in multipart.
                    Value[] vs = extractValuesFromRequest(targetPath);
                    if (vs.length == 0) {
                        if (parent.hasProperty(propName)) {
                            // avoid problems with single vs. multi valued props.
                            parent.getProperty(propName).remove();
                        } else {
                            // property does not exist -> stick to rule that missing
                            // [] indicates single valued.
                            parent.setProperty(propName, (Value) null);
                        }
                    } else if (vs.length == 1) {
                        parent.setProperty(propName, vs[0]);
                    } else {
                        throw new DiffException("Unexpected number of values in multipart. Was " + vs.length + " but expected 1.");
                    }
                } else if (diffValue.startsWith("[") && diffValue.endsWith("]")) {
                    // multivalued property
                    if (diffValue.length() == 2) {
                        // empty array OR values in multipart
                        Value[] vs = extractValuesFromRequest(targetPath);
                        parent.setProperty(propName, vs);
                    } else {
                        // json array
                        Value[] vs = extractValues(diffValue);
                        parent.setProperty(propName, vs);
                    }
                } else {
                    // single prop value included in the diff
                    Value v = extractValue(diffValue);
                    parent.setProperty(propName, v);
                }
            }
        } catch (RepositoryException e) {
            throw new DiffException(e.getMessage(), e);
        } catch (IOException e) {
            if (e instanceof DiffException) {
                throw (DiffException) e;
            } else {
                throw new DiffException(e.getMessage(), e);
            }
        }
    }

    /**
     * @see DiffHandler#remove(String, String) 
     */
    @Override
    public void remove(String targetPath, String diffValue) throws DiffException {
        if (!(diffValue == null || diffValue.trim().length() == 0)) {
            throw new DiffException("'remove' may not have a diffValue.");
        }
        try {
            String itemPath = getItemPath(targetPath);
            Item item = session.getItem(itemPath);

            ItemDefinition def = (item.isNode()) ? ((Node) item).getDefinition() : ((Property) item).getDefinition();
            if (def.isProtected()) {
                // delegate to the manager.
                if (protectedRemoveManager == null || !protectedRemoveManager.remove(session, itemPath)) {
                   throw new ConstraintViolationException("Cannot remove protected node: no suitable handler configured.");
                }
            } else {
                item.remove();
            }
        } catch (RepositoryException e) {
            throw new DiffException(e.getMessage(), e);
        }
    }

    /**
     * @see DiffHandler#move(String, String) 
     */
    @Override
    public void move(String targetPath, String diffValue) throws DiffException {
        if (diffValue == null || diffValue.length() == 0) {
            throw new DiffException("Invalid 'move' value '" + diffValue + "'");
        }
        try {
            String srcPath = getItemPath(targetPath);
            String orderPosition = getOrderPosition(diffValue);
            if (orderPosition == null) {
                // simple move
                String destPath = getItemPath(diffValue);
                session.move(srcPath, destPath);
            } else {
                String srcName = Text.getName(srcPath);
                int pos = diffValue.lastIndexOf('#');
                String destName = (pos == 0) ? null : Text.getName(diffValue.substring(0, pos));

                Item item = session.getItem(Text.getRelativeParent(srcPath, 1));
                if (!item.isNode()) {
                    throw new ItemNotFoundException(srcPath);
                }
                Node parent = (Node) item;

                if (ORDER_POSITION_FIRST.equals(orderPosition)) {
                    if (destName != null) {
                        throw new DiffException(ORDER_POSITION_FIRST + " may not have a leading destination.");
                    }
                    destName = Text.getName(parent.getNodes().nextNode().getPath());
                    parent.orderBefore(srcName, destName);
                } else if (ORDER_POSITION_LAST.equals(orderPosition)) {
                    if (destName != null) {
                        throw new DiffException(ORDER_POSITION_LAST + " may not have a leading destination.");
                    }
                    parent.orderBefore(srcName, null);
                } else if (ORDER_POSITION_AFTER.equals(orderPosition)) {
                    if (destName == null) {
                        throw new DiffException(ORDER_POSITION_AFTER + " must have a leading destination.");
                    }
                    for (NodeIterator it = parent.getNodes(); it.hasNext();) {
                        Node child = it.nextNode();
                        if (destName.equals(child.getName())) {
                            if (it.hasNext()) {
                                destName = Text.getName(it.nextNode().getName());
                            } else {
                                destName = null;
                            }
                            break;
                        }
                    }
                    // reorder... if no child node matches the original destName
                    // the reorder will fail. no extra check.
                    parent.orderBefore(srcName, destName);
                } else {
                    // standard jcr reorder (before)
                    parent.orderBefore(srcName, destName);
                }
            }

        } catch (RepositoryException e) {
            throw new DiffException(e.getMessage(), e);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * 
     * @param diffPath
     * @return
     * @throws RepositoryException
     */
    public String getItemPath(String diffPath) throws RepositoryException {
        StringBuffer itemPath;
        if (!diffPath.startsWith("/")) {
            // diff path is relative to the item path retrieved from the
            // request URI -> calculate item path.
            itemPath = new StringBuffer(requestItemPath);
            if (!requestItemPath.endsWith("/")) {
                itemPath.append('/');
            }
            itemPath.append(diffPath);
        } else {
            itemPath = new StringBuffer(diffPath);
        }
        return normalize(itemPath.toString());
    }

    private void addNode(String parentPath, String nodeName, String diffValue)
            throws DiffException, RepositoryException {
        Item item = session.getItem(parentPath);
        if (!item.isNode()) {
            throw new ItemNotFoundException(parentPath);
        }

        Node parent = (Node) item;
        try {
            NodeHandler hndlr = new NodeHandler(this, parent, nodeName);
            new JsonParser(hndlr).parse(diffValue);
        } catch (IOException e) {
            if (e instanceof DiffException) {
                throw (DiffException) e;
            } else {
                throw new DiffException(e.getMessage(), e);
            }
        }
    }

    public NodeTypeManager getNodeTypeManager() throws RepositoryException {
        if (ntManager == null) {
            ntManager = session.getWorkspace().getNodeTypeManager();
        }
        return ntManager;
    }

    private static String normalize(String path) {
        if (path.indexOf('.') == -1) {
            return path;
        }
        String[]  elems = Text.explode(path, '/', false);
        LinkedList<String> queue = new LinkedList<String>();
        String last = "..";
        for (String segm : elems) {
            if ("..".equals(segm) && !"..".equals(last)) {
                queue.removeLast();
                if (queue.isEmpty()) {
                    last = "..";
                } else {
                    last = queue.getLast();
                }
            } else if (!".".equals(segm)) {
                last = segm;
                queue.add(last);
            }
        }
        return "/" + Text.implode(queue.toArray(new String[queue.size()]), "/");
    }

    public static ContentHandler createContentHandler(Node parent) throws RepositoryException {
        return parent.getSession().getImportContentHandler(parent.getPath(), ImportUUIDBehavior.IMPORT_UUID_COLLISION_THROW);
    }

    public static Node importNode(Node parent, String nodeName, String ntName,
                                  String uuid) throws RepositoryException {

        String uri = "http://www.jcp.org/jcr/sv/1.0";
        String prefix = "sv:";

        ContentHandler ch = createContentHandler(parent);
        try {
            ch.startDocument();

            String nN = "node";
            AttributesImpl attrs = new AttributesImpl();
            attrs.addAttribute(uri, "name", prefix + "name", "CDATA", nodeName);
            ch.startElement(uri, nN, prefix + nN, attrs);

            // primary node type
            String pN = "property";
            attrs = new AttributesImpl();
            attrs.addAttribute(uri, "name", prefix + "name", "CDATA", JcrConstants.JCR_PRIMARYTYPE);
            attrs.addAttribute(uri, "type", prefix + "type", "CDATA", PropertyType.nameFromValue(PropertyType.NAME));
            ch.startElement(uri, pN, prefix + pN, attrs);
            ch.startElement(uri, "value", prefix + "value", new AttributesImpl());
            char[] val = ntName.toCharArray();
            ch.characters(val, 0, val.length);
            ch.endElement(uri, "value", prefix + "value");
            ch.endElement(uri, pN, prefix + pN);

            // uuid
            attrs = new AttributesImpl();
            attrs.addAttribute(uri, "name", prefix + "name", "CDATA", JcrConstants.JCR_UUID);
            attrs.addAttribute(uri, "type", prefix + "type", "CDATA", PropertyType.nameFromValue(PropertyType.STRING));
            ch.startElement(uri, pN, prefix + pN, attrs);
            ch.startElement(uri, "value", prefix + "value", new AttributesImpl());
            val = uuid.toCharArray();
            ch.characters(val, 0, val.length);
            ch.endElement(uri, "value", prefix + "value");
            ch.endElement(uri, pN, prefix + pN);

            ch.endElement(uri, nN, prefix + nN);
            ch.endDocument();

        } catch (SAXException e) {
            throw new RepositoryException(e);
        }

        Node n = null;
        NodeIterator it = parent.getNodes(nodeName);
        while (it.hasNext()) {
            n = it.nextNode();
        }
        if (n == null) {
            throw new RepositoryException("Internal error: No child node added.");
        }
        return n;
    }

    private static void setPrimaryType(Node n, Value[] values) throws RepositoryException, DiffException {
        if (values.length == 1) {
            String ntName = values[0].getString();
            if (!ntName.equals(n.getPrimaryNodeType().getName())) {
                n.setPrimaryType(ntName);
            } // else: same primaryType as before -> nothing to do.
        } else {
            throw new DiffException("Invalid diff: jcr:primarytype cannot have multiple values, nor can it's value be removed.");
        }
    }

    public static void setMixins(Node n, Value[] values) throws RepositoryException {
        if (values.length == 0) {
            // remove all mixins
            NodeType[] mixins = n.getMixinNodeTypes();
            for (NodeType mixin : mixins) {
                String mixinName = mixin.getName();
                n.removeMixin(mixinName);
            }
        } else {
            List<String> newMixins = new ArrayList<String>(values.length);
            for (Value value : values) {
                newMixins.add(value.getString());
            }
            NodeType[] mixins = n.getMixinNodeTypes();
            for (NodeType mixin : mixins) {
                String mixinName = mixin.getName();
                if (!newMixins.remove(mixinName)) {
                    n.removeMixin(mixinName);
                }
            }
            for (String newMixinName : newMixins) {
                n.addMixin(newMixinName);
            }
        }
    }

    private static String getOrderPosition(String diffValue) {
        String position = null;
        if (diffValue.indexOf('#') > -1) {
            if (diffValue.endsWith(ORDER_POSITION_FIRST) ||
                    diffValue.endsWith(ORDER_POSITION_LAST) ||
                    diffValue.endsWith(ORDER_POSITION_BEFORE) ||
                    diffValue.endsWith(ORDER_POSITION_AFTER)) {
                position = diffValue.substring(diffValue.lastIndexOf('#'));
            } // else: apparently # is part of the move path.
        }
        return position;
    }

    public Value[] extractValuesFromRequest(String paramName) throws RepositoryException, IOException {
        ValueFactory vf = session.getValueFactory();
        Value[] vs;
        InputStream[] ins = data.getFileParameters(paramName);
        if (ins != null) {
            vs = new Value[ins.length];
            for (int i = 0; i < ins.length; i++) {
                vs[i] = vf.createValue(ins[i]);
            }
        } else {
            String[] strs = data.getParameterValues(paramName);
            if (strs == null) {
                vs = new Value[0];
            } else {
                List<Value> valList = new ArrayList<Value>(strs.length);
                for (int i = 0; i < strs.length; i++) {
                    if (strs[i] != null) {
                        String[] types = data.getParameterTypes(paramName);
                        int type = (types == null || types.length <= i) ? PropertyType.UNDEFINED : JcrValueType.typeFromContentType(types[i]);
                        if (type == PropertyType.UNDEFINED) {
                            valList.add(vf.createValue(strs[i]));
                        } else {
                            valList.add(vf.createValue(strs[i], type));
                        }
                    }
                }
                vs = valList.toArray(new Value[valList.size()]);
            }
        }
        return vs;
    }

    private Value extractValue(String diffValue) throws RepositoryException, DiffException, IOException {
        ValueHandler hndlr = new ValueHandler(this);
        // surround diff value { key : } to make it parsable
        new JsonParser(hndlr).parse("{\"a\":"+diffValue+"}");

        return hndlr.getValue();
    }

    private Value[] extractValues(String diffValue) throws RepositoryException, DiffException, IOException {
        ValuesHandler hndlr = new ValuesHandler();
        // surround diff value { key : } to make it parsable
        new JsonParser(hndlr).parse("{\"a\":" + diffValue + "}");

        return hndlr.getValues();
    }

    //--------------------------------------------------------------------------

    /**
     * Inner class used to parse the values from a simple json array
     */
    private final class ValuesHandler implements JsonHandler {
        private List<Value> values = new ArrayList<Value>();

        @Override
        public void object() throws IOException {
            // ignore
        }
        @Override
        public void endObject() throws IOException {
            // ignore
        }
        @Override
        public void array() throws IOException {
            // ignore
        }
        @Override
        public void endArray() throws IOException {
            // ignore
        }
        @Override
        public void key(String key) throws IOException {
            // ignore
        }

        @Override
        public void value(String value) throws IOException {
            if (value != null) {
                values.add(vf.createValue(value));
            } else {
                log.warn("Null element for a multivalued property -> Ignore.");
            }
        }
        @Override
        public void value(boolean value) throws IOException {
            values.add(vf.createValue(value));
        }
        @Override
        public void value(long value) throws IOException {
            values.add(vf.createValue(value));
        }
        @Override
        public void value(double value) throws IOException {
            values.add(vf.createValue(value));
        }

        private Value[] getValues() {
            return values.toArray(new Value[values.size()]);
        }
    }

}
