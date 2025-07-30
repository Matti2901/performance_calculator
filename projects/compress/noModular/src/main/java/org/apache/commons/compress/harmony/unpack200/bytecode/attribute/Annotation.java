package org.apache.commons.compress.harmony.unpack200.bytecode.attribute;

import org.apache.commons.compress.harmony.unpack200.bytecode.CP.CPUTF8;
import org.apache.commons.compress.harmony.unpack200.bytecode.ClassConstantPool;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the annotation structure for class file attributes.
 */
public class Annotation {

    private final int numPairs;
    private final CPUTF8[] elementNames;
    private final ElementValue[] elementValues;
    private final CPUTF8 type;

    // Resolved values
    private int typeIndex;
    private int[] nameIndexes;

    /**
     * Constructs a new instance.
     *
     * @param numPairs      Number of pairs, matches the lengths of {@code elementNames} and {@code elementValues}.
     * @param type          Type.
     * @param elementNames  Element names.
     * @param elementValues Element values.
     */
    public Annotation(final int numPairs, final CPUTF8 type, final CPUTF8[] elementNames, final ElementValue[] elementValues) {
        this.numPairs = numPairs;
        this.type = type;
        this.elementNames = elementNames;
        this.elementValues = elementValues;
    }

    /**
     * Gets a list of class file entries.
     *
     * @return a list of class file entries.
     */
    public List<Object> getClassFileEntries() {
        final List<Object> entries = new ArrayList<>();
        for (int i = 0; i < elementNames.length; i++) {
            entries.add(elementNames[i]);
            entries.addAll(elementValues[i].getClassFileEntries());
        }
        entries.add(type);
        return entries;
    }

    /**
     * Gets the cumulative length of all element values.
     *
     * @return the cumulative length of all element values.
     */
    public int getLength() {
        int length = 4;
        for (int i = 0; i < numPairs; i++) {
            length += 2;
            length += elementValues[i].getLength();
        }
        return length;
    }

    /**
     * Resolves this instance against a given pool.
     *
     * @param pool a class constant pool.
     */
    public void resolve(final ClassConstantPool pool) {
        type.resolve(pool);
        typeIndex = pool.indexOf(type);
        nameIndexes = new int[numPairs];
        for (int i = 0; i < elementNames.length; i++) {
            elementNames[i].resolve(pool);
            nameIndexes[i] = pool.indexOf(elementNames[i]);
            elementValues[i].resolve(pool);
        }
    }

    /**
     * Writes this instance to the given output stream.
     *
     * @param dos the output stream.
     * @throws IOException if an I/O error occurs.
     */
    public void writeBody(final DataOutputStream dos) throws IOException {
        dos.writeShort(typeIndex);
        dos.writeShort(numPairs);
        for (int i = 0; i < numPairs; i++) {
            dos.writeShort(nameIndexes[i]);
            elementValues[i].writeBody(dos);
        }
    }
}
