package org.apache.commons.compress.harmony.unpack200.bytecode;

public interface IByteCodeForm {
    void setOperandByte(int i, int i1);

    void setOperandBytes(int[] ints);

    int[] getByteCodeTargets();

    int getByteCodeIndex();

    void setOperandSigned2Bytes(int i, int i1);

    void setNestedPositions(int[][] ints);

    void setByteCodeTargets(int[] ints);

    void setOperand2Bytes(int i, int i1);

    void setNested(ClassFileEntry[] nested);
    ClassFileEntry[] getNestedClassFileEntries();

    int[] getRewrite();

    int getOpcode();

    void setRewrite(int[] newRewrite);
}
