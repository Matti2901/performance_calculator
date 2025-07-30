package org.apache.commons.compress.harmony.unpack200.bytecode.attribute;

import org.apache.commons.compress.harmony.unpack200.bytecode.CP.CPConstant;
import org.apache.commons.compress.harmony.unpack200.bytecode.CP.CPNameAndType;
import org.apache.commons.compress.harmony.unpack200.bytecode.CP.CPUTF8;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPClass;
import org.apache.commons.compress.harmony.unpack200.bytecode.ClassConstantPool;
import org.apache.commons.compress.harmony.unpack200.bytecode.ClassFileEntry;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Pairs a tag and value.
 */
public class ElementValue {

    private final Object value;
    private final int tag;

    // resolved value index if it's a constant
    private int constantValueIndex = -1;

    /**
     * Constructs a new instance.
     *
     * @param tag   a tag.
     * @param value a value.
     */
    public ElementValue(final int tag, final Object value) {
        this.tag = tag;
        this.value = value;
    }

    /**
     * Gets a list of class file entries.
     *
     * @return a list of class file entries.
     */
    public List<Object> getClassFileEntries() {
        final List<Object> entries = new ArrayList<>(1);
        if (value instanceof CPNameAndType) {
            // used to represent enum, so don't include the actual CPNameAndType
            entries.add(((CPNameAndType) value).name);
            entries.add(((CPNameAndType) value).descriptor);
        } else if (value instanceof ClassFileEntry) {
            // TODO? ClassFileEntry is an Object
            entries.add(value);
        } else if (value instanceof ElementValue[]) {
            final ElementValue[] values = (ElementValue[]) value;
            for (final ElementValue value2 : values) {
                entries.addAll(value2.getClassFileEntries());
            }
        } else if (value instanceof Annotation) {
            entries.addAll(((Annotation) value).getClassFileEntries());
        }
        return entries;
    }

    /**
     * Gets the length.
     *
     * @return the length.
     */
    public int getLength() {
        switch (tag) {
            case 'B':
            case 'C':
            case 'D':
            case 'F':
            case 'I':
            case 'J':
            case 'S':
            case 'Z':
            case 'c':
            case 's':
                return 3;
            case 'e':
                return 5;
            case '[':
                int length = 3;
                final ElementValue[] nestedValues = (ElementValue[]) value;
                for (final ElementValue nestedValue : nestedValues) {
                    length += nestedValue.getLength();
                }
                return length;
            case '@':
                return 1 + ((Annotation) value).getLength();
        }
        return 0;
    }

    /**
     * Resolves this instance against a given pool.
     *
     * @param pool a class constant pool.
     */
    public void resolve(final ClassConstantPool pool) {
        if (value instanceof CPConstant) {
            ((CPConstant) value).resolve(pool);
            constantValueIndex = pool.indexOf((CPConstant) value);
        } else if (value instanceof CPClass) {
            ((CPClass) value).resolve(pool);
            constantValueIndex = pool.indexOf((CPClass) value);
        } else if (value instanceof CPUTF8) {
            ((CPUTF8) value).resolve(pool);
            constantValueIndex = pool.indexOf((CPUTF8) value);
        } else if (value instanceof CPNameAndType) {
            ((CPNameAndType) value).resolve(pool);
        } else if (value instanceof Annotation) {
            ((Annotation) value).resolve(pool);
        } else if (value instanceof ElementValue[]) {
            final ElementValue[] nestedValues = (ElementValue[]) value;
            for (final ElementValue nestedValue : nestedValues) {
                nestedValue.resolve(pool);
            }
        }
    }

    /**
     * Writes this instance to the given output stream.
     *
     * @param dos the output stream.
     * @throws IOException if an I/O error occurs.
     */
    public void writeBody(final DataOutputStream dos) throws IOException {
        dos.writeByte(tag);
        if (constantValueIndex != -1) {
            dos.writeShort(constantValueIndex);
        } else if (value instanceof CPNameAndType) {
            ((CPNameAndType) value).writeBody(dos);
        } else if (value instanceof Annotation) {
            ((Annotation) value).writeBody(dos);
        } else if (value instanceof ElementValue[]) {
            final ElementValue[] nestedValues = (ElementValue[]) value;
            dos.writeShort(nestedValues.length);
            for (final ElementValue nestedValue : nestedValues) {
                nestedValue.writeBody(dos);
            }
        } else {
            throw new Error("");
        }
    }
}
