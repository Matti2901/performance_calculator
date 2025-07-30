package org.apache.jackrabbit.server.remoting.davex.json.handler;

import org.apache.jackrabbit.commons.json.JsonHandler;

import javax.jcr.Value;
import java.io.IOException;

/**
 * Inner class used to parse a single value
 */
public final class ValueHandler implements JsonHandler {
    private final JsonDiffHandler jsonDiffHandler;
    private Value v;

    public ValueHandler(JsonDiffHandler jsonDiffHandler) {
        this.jsonDiffHandler = jsonDiffHandler;
    }

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
        v = (value == null) ? null : jsonDiffHandler.vf.createValue(value);
    }

    @Override
    public void value(boolean value) throws IOException {
        v = jsonDiffHandler.vf.createValue(value);
    }

    @Override
    public void value(long value) throws IOException {
        v = jsonDiffHandler.vf.createValue(value);
    }

    @Override
    public void value(double value) throws IOException {
        v = jsonDiffHandler.vf.createValue(value);
    }

    Value getValue() {
        return v;
    }
}
