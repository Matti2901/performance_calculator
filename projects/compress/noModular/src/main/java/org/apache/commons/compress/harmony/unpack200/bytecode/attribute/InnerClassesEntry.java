package org.apache.commons.compress.harmony.unpack200.bytecode.attribute;

import org.apache.commons.compress.harmony.unpack200.bytecode.CP.CPUTF8;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPClass;
import org.apache.commons.compress.harmony.unpack200.bytecode.ClassConstantPool;

import java.io.DataOutputStream;
import java.io.IOException;

final class InnerClassesEntry {

    CPClass innerClassInfo;
    CPClass outerClassInfo;
    CPUTF8 innerClassName;

    int innerClassInfoIndex = -1;
    int outerClassInfoIndex = -1;
    int innerNameIndex = -1;
    int innerClassAccessFlags = -1;

    InnerClassesEntry(final CPClass innerClass, final CPClass outerClass, final CPUTF8 innerName, final int flags) {
        this.innerClassInfo = innerClass;
        this.outerClassInfo = outerClass;
        this.innerClassName = innerName;
        this.innerClassAccessFlags = flags;
    }

    /**
     * Determine the indices of the things in the receiver which point to elements of the ClassConstantPool
     *
     * @param pool ClassConstantPool which holds the CPClass and CPUTF8 objects.
     */
    public void resolve(final ClassConstantPool pool) {
        if (innerClassInfo != null) {
            innerClassInfo.resolve(pool);
            innerClassInfoIndex = pool.indexOf(innerClassInfo);
        } else {
            innerClassInfoIndex = 0;
        }

        if (innerClassName != null) {
            innerClassName.resolve(pool);
            innerNameIndex = pool.indexOf(innerClassName);
        } else {
            innerNameIndex = 0;
        }

        if (outerClassInfo != null) {
            outerClassInfo.resolve(pool);
            outerClassInfoIndex = pool.indexOf(outerClassInfo);
        } else {
            outerClassInfoIndex = 0;
        }
    }

    public void write(final DataOutputStream dos) throws IOException {
        dos.writeShort(innerClassInfoIndex);
        dos.writeShort(outerClassInfoIndex);
        dos.writeShort(innerNameIndex);
        dos.writeShort(innerClassAccessFlags);
    }

}
