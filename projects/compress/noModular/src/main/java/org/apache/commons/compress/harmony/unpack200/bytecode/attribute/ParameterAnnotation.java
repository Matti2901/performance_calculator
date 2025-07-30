package org.apache.commons.compress.harmony.unpack200.bytecode.attribute;

import org.apache.commons.compress.harmony.unpack200.bytecode.ClassConstantPool;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ParameterAnnotation represents the annotations on a single parameter.
 */
public class ParameterAnnotation {

    private final Annotation[] annotations;

    /**
     * Constructs a new instance.
     *
     * @param annotations Annotation.
     */
    public ParameterAnnotation(final Annotation[] annotations) {
        this.annotations = annotations;
    }

    /**
     * Gets all annotation class file entries.
     *
     * @return all annotation class file entries.
     */
    public List<Object> getClassFileEntries() {
        final List<Object> nested = new ArrayList<>();
        for (final Annotation annotation : annotations) {
            nested.addAll(annotation.getClassFileEntries());
        }
        return nested;
    }

    /**
     * Gets the cumulative length of all annotations.
     *
     * @return the cumulative length of all annotations.
     */
    public int getLength() {
        int length = 2;
        for (final Annotation annotation : annotations) {
            length += annotation.getLength();
        }
        return length;
    }

    /**
     * Resolves all annotations in this instance against the given pool.
     *
     * @param pool A class constant pool.
     */
    public void resolve(final ClassConstantPool pool) {
        for (final Annotation annotation : annotations) {
            annotation.resolve(pool);
        }
    }

    /**
     * Writes this body to the given output stream.
     *
     * @param dos the output stream.
     * @throws IOException if an I/O error occurs.
     */
    public void writeBody(final DataOutputStream dos) throws IOException {
        dos.writeShort(annotations.length);
        for (final Annotation annotation : annotations) {
            annotation.writeBody(dos);
        }
    }

}
